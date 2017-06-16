package com.camas.domain;

//AbstractDomain contains common elements for all domains (aggregates)
public abstract class AbstractDomain {

	String id;					//The id given an instance at creation
	boolean dropped = false;	//Whether or not this instance is no longer active

	public AbstractDomain() {
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public void setDropped() {
		dropped = true;
	}

	public boolean isDropped() {
		return dropped;
	}

}