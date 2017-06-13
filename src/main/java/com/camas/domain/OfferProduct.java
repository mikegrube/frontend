package com.camas.domain;

public class OfferProduct {
	
	String productId;
	int inventory;
	double price;
	
	public OfferProduct(String productId, int inventory, double price) {
		this.productId = productId;
		this.inventory = inventory;
		this.price = price;
	}
	
	public String getProductId() {
		return productId;
	}
	
	public int getInventory() {
		return inventory;
	}
	
	public double getPrice() {
		return price;
	}
}