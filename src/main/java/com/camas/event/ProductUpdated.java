package com.camas.event;

public class ProductUpdated extends AbstractEvent {

	String name;
	String description;

	public ProductUpdated(String id, String name, String description) {
		super("ProductUpdated", id);
		
		this.name = name;
		this.description = description;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}
	
	@Override
	public String toString() {
		return super.toString() + "[name: " + name + ", description" + description + "]";
	}

}
