package com.camas.projector;

import java.util.ArrayList;

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

import java.util.Timer;
import java.util.TimerTask;

import java.util.ArrayList;

public abstract class AbstractProjector extends AbstractActor {
	final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

	ActorRef eventStore;

	int nextOffset = 0;
	int frequency = 5;
	Timer timer;
	String name;

	public AbstractProjector(String name, int nextOffset, int frequency) {
		this.name = name;
		this.nextOffset = nextOffset;
		this.frequency = frequency;
	}

	public static Props props() {
		return Props.create(AbstractProjector.class);
	}

	//Need to be overridden
	abstract Object getTimerCommand();

	@Override
	public void preStart() {
		log.info(name + " started");
		setTimer();
	}

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
			.match(ActorSet.class, this::onActorSet)
			.match(Command.class, this::onCommand)
			.build();
	}

	private void onActorSet(ActorSet set) {
		eventStore = set.getEventStore();
	}
	/*
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
	*/
	//Needs override
	abstract void onCommand(Command cmd);

}
