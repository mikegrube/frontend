package com.camas.message;

import com.camas.event.AbstractEvent;

public class Put {
	
	AbstractEvent event;
	
	public Put(AbstractEvent event) {
		this.event = event;
	}
	
	public AbstractEvent getEvent() {
		return event;
	}
	
}