/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.loercher.localpress.core.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.loercher.localpress.commons.LocalPressProperties;
import de.loercher.localpress.commons.exception.GeneralLocalPressException;
import de.loercher.localpress.commons.exception.UnauthorizedException;
import de.loercher.localpress.geo.AddArticleEntityDTO;
import de.loercher.localpress.geo.LocalPressArticleEntity;
import de.loercher.localpress.transformation.ResponseMapFilterImpl;
import de.loercher.localpress.weighting.WeightingPolicy;
import de.loercher.localpress.weighting.WeightingPolicyImpl;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.AsyncRestTemplate;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

/**
 *
 * @author Jimmy
 */
@RestController
@RequestMapping("localpress/")
public class LocalPressController
{

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final ObjectMapper objectMapper;

    private final Properties prop;
    private final String RATING_URL;
    private final String GEO_URL;

    @Autowired
    public LocalPressController(LocalPressProperties pProperties, ObjectMapper pMapper)
    {
	prop = pProperties.getProp();
	RATING_URL = prop.getProperty("ratingURL");
	GEO_URL = prop.getProperty("geoURL");
	objectMapper = pMapper;
    }

    @RequestMapping(value = "/articles", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> addArticleEntry(@RequestBody LocalPressArticleEntity entity, @RequestHeader HttpHeaders headers) throws UnauthorizedException, GeneralLocalPressException, JsonProcessingException
    {
	AddArticleEntityDTO geoDTO = new AddArticleEntityDTO(entity.getRelease());

	List<String> userHeader = headers.get("UserID");
	if (userHeader == null || userHeader.isEmpty())
	{
	    throw new UnauthorizedException("There needs to be set a UserID-Header!", "", "");
	}

	MultiValueMap<String, String> geoHeaders = new LinkedMultiValueMap<>();
	geoHeaders.add("UserID", userHeader.get(0));

	HttpEntity<AddArticleEntityDTO> request = new HttpEntity<>(geoDTO, geoHeaders);

	RestTemplate template = new RestTemplate();
	String ratingURL = RATING_URL + "feedback";
	ResponseEntity<Map> result;
	try
	{
	    result = template.postForEntity(ratingURL, request, Map.class);
	} catch (RestClientException e)
	{
	    GeneralLocalPressException ex = new GeneralLocalPressException("There happened an error by trying to invoke the geo API!", e);
	    log.error(ex.getLoggingString());
	    throw ex;
	}

	if (!result.getStatusCode().equals(HttpStatus.CREATED))
	{
	    GeneralLocalPressException e = new GeneralLocalPressException(result.getStatusCode().getReasonPhrase());
	    log.error(e.getLoggingString());
	    throw e;
	}

	String articleID = (String) result.getBody().get("articleID");
	if (articleID == null)
	{
	    GeneralLocalPressException e = new GeneralLocalPressException("No articleID found in response from rating module by trying to add new article!");
	    log.error(e.getLoggingString());
	    throw e;
	}

	HttpEntity<LocalPressArticleEntity> second = new HttpEntity<>(entity, geoHeaders);

	template.put(GEO_URL + articleID, second);

	String url = (String) result.getBody().get("feedbackURL");

	Map<String, Object> returnMap = new LinkedHashMap<>();
	returnMap.put("articleID", articleID);
	returnMap.put("userID", userHeader.get(0));

	Timestamp now = new Timestamp(new Date().getTime());
	returnMap.put("timestamp", now);
	returnMap.put("status", 201);
	returnMap.put("message", "Created.");

	returnMap.put("feedbackURL", url);

	return new ResponseEntity<>(objectMapper.writeValueAsString(returnMap), HttpStatus.CREATED);
    }

    // TODO needs to be reworked especially considering readability and therefore maintainability
    @RequestMapping(value = "/articles", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getArticlesAround(@RequestParam Double lat, @RequestParam Double lon) throws InterruptedException, ExecutionException, JsonProcessingException
    {
	UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(GEO_URL)
		.queryParam("lat", lat.toString())
		.queryParam("lon", lon.toString());

	RestTemplate template = new RestTemplate();
	List<Map<String, Object>> geoResponse = template.getForObject(builder.build().encode().toUri(), List.class);

	Iterator<Map<String, Object>> it = geoResponse.iterator();
	List<Future<ResponseEntity<Map>>> jobs = new ArrayList<>();

	// to be able to merge answers from rating to geoitems there is a need 
	// to map the article to its articleID
	// (articleID) => (articleItem)
	Map<String, Map<String, Object>> mappedResponseObjects = new HashMap<>();
	while (it.hasNext())
	{
	    Map<String, Object> item = it.next();

	    AsyncRestTemplate async = new AsyncRestTemplate();
	    Future<ResponseEntity<Map>> futureResult = async.getForEntity((String) item.get("rating"), Map.class);
	    jobs.add(futureResult);
	    mappedResponseObjects.put((String) item.get("articleID"), item);
	}

	for (Future<ResponseEntity<Map>> job : jobs)
	{
	    Map<String, Object> ratingMap = job.get().getBody();
	    String articleID = (String) ratingMap.get("articleID");

	    if ((Boolean) ratingMap.get("appropriate"))
	    {
		mappedResponseObjects.get(articleID).putAll(ratingMap);
	    } else
	    {
		mappedResponseObjects.remove(articleID);
	    }
	}

	WeightingPolicy policy = new WeightingPolicyImpl();
	List<Map<String, Object>> orderedResponse = policy.sortIncludingRating(mappedResponseObjects.values());
	List<Map<String, Object>> result = new ResponseMapFilterImpl().filter(orderedResponse);

	return new ResponseEntity<>(objectMapper.writeValueAsString(result), HttpStatus.OK);
    }

    @RequestMapping(value = "/articles/new", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getNewArticlesAround(@RequestParam Double lat, @RequestParam Double lon) throws InterruptedException, ExecutionException, JsonProcessingException
    {
	UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(GEO_URL)
		.queryParam("lat", lat.toString())
		.queryParam("lon", lon.toString());

	RestTemplate template = new RestTemplate();
	List<Map<String, Object>> geoResponse = template.getForObject(builder.build().encode().toUri(), List.class);

	Iterator<Map<String, Object>> it = geoResponse.iterator();
	List<Future<ResponseEntity<Map>>> jobs = new ArrayList<>();

	// to be able to merge answers from rating to geoitems there is a need 
	// to map the article to its articleID
	// (articleID) => (articleItem)
	Map<String, Map<String, Object>> mappedResponseObjects = new HashMap<>();
	while (it.hasNext())
	{
	    Map<String, Object> item = it.next();

	    AsyncRestTemplate async = new AsyncRestTemplate();
	    Future<ResponseEntity<Map>> futureResult = async.getForEntity((String) item.get("rating"), Map.class);
	    jobs.add(futureResult);
	    mappedResponseObjects.put((String) item.get("articleID"), item);
	}

	for (Future<ResponseEntity<Map>> job : jobs)
	{
	    Map<String, Object> ratingMap = job.get().getBody();
	    String articleID = (String) ratingMap.get("articleID");

	    mappedResponseObjects.get(articleID).putAll(ratingMap);
	}

	WeightingPolicy policy = new WeightingPolicyImpl();
	List<Map<String, Object>> orderedResponse = policy.sortExcludingRating(mappedResponseObjects.values());
	List<Map<String, Object>> result = new ResponseMapFilterImpl().filter(orderedResponse);

	return new ResponseEntity<>(objectMapper.writeValueAsString(result), HttpStatus.OK);
    }

    @RequestMapping(value = "/**/**", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public void defaultRequest()
    {
	throw new IndexOutOfBoundsException("There is no resource on this path.");
    }

}
