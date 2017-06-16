package com.camas.store;

import com.camas.event.AbstractEvent;

import com.camas.message.EventList;
import com.camas.message.Put;
import com.camas.message.Read;

import java.util.ArrayList;

import akka.actor.AbstractActor;
import akka.actor.ActorLogging;
import akka.actor.Props;
import akka.actor.ActorRef;
import akka.event.Logging;
import akka.event.LoggingAdapter;

//The EventStore manages all events
public class EventStore extends AbstractActor {
   private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);
   
   //Fake storage
   static ArrayList<AbstractEvent> store = new ArrayList<>();

   public EventStore() {
      //TODO: Re-establish the previous values in the store if we're going to handle failure
   }

   public static Props props() {
     return Props.create(EventStore.class);
   }

   @Override
   public void preStart() {
     log.info("Event store started");
   }

   @Override
   public void postStop() {
     log.info("Event store stopped");
   }

   @Override
   public Receive createReceive() {
      return receiveBuilder()
         .match(Put.class, this::onPut)
         .match(Read.class, this::onRead)
         .build();
   }
	
   //Store an event
	private void onPut(Put put) {
		//log.info("EventStore saving: " + put.event);
		store.add(put.getEvent());
	}

	//Return a list of events based on filtering by type of event or specific aggregate, from a starting offset
	private void onRead(Read read) {
      ArrayList<AbstractEvent> events = new ArrayList<>();

      for (int i = read.getOffset(); i < store.size(); i++) {
			AbstractEvent event = store.get(i);
			if ((read.getType().equals("ANY") || read.getType().equals(event.getType())) && (read.getKey().equals("ANY") || read.getKey().equals(event.getKey()))) {
 		   		events.add(store.get(i));
			}
      }
     
		getSender().tell(new EventList(read.getType(), read.getKey(), read.getOffset(), events, store.size() - 1), getSelf());
	}
	
}
