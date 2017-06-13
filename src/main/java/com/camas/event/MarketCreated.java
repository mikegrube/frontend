package com.camas.event;

public class MarketCreated extends AbstractEvent {

	String name;

	public MarketCreated(String id, String name) {
		super("MarketCreated", id);
		
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
