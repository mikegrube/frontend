package com.camas.domain;

public abstract class AbstractDomain {

	String id;
	boolean dropped = false;

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