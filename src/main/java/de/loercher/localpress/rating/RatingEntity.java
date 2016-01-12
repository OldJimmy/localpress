/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.loercher.localpress.rating;

/**
 *
 * @author Jimmy
 */
public class RatingEntity
{
    private Boolean appropriate;
    private String release;
    private String articleID;
    private Double rating;

    public Boolean getAppropriate()
    {
	return appropriate;
    }

    public void setAppropriate(Boolean appropriate)
    {
	this.appropriate = appropriate;
    }

    public String getRelease()
    {
	return release;
    }

    public void setRelease(String release)
    {
	this.release = release;
    }

    public String getArticleID()
    {
	return articleID;
    }

    public void setArticleID(String articleID)
    {
	this.articleID = articleID;
    }

    public Double getRating()
    {
	return rating;
    }

    public void setRating(Double rating)
    {
	this.rating = rating;
    }
    
}
