package com.camas.event;

public class MarketDropped extends AbstractEvent {

	public MarketDropped(String id) {
		super("MarketDropped", id);
	}

	@Override
	public String toString() {
		return super.toString();
	}

}
