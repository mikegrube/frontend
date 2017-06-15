package com.camas.frontend;

import java.util.ArrayList;

import com.camas.event.AbstractEvent;
import com.camas.domain.AbstractDomain;

import com.camas.message.ActorSet;
import com.camas.message.AggregateReq;
import com.camas.message.Command;
import com.camas.message.EventList;
import com.camas.message.Read;
import com.camas.message.Status;

import akka.actor.AbstractActor;
import akka.actor.ActorLogging;
import akka.actor.Props;
import akka.actor.ActorRef;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.pattern.Patterns;
import akka.util.Timeout;
import scala.concurrent.duration.Duration;
import java.util.concurrent.TimeUnit;
import scala.concurrent.Await;
import scala.concurrent.Future;

import java.util.ArrayList;

public abstract class AbstractHandler extends AbstractActor {
	final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

	ActorRef eventStore;
	ActorRef buyerRef;
	ActorRef marketRef;
	ActorRef offerRef;
	ActorRef productRef;
	
	int highestId = 0;
	String name;
	String createCommand;

	public AbstractHandler(String name, String createCommand) {
		this.name = name;
		this.createCommand = createCommand;
	}

	public static Props props() {
		return Props.create(AbstractHandler.class);
	}

	@Override
	public void preStart() {
		log.info(name + " started");
	}

	@Override
	public void postStop() {
		log.info(name + " stopped");
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public Receive createReceive() {
		return receiveBuilder()
			.match(ActorSet.class, this::onActorSet)
			.match(Command.class, this::onCommand)
			.match(Status.class, this::onStatus)
			.build();
	}

	private void onActorSet(ActorSet set) {
		eventStore = set.getEventStore();
		buyerRef = set.getBuyerRef();
		marketRef = set.getMarketRef();
		offerRef = set.getOfferRef();
		productRef = set.getProductRef();
		
		populate();
	}

	private void populate() {
		//Re-establish the highest used id
		EventList eventList = (EventList) request(eventStore, new Read(createCommand, "ANY", 0));
		if (eventList != null) {
			ArrayList<AbstractEvent> events = eventList.getEvents();
			for (AbstractEvent event : events) {
				int pid = Integer.parseInt(event.getKey().substring(1));
				if (pid > highestId) {
					highestId = pid;
				}
			}
		}
	}
	
	Object request(ActorRef requestee, Object request) {
		Timeout timeout = new Timeout(Duration.create(5, TimeUnit.SECONDS));
		Future<Object> future = Patterns.ask(requestee, request, timeout);
		Object obj;
		try {
			obj = Await.result(future, timeout.duration());
		} catch (Exception e) {
			obj = null;
		}
		return obj;
	}

	//Needs override
	abstract void onCommand(Command cmd);
	
	//May not do anything
	void onStatus(Status s) {
		
	}

	public void remake(AbstractDomain a, String id) {
		Timeout timeout = new Timeout(Duration.create(5, TimeUnit.SECONDS));
		Future<Object> future = Patterns.ask(eventStore, new Read("ANY", id, 0), timeout); 
		EventList eventList;
		try {
			eventList = (EventList) Await.result(future, timeout.duration());
		} catch (Exception e) {
			System.out.println(e.getMessage());
			eventList = new EventList("", "", 0, new ArrayList<AbstractEvent>(), 0);
		}
		ArrayList<AbstractEvent> events = eventList.getEvents();
		replayEvents(a, events);
	}
	
	private void replayEvents(AbstractDomain a, ArrayList<AbstractEvent> events) {
		for (AbstractEvent event : events) {
			applyEvent(a, event);
		}
	}

	//Needs to be overridden
	abstract void applyEvent(AbstractDomain a, AbstractEvent event);
	
}
