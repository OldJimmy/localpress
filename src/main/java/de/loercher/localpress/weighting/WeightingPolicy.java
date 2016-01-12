/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.loercher.localpress.weighting;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Jimmy
 */
public interface WeightingPolicy
{
    // I deliberately forgo type safety for versioning reasons since a tight coupling by DTO is neither necessary nor advantageous.
    public List<Map<String, Object> > sortIncludingRating(Collection<Map<String, Object> > objects );
    public List<Map<String, Object> > sortExcludingRating(Collection<Map<String, Object> > objects );
}
