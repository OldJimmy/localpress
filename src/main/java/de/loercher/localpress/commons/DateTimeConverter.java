/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.loercher.localpress.commons;

import java.time.ZonedDateTime;

/**
 *
 * @author Jimmy
 */
public class DateTimeConverter
{
    public String toString(ZonedDateTime t)
    {
	return t.toString();
    }

    public ZonedDateTime fromString(String timeString)
    {
	return ZonedDateTime.parse(timeString);
    }
}
