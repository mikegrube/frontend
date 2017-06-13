package com.camas.event;

public class OfferProductRemoved extends AbstractEvent {

	String productId;

	public OfferProductRemoved(String id, String productId) {
		super("OfferProductRemoved", id);
		
		this.productId = productId;
	}

	public String getProductId() {
		return productId;
	}

	@Override
	public String toString() {
		return super.toString() + "[productId: " + productId + "]";
	}

}
