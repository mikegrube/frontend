package com.camas.domain;

//A Buyer is a party that buys Products
public class Buyer extends AbstractDomain {

	String name;

	public Buyer() {
		super();
	}

	// ===== Accessors

	public String getName() {
		return name;
	}

	// ===== Commands

	//Populate a new instance if valid
	public boolean create(String id, String name, boolean replaying) {
		boolean res = true;

		//If we're rebuilding this instance, we don't need to validate
		if (!replaying) {
			if (!nameExists(name)) {
				res = false;
			}
		}

		//Apply the change if valid
		if (res) {
			setId(id);
			this.name = name;
		}

		return res;
	}
	//Update the instance if valid
	public boolean update(String name, boolean replaying) {
		boolean res = true;

		//If we're rebuilding this instance, we don't need to validate
		if (!replaying) {
			if (!nameExists(name)) {
				res = false;
			}
		}

		//Apply the change if valid
		if (res) {
			this.name = name;
		}

		return res;
	}

	//Inactivate the instance if valid
	public boolean drop(boolean replaying) {
		boolean res = true;

		//If we're rebuilding this instance, we don't need to validate
		if (!replaying) {
			//TODO: Figure out why we couldn't drop
			if (isDropped()) {
				res = false;
			}
		}

		//Apply the change if valid
		if (res) {
			setDropped();
		}

		return res;
	}

	//===== Tests

	//Test that a name exists
	private boolean nameExists(String name) {
		return (name != null && name != "");
	}

}