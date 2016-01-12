/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.loercher.localpress.geo;

/**
 *
 * @author Jimmy
 */
public class AddArticleEntityDTO
{
    private String release;

    public AddArticleEntityDTO()
    {
    }

    public AddArticleEntityDTO(String release)
    {
	this.release = release;
    }

    public String getRelease()
    {
	return release;
    }

    public void setRelease(String release)
    {
	this.release = release;
    }
}
