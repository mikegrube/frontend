package com.camas.event;

//The BuyerCreated event represents the successful command to create a new Buyer
public class BuyerCreated extends AbstractEvent {

	String name;

	public BuyerCreated(String id, String name) {
		super("BuyerCreated", id);
		
		this.name = name;
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return super.toString() + "[name: " + name + "]";
	}

}
