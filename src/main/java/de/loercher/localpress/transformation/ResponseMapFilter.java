/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.loercher.localpress.transformation;

import java.util.List;
import java.util.Map;

/**
 *
 * @author Jimmy
 */
public interface ResponseMapFilter
{
    public List<Map<String, Object> > filter(List<Map<String, Object>> input);
    public Map<String, Object>  filter(Map<String, Object> input);
}
