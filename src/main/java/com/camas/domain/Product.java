package com.camas.domain;

import java.util.ArrayList;
import java.io.IOException;

/* Product
* This class represnts the aggregate for product
*/
public class Product extends AbstractDomain {

	String name;
	String description;
	int inventory = 0;
	double price = 0.0;

	public Product() {}

	//===== Accessors

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public int getInventory() {
		return inventory;
	}

	public double getPrice() {
		return price;
	}

	//===== Commands

	public boolean create(String id, String name, String description, boolean replaying) {
		boolean res = true;

		if (!replaying) {
			if (!nameExists(name) || !descriptionExists(description)) {
				res = false;
			}
		}

		if (res) {
			this.name = name;
			this.description = description;
		}

		return res;
	}

	public boolean update(String name, String description, boolean replaying) {
		boolean res = true;

		if (!replaying) {
			if (!nameExists(name) || !descriptionExists(description)) {
				res = false;
			}
			if (isDropped()) {
				res = false;
			}
		}

		if (res) {
			this.name = name;
			this.description = description;
		}

		return res;
	}

	public boolean adjustInventory(int amount, boolean replaying) {
		boolean res = true;

		if (!replaying) {
			if (!sufficientInventoryExists(amount)) {
				res = false;
			}
			if (isDropped()) {
				res = false;
			}
		}

		if (res) {
			inventory += amount;
		}

		return res;
	}

	public boolean updatePrice(double amount, boolean replaying) {
		boolean res = true;

		if (!replaying) {
			if (!priceIsPositive(amount)) {
				res = false;
			}
			if (isDropped()) {
				res = false;
			}
		}

		if (res) {
			price = amount;
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

	//===== Tests

	private boolean nameExists(String name) {
		return (name != null && name != "");
	}

	private boolean descriptionExists(String description) {
		return (description != null && description != "");
	}

	private boolean sufficientInventoryExists(int amount) {
		return ((inventory + amount) >= 0);
	}

	private boolean priceIsPositive(double amount) {
		return (amount > 0.0);
	}

}