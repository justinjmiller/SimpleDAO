package org.simpledao.lookuplist;

/**
 * <p/>
 * User: jumiller
 * Date: Aug 23, 2006
 * Time: 12:38:41 PM
 * </p>
 */
public class LookupListBean
{
	// ----------------------------------------------------- Instance Variables
	private String description = null;
	private int id ;

	// ----------------------------------------------------- Constructors
	public LookupListBean(int id, String name)
	{
		this.id = id;
		this.description = name;
	}

	public LookupListBean() {}

	// ----------------------------------------------------- Getters/Setters
	public int getId()
	{
		return id;
	}

	public void setId(int id)
	{
		this.id = id;
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}}
