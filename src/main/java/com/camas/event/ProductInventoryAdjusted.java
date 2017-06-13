package com.camas.event;

public class ProductInventoryAdjusted extends AbstractEvent {

	String amount;

	public ProductInventoryAdjusted(String id, String amount) {
		super("ProductInventoryAdjusted", id);
		
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
