package com.camas.message;

import akka.actor.ActorRef;

public class ActorSet {
	ActorRef buyerRef;
	ActorRef marketRef;
	ActorRef offerRef;
	ActorRef productRef;
	ActorRef eventStore;
	
	public ActorSet(ActorRef buyerRef, ActorRef marketRef, ActorRef offerRef, ActorRef productRef, ActorRef eventStore) {
		this.buyerRef = buyerRef;
		this.marketRef = marketRef;
		this.offerRef = offerRef;
		this.productRef = productRef;
		this.eventStore = eventStore;
	}
	
	public ActorRef getBuyerRef() {
		return buyerRef;
	}

	public ActorRef getMarketRef() {
		return marketRef;
	}

	public ActorRef getOfferRef() {
		return offerRef;
	}

	public ActorRef getProductRef() {
		return productRef;
	}
	
	public ActorRef getEventStore() {
		return eventStore;
	}

}