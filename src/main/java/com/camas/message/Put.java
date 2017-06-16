package com.camas.message;

import com.camas.event.AbstractEvent;

//The Put message carries a new event to the store
public class Put {
	
	AbstractEvent event;
	
	public Put(AbstractEvent event) {
		this.event = event;
	}
	
	public AbstractEvent getEvent() {
		return event;
	}
	
}