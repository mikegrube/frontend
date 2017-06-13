package com.camas.event;

public class OfferUpdated extends AbstractEvent {

	String title;

	public OfferUpdated(String id, String title) {
		super("OfferUpdated", id);
		
		this.title = title;
	}

	public String getTitle() {
		return title;
	}

	@Override
	public String toString() {
		return super.toString() + "[title: " + title +  "]";
	}

}
