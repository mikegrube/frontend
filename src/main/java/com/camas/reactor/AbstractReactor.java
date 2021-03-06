package com.camas.reactor;

import java.util.ArrayList;

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

import java.util.Timer;
import java.util.TimerTask;

import java.util.ArrayList;

//Elements common to all reactors
public abstract class AbstractReactor extends AbstractActor {
	final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

	public static final Timeout TIMEOUT = new Timeout(100, TimeUnit.MILLISECONDS);

	ActorRef eventStore;

	int nextOffset = 0;			//Next offset to be read from the store
	int frequency = 5;			//Frequency in seconds between requests
	Timer timer;				//Timer that schedules requests to get events
	String name;				//Name of this reactor

	public AbstractReactor(String name, int nextOffset, int frequency) {
		this.name = name;
		this.nextOffset = nextOffset;
		this.frequency = frequency;
	}

	public static Props props() {
		return Props.create(AbstractReactor.class);
	}

	//Need to be overridden
	abstract Object getTimerCommand();

	@Override
	public void preStart() {
		log.info(name + " started");
		eventStore = getSingleActorRefFromPath(getContext().getSystem(), "/user/command-handler/eventstore-01");
		setTimer();
	}

	//Set up a scheduled timer to keep up to date on events
	private void setTimer() {
		timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {

				Timeout timeout = new Timeout(Duration.create(5, TimeUnit.SECONDS));
				Future<Object> future = Patterns.ask(eventStore, getTimerCommand(), timeout);
				Object obj = null;
				try {
					obj = Await.result(future, timeout.duration());
				} catch (Exception e) {
					System.out.println(e.getMessage());
				}
				timerPosting(obj);
			}
		}, frequency * 1000, frequency * 1000);
	}

	//Processing that should occur when events are returned
	//Needs to be overridden
	abstract void timerPosting(Object obj);

	@Override
	public void postStop() {
		timer.cancel();
		log.info(name + " stopped");
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public Receive createReceive() {
		return receiveBuilder()
			.build();
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

}
