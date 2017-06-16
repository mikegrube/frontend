package com.camas.message;

//The Read message carries the request for a filtered list of events to the store
public class Read {

	String type;
	String key;
	int offset;
	
	public Read(String type, String key, int offset) {
		this.type = type;
		this.key = key;
		this.offset = offset;
	}
	
	public String getType() {
		return type;
	}
	
	public String getKey() {
		return key;
	}
	
	public int getOffset() {
		return offset;
	}
	
}