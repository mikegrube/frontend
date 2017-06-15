package com.camas.message;

import java.util.ArrayList;
import com.camas.event.AbstractEvent;

public class EventList {
	
	String type;
	String id;
	int offset;
	ArrayList<AbstractEvent> events;
	int lastOffsetRead;
	
	public EventList(String type, String id, int offset, ArrayList<AbstractEvent> events, int lastOffsetRead) {
		this.type = type;
		this.id = id;
		this.offset = offset;
		this.events = events;
		this.lastOffsetRead =lastOffsetRead;
	}
	
	public String getType() {
		return type;
	}

	public String getId() {
		return id;
	}
	
	public int getOffset() {
		return offset;
	}

	public ArrayList<AbstractEvent> getEvents() {
		return events;
	}
	
	public int getLastOffsetRead() {
		return lastOffsetRead;
	}
	
}