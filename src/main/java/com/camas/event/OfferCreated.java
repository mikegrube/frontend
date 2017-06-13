package com.camas.event;

public class OfferCreated extends AbstractEvent {

	String marketId;
	String title;

	public OfferCreated(String id, String marketId, String title) {
		super("OfferCreated", id);
		
		this.marketId = marketId;
		this.title = title;
	}

	public String getMarketId() {
		return marketId;
	}

	public String getTitle() {
		return title;
	}
	
	@Override
	public String toString() {
		return super.toString() + "[marketId: " + marketId + ", title" + title + "]";
	}

}
