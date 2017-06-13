package com.camas.event;

/* AbstractEvent
* Type is the actual event type String
* Key is a one-letter prefix followed by the aggregate's id
* Paylod is the JSON representation of the pertinent information
*/
abstract public class AbstractEvent {

	String type;
	String key;

	public AbstractEvent() {}

	public AbstractEvent(String type, String key) {
		this.type = type;
		this.key = key;
	}

	public String getType() {
		return type;
	}

	public String getKey() {
		return key;
	}

	public String getAggregateType() {
		return key.substring(0,1);
	}

	public Integer getAggregateId() {
		return Integer.parseInt(key.substring(1));
	}

	public String toString() {
		return "Event: " + type + ", " + key + " - ";
	}
	
}
