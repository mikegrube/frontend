package com.camas.domain;

import java.util.ArrayList;
import java.io.IOException;

/* Offer
* This class represnts the aggregate for offer
*/
public class Offer  extends AbstractDomain {

	String marketId;
	String title;
	ArrayList<OfferProduct> products = new ArrayList<OfferProduct>();

	public Offer() {
		super();
	}

	//===== Accessors

	public String getMarketId() {
		return marketId;
	}

	public String getTitle() {
		return title;
	}

	public ArrayList<OfferProduct> getProducts() {
		return products;
	}
	
	public OfferProduct getOfferProduct(String productId) {
		OfferProduct p = null;
		
		for (OfferProduct op : products) {
			if (op.getProductId().equals(productId)) {
				p = op;
			}
		}
		return p;
	}

	//===== Commands

	public boolean create(String id, String marketId, String title, boolean replaying) {
		boolean res = true;

		if (!replaying) {
			if (!titleExists(title)) {
				res = false;
			}
		}

		if (res) {
			this.marketId = marketId;
			this.title = title;
		}

		return res;
	}

	public boolean update(String title, boolean replaying) {
		boolean res = true;

		if (!replaying) {
			if (!titleExists(title)) {
				res = false;
			}
			if (isDropped()) {
				res = false;
			}
		}

		if (res) {
			this.title = title;
		}

		return res;
	}

	public boolean addProduct(String productId, int inventory, double price, boolean replaying) {
		boolean res = true;

		if (!replaying) {
			if (hasProduct(productId)) {
				res = false;
			}
		}

		if (res) {
			products.add(new OfferProduct(productId, inventory, price));
		}

		return res;
	}

	public boolean removeProduct(String productId, boolean replaying) {
		boolean res = true;

		if (!replaying) {
			if (!hasProduct(productId)) {
				res = false;
			}
		}

		if (res) {
			for (OfferProduct op : products) {
				if (op.getProductId().equals(productId)) {
					products.remove(op);
				}
			}
		}

		return res;
	}

	public boolean adjustProductInventory(String productId, int amount, boolean replaying) {
		boolean res = true;

		OfferProduct op = findOfferProduct(productId);
		if (op == null) {
			res = false;
			System.out.println("Product " + productId + " not found in offer " + id);
		}

		if (!replaying) {
			if (!sufficientInventoryExists(op, amount)) {
				res = false;
			}
		}

		if (res) {
			op.inventory += amount;
		}

		return res;
	}

	public boolean updateProductPrice(String productId, double amount, boolean replaying) {
		boolean res = true;

		OfferProduct op = findOfferProduct(productId);
		if (op == null) {
			res = false;
			System.out.println("Product " + productId + " not found in offer " + id);
		}

		if (!replaying) {
			if (!priceIsPositive(amount)) {
				res = false;
			}
		}

		if (res) {
			op.price = amount;
			res = true;
		}

	return res;
	}

	public boolean drop(boolean replaying) {
		boolean res = true;

		if (!replaying) {
			//TODO: Figure out why we couldn't drop
			if (isDropped()) {
				res = false;
			}
		}

		if (res) {
			dropped = true;
		}

	return res;
	}

	private OfferProduct findOfferProduct(String productId) {
		for (OfferProduct op : products) {
			if (op.getProductId().equals(productId)) {
				return op;
			}
		}
		return null;
	}

	//===== Tests

	private boolean hasProduct(String productId) {
		for (OfferProduct op : products) {
			if (op.getProductId().equals(productId)) {
				return true;
			}
		}
		return false;
	}

	private boolean titleExists(String title) {
		return (title != null && title != "");
	}

	private boolean sufficientInventoryExists(OfferProduct op, int amount) {
		return ((op.inventory + amount) >= 0);
	}

	private boolean priceIsPositive(double amount) {
		return (amount > 0.0);
	}

}

