/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.loercher.localpress.weighting;

import de.loercher.localpress.commons.DateTimeConverter;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 *
 * @author Jimmy
 */
public class WeightingPolicyImpl implements WeightingPolicy
{
    /*
     Example calculation: in 72 hours (3 days) the rating goes down 50% with the following parameters:
     SINKINGTIMEINHOURS = 72.0
     SINKINGFACTOR = 0.5
     */
    private static final Double SINKINGTIMEINHOURS = 72.0;
    private static final Double SINKINGFACTOR = 0.5;
    
    
    @Override
    public List<Map<String, Object>> sortIncludingRating(Collection<Map<String, Object>> objects)
    {
	Map< Double, Map<String, Object>> orderMap = new TreeMap<>(Collections.reverseOrder());

	for (Map<String, Object> item : objects)
	{
	    Double factor = (Double) item.get("factor");
	    Double rating = (Double) item.get("rating");

	    Double effective = factor * rating;

	    orderMap.put(effective, item);
	}

	List<Map<String, Object>> result = new ArrayList<>();

	for (Double index : orderMap.keySet())
	{
	    result.add(orderMap.get(index));
	}

	return result;
    }

    @Override
    public List<Map<String, Object>> sortExcludingRating(Collection<Map<String, Object>> objects)
    {
	Map< Double, Map<String, Object>> orderMap = new TreeMap<>(Collections.reverseOrder());

	for (Map<String, Object> item : objects)
	{
	    Double factor = (Double) item.get("factor");
	    ZonedDateTime time = new DateTimeConverter().fromString((String) item.get("release"));

	    Duration timeDifference = Duration.between(time, ZonedDateTime.now());

	    Double potency = (double) timeDifference.toMinutes() / 60;

	    potency = potency / SINKINGTIMEINHOURS;

	    Double timeFactor = Math.pow(SINKINGFACTOR, potency);

	    Double effective = factor * timeFactor;

	    orderMap.put(effective, item);
	}

	List<Map<String, Object>> result = new ArrayList<>();

	for (Double index : orderMap.keySet())
	{
	    result.add(orderMap.get(index));
	}

	return result;
    }

}
