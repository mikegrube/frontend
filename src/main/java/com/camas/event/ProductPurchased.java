package com.camas.event;

public class ProductPurchased extends AbstractEvent {

	String offerId;
	String productId;
	String quantity;
	String price;
	String buyerId;

	public ProductPurchased(String id, String productId, String quantity, String price, String buyerId) {
		super("ProductPurchased", id);
		
		this.productId = productId;
		this.quantity = quantity;
		this.price = price;
		this.buyerId = buyerId;
	}

	public String getProductId() {
		return productId;
	}

	public String getOfferId() {
		return productId;
	}

	public String getQuantity() {
		return quantity;
	}

	public String getPrice() {
		return price;
	}
	
	public String getBuyerId() {
		return buyerId;
	}

	@Override
	public String toString() {
		return super.toString() + "[productId:" + productId + ", quantity:" + quantity + ", price:" + price + ", buyerId:" + buyerId + "]";
	}

}
