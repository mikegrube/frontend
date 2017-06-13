package com.camas.domain;

public class Market extends AbstractDomain {

	String name;

	public Market() {
		super();
	}

	// ===== Accessors

	public String getName() {
		return name;
	}

	// ===== Commands

	public boolean create(String id, String name, boolean replaying) {
		boolean res = true;

		if (!replaying) {
			if (!nameExists(name)) {
				res = false;
			}
		}

		if (res) {
			this.name = name;
		}

		return res;
	}

	public boolean update(String name, boolean replaying) {
		boolean res = true;

		if (!replaying) {
			if (!nameExists(name)) {
				res = false;
			}
		}

		if (res) {
			this.name = name;
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

}