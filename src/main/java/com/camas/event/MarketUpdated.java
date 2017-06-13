package com.camas.event;

public class MarketUpdated extends AbstractEvent {

	String name;

	public MarketUpdated(String id, String name) {
		super("MarketUpdated", id);
		
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
