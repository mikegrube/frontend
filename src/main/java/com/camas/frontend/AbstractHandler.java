package com.camas.frontend;

import java.util.ArrayList;

import com.camas.event.AbstractEvent;
import com.camas.domain.AbstractDomain;

import com.camas.message.AggregateReq;
import com.camas.message.Command;
import com.camas.message.EventList;
import com.camas.message.Read;
import com.camas.message.Status;

import akka.actor.AbstractActor;
import akka.actor.ActorLogging;
import akka.actor.Props;
import akka.actor.ActorRef;
import akka.actor.ActorNotFound;
import akka.actor.ActorSelection;
import akka.actor.ActorSystem;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.pattern.Patterns;
import akka.util.Timeout;
import scala.concurrent.duration.Duration;
import java.util.concurrent.TimeUnit;
import scala.concurrent.Await;
import scala.concurrent.Future;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

//AbstractHandler contains functions common to all command handlers
public abstract class AbstractHandler extends AbstractActor {
	final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);
	
	public static final Timeout TIMEOUT = new Timeout(100, TimeUnit.MILLISECONDS);

	ActorRef eventStore;
	
	int highestId = 0;			//This maintains the highest id used for the specific aggregate
	String name;				//The name of the command handler
	String createCommand;		//The name of the command used to create an instance of the aggregate (for finding highest id)

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
		eventStore = getSingleActorRefFromPath(getContext().getSystem(), "/user/command-handler/eventstore-01");
		populate();
	}

	@Override
	public void postStop() {
		log.info(name + " stopped");
	}

	public void setName(String name) {
		this.name = name;
	}

	//This actor accepts ActorSet for setup, Command for commands, and Status if a sibling needs to know about an instance
	@Override
	public Receive createReceive() {
		return receiveBuilder()
			.match(Command.class, this::onCommand)
			.match(Status.class, this::onStatus)
			.build();
	}

	//Re-establish the highest used id
	private void populate() {
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
	
	//Find an actor ref by path
    ActorRef getSingleActorRefFromPath(ActorSystem system, String path) {
 
        try {
            // create an ActorSelection based on the path
            ActorSelection sel = system.actorSelection(path);
            // check if a single actor exists at the path
            Future<ActorRef> fut = sel.resolveOne(TIMEOUT);
            ActorRef ref = Await.result(fut, TIMEOUT.duration());
            return ref;
        } catch (ActorNotFound e) {
            return null;
        } catch (Exception e) {
        	return null;
        }
    }

	//A synchronous request to pick up info from a sibling
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

	//What to do for each command
	//Needs override
	abstract void onCommand(Command cmd);
	
	//Needed if another sibling requires info about an aggregate
	//May not do anything
	void onStatus(Status s) {
		
	}

	//Given an aggregate id, reconstruct it from past events, using a synchronous call to the event store
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
	
	//Replay a list of events against an aggregate
	private void replayEvents(AbstractDomain a, ArrayList<AbstractEvent> events) {
		for (AbstractEvent event : events) {
			applyEvent(a, event);
		}
	}

	//For a given event, apply it to the aggregate
	//Needs to be overridden
	abstract void applyEvent(AbstractDomain a, AbstractEvent event);
	
}
