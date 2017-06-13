package com.camas.event;

public class ProductPriceUpdated extends AbstractEvent {

	String amount;

	public ProductPriceUpdated(String id, String amount) {
		super("ProductPriceUpdated", id);
		
		this.amount = amount;
	}

	public String getAmount() {
		return amount;
	}

	@Override
	public String toString() {
		return super.toString() + "[amount: " + amount + "]";
	}

}
