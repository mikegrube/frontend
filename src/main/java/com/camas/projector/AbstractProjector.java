package com.camas.projector;

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

//AbstractProjector contains elements common to projectors
public abstract class AbstractProjector extends AbstractActor {
	final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

	public static final Timeout TIMEOUT = new Timeout(100, TimeUnit.MILLISECONDS);

	ActorRef eventStore;

	int nextOffset = 0;			//The next offset not yet read by this projector
	int frequency = 5;			//The frequency in seconds for requesting updates
	Timer timer;				//The timer that schedules update requests
	String name;				//The nme of this projector

	public AbstractProjector(String name, int nextOffset, int frequency) {
		this.name = name;
		this.nextOffset = nextOffset;
		this.frequency = frequency;
	}

	public static Props props() {
		return Props.create(AbstractProjector.class);
	}

	//The request to be made when the timer fires
	//Needs to be overridden
	abstract Object getTimerCommand();

	@Override
	public void preStart() {
		log.info(name + " started");
		eventStore = getSingleActorRefFromPath(getContext().getSystem(), "/user/command-handler/eventstore-01");
		setTimer();
	}

	//Set up the timer to make update requests to the store
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

	//Process the returned events after a timer request
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
			.match(Command.class, this::onCommand)
			.build();
	}

	//Needs override
	abstract void onCommand(Command cmd);

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
