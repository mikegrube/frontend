package com.camas.event;

public class OfferProductPriceUpdated extends AbstractEvent {

	String productId;
	String amount;

	public OfferProductPriceUpdated(String id, String productId, String amount) {
		super("OfferProductPriceUpdated", id);
		
		this.productId = productId;
		this.amount = amount;
	}

	public String getProductId() {
		return productId;
	}

	public String getAmount() {
		return amount;
	}

	@Override
	public String toString() {
		return super.toString() + "[productId: " + productId + ", amount:" + amount + "]";
	}

}
