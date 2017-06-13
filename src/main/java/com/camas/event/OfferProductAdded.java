package com.camas.event;

public class OfferProductAdded extends AbstractEvent {

	String productId;
	String inventory;
	String price;

	public OfferProductAdded(String id, String productId, String inventory, String price) {
		super("OfferProductAdded", id);
		
		this.productId = productId;
		this.inventory = inventory;
		this.price = price;
	}

	public String getProductId() {
		return productId;
	}

	public String getInventory() {
		return inventory;
	}

	public String getPrice() {
		return price;
	}

	@Override
	public String toString() {
		return super.toString() + "[productId: " + productId + ", inventory:" + inventory + ", price:" + price + "]";
	}

}
