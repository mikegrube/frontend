package com.camas.event;

public class ProductCreated extends AbstractEvent {

	String name;
	String description;

	public ProductCreated(String id, String name, String description) {
		super("ProductCreated", id);
		
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
