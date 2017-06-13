package com.camas.event;

public class ProductDropped extends AbstractEvent {

	public ProductDropped(String id) {
		super("ProductDropped", id);
	}

	@Override
	public String toString() {
		return super.toString();
	}

}
