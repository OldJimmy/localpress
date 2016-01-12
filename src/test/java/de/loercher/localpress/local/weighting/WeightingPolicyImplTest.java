/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.loercher.localpress.local.weighting;

import de.loercher.localpress.weighting.WeightingPolicyImpl;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Jimmy
 */
public class WeightingPolicyImplTest
{

    private WeightingPolicyImpl policy = new WeightingPolicyImpl();

    private Map<String, Object> first;
    private Map<String, Object> second;
    private Map<String, Object> third;

    private List<Map<String, Object>> liste;

    public WeightingPolicyImplTest()
    {
    }

    @Before
    public void setUp()
    {
	first = new HashMap<>();
	second = new HashMap<>();
	third = new HashMap<>();

	first.put("factor", 1.0);
	first.put("rating", 1.0);

	second.put("factor", 2.0);
	second.put("rating", 2.5);

	third.put("factor", 1.0);
	third.put("rating", 4.0);

	liste = new ArrayList<>();
    }

    @Test
    public void testWeightIncluding()
    {

//	    Double factor = (Double) item.get("factor");
//	    Double rating = (Double) item.get("rating");
	liste.add(first);

	liste.add(second);
	
	liste.add(third);
	
	List<Map<String,Object> > result = policy.sortIncludingRating(liste);
	
	assertEquals("The second inserted item should be the one listed first!", second, result.get(0));
	assertEquals("The third inserted item should be the one listed second!", third, result.get(1));
	assertEquals("The first inserted item should be the one listed third!", first, result.get(2));
    }

}
