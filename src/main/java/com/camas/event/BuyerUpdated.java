package com.camas.event;

//The BuyerUpdated event represents the successful command to update an existing Buyer
public class BuyerUpdated extends AbstractEvent {

	String name;

	public BuyerUpdated(String id, String name) {
		super("BuyerUpdated", id);
		
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
