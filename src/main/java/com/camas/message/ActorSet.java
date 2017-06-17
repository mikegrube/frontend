package com.camas.message;

import akka.actor.ActorRef;

import java.util.HashMap;
import java.util.Set;

public class ActorSet {

	HashMap<String, ActorRef> actorRefs = new HashMap<>();
	
	public ActorSet() {
	}
	
	public void addActorRef(String name, ActorRef actorRef) {
		actorRefs.put(name, actorRef);
	}

	public ActorRef getActorRef(String name) {
		return actorRefs.get(name);
	}
	
	public Set<String> getKeys() {
		return actorRefs.keySet();
	}

}