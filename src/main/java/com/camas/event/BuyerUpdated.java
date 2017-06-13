package com.camas.event;

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
