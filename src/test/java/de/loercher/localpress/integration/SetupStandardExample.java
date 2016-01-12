/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.loercher.localpress.integration;

import de.loercher.localpress.commons.Coordinate;
import de.loercher.localpress.commons.DateTimeConverter;
import de.loercher.localpress.geo.AddArticleEntityDTO;
import de.loercher.localpress.geo.GeoBaseEntity;
import de.loercher.localpress.geo.LocalPressArticleEntity;
import java.time.Duration;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

/**
 *
 * @author Jimmy
 */
public class SetupStandardExample
{
    
    public SetupStandardExample()
    {
    }
    
    @Before
    public void setUp()
    {
    }
    
    @Test
    public void testAddArticle()
    {
	String nowString = "2015-10-12T08:00+02:00[Europe/Berlin]";
	ZonedDateTime now = new DateTimeConverter().fromString(nowString);

	AddArticleEntityDTO geoDTO = new AddArticleEntityDTO(nowString);
	Instant instant = now.toInstant();
	Instant fir = Instant.now();

	MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
	headers.add("UserID", "ulf");

	HttpEntity<AddArticleEntityDTO> request = new HttpEntity<>(geoDTO, headers);

	RestTemplate template = new RestTemplate();
	ResponseEntity<Map> result = template.postForEntity("http://52.29.77.191:8080/localpress/feedback", request, Map.class);
	
	Instant afterRating = Instant.now();
	
	GeoBaseEntity.EntityBuilder builder = new GeoBaseEntity.EntityBuilder();
	GeoBaseEntity entity = builder.author("ulf")
		.coordinates(Arrays.asList(new Coordinate[]
				{
				    new Coordinate(50.1, 8.4)
		}))
		.timestamp(now.toEpochSecond())
		.content("abc.de")
		.title("mein titel")
		.user("ulf")
		.build();

	HttpEntity<GeoBaseEntity> second = new HttpEntity<>(entity, headers);
	System.out.println(result.getBody().get("articleID"));

	template.put("http://euve33985.vserver.de:8080/geo/" + result.getBody().get("articleID"), second);
	
	Instant afterGeoPut = Instant.now();

	result = template.getForEntity("http://euve33985.vserver.de:8080/geo/" + result.getBody().get("articleID"), Map.class);
	
	Instant afterGeoGet = Instant.now();

	assertEquals("User ID has changed over time!", "ulf", result.getBody().get("user"));
	assertEquals("Content URL has changed over time!", "abc.de", result.getBody().get("content"));
	
	DateTimeConverter conv = new DateTimeConverter();
	
	Duration first = Duration.between(fir, afterRating);
	Duration sec = Duration.between(afterRating, afterGeoPut);
	Duration third = Duration.between(afterGeoPut, afterGeoGet);
	
	System.out.println("Begin: " + conv.toString(now));
	System.out.println("Time until POST to rating: " + new Double(first.toMillis())/1000 );
	System.out.println("Time until PUT to geo: " + new Double(sec.toMillis())/1000);
	System.out.println("Time until GET to geo: " + new Double(third.toMillis())/1000);
    }
}
