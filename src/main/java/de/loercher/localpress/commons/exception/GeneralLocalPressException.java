/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.loercher.localpress.commons.exception;

import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;

/**
 *
 * @author Jimmy
 */
public class GeneralLocalPressException extends Exception
{
    protected String uuid;
    protected Date time;
    protected String error;

    public GeneralLocalPressException(String pError)
    {
	uuid = UUID.randomUUID().toString();
	time = new Date();
	error = pError;
    }
    
    /**
     * Create a GeneralGeoModuleException with a string and the throwable. The constructor will 
     * generate a UUID by which you can match the log entry to the exception thrown by using 
     * getLoggingString() on the debugger.
     * 
     * @param pError
     * @param e 
     */
    public GeneralLocalPressException(String pError, Throwable e)
    {
	super(e);

	uuid = UUID.randomUUID().toString();
	time = new Date();
	error = pError;
    }

    public String getLoggingString()
    {
	Timestamp stamp = new Timestamp(time.getTime());
	return stamp + ": " + uuid + ": " + error;
    }

    public String getUuid()
    {
	return uuid;
    }

    public void setUuid(String uuid)
    {
	this.uuid = uuid;
    }

    public Date getTime()
    {
	return time;
    }

    public void setTime(Date time)
    {
	this.time = time;
    }

    public String getError()
    {
	return error;
    }

    public void setError(String error)
    {
	this.error = error;
    }

}
