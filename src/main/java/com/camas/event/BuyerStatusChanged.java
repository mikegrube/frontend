package com.camas.event;

//The BuyerStatusChanged event represents the successful change of a Buyer's status
public class BuyerStatusChanged extends AbstractEvent {

	String status;

	public BuyerStatusChanged(String id, String status) {
		super("BuyerStatusChanged", id);
		
		this.status = status;
	}

	public String getStatus() {
		return status;
	}

	@Override
	public String toString() {
		return super.toString() + "[status: " + status + "]";
	}

}
