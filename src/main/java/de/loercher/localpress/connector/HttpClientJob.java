/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.loercher.localpress.connector;

import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.methods.GetMethod;
import org.springframework.web.client.RestTemplate;

/**
 *
 * @author Jimmy
 */
public class HttpClientJob implements Runnable
{

    private Map<String, Object> result = null;
    private final String url;
    RestTemplate template = new RestTemplate();


    public String getUrl()
    {
	return url;
    }
    public HttpClientJob(String pUrl)
    {
	url = pUrl;
    }

    @Override
    public void run()
    {
	try
	{
	    MultiThreadedHttpConnectionManager connectionManager = new MultiThreadedHttpConnectionManager();
	    HttpClient client = new HttpClient(connectionManager);
	    HttpMethod method = new GetMethod(url);
	    client.executeMethod(method);
	    
	    System.out.println(new String(method.getResponseBody()));
	    
	} catch (IOException ex)
	{
	    Logger.getLogger(HttpClientJob.class.getName()).log(Level.SEVERE, null, ex);
	} 
    }

    public Map<String, Object> getResult()
    {
	return result;
    }
}
