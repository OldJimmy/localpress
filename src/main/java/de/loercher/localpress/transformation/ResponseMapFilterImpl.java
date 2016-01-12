/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.loercher.localpress.transformation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Jimmy
 */
public class ResponseMapFilterImpl implements ResponseMapFilter
{

    @Override
    public Map<String, Object> filter(Map<String, Object> input)
    {
	List<String> toFilterKeys = Arrays.asList(new String[]{"path", "factor", "rating", "appropriate"});
	
	Map<String, Object> result = new HashMap<>();
	for (Map.Entry<String, Object> item : input.entrySet())
	{
	    if (!toFilterKeys.contains(item.getKey()))
	    {
		result.put(item.getKey(), item.getValue());
	    }
	}
	
	return result;
    }

    @Override
    public List<Map<String, Object>> filter(List<Map<String, Object>> input)
    {
	List<Map<String, Object>> result = new ArrayList<>();
	
	for (Map<String, Object> item : input)
	{
	    result.add(filter(item));
	}
	
	return result;
    }
    
}
