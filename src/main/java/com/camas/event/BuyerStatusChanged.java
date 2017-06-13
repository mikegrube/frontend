package com.camas.event;

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
