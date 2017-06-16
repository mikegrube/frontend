package com.camas.event;

//The BuyerDropped event represents the successful command to drop an existing Buyer
public class BuyerDropped extends AbstractEvent {

	public BuyerDropped(String id) {
		super("BuyerDropped", id);
	}

	@Override
	public String toString() {
		return super.toString();
	}

}
