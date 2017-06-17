package com.camas.frontend;

import java.util.ArrayList;

import com.camas.domain.Market;
import com.camas.domain.AbstractDomain;

import com.camas.event.AbstractEvent;
import com.camas.event.MarketCreated;
import com.camas.event.MarketUpdated;
import com.camas.event.MarketDropped;

import com.camas.message.AggregateReq;
import com.camas.message.Put;
import com.camas.message.Status;
import com.camas.message.Command;
import com.camas.message.Read;
import com.camas.message.EventList;

import akka.actor.Props;
import akka.actor.ActorRef;
import akka.pattern.Patterns;
import akka.util.Timeout;
import scala.concurrent.duration.Duration;
import java.util.concurrent.TimeUnit;
import scala.concurrent.Await;
import scala.concurrent.Future;

import java.util.ArrayList;

public class MarketHandler extends AbstractHandler {

	private static String keyPrefix = "M";

	private int highestId = 0;

	public MarketHandler() {
		super("MarketHandler", "CreateMarket");
	}

	public static Props props() {
		return Props.create(MarketHandler.class);
	}

	@Override
	void onCommand(Command cmd) {

		boolean res;

  	  String[] args = cmd.getArgs();
     
		switch (cmd.getCommand()) {
			case "CreateMarket":
				onCreate(keyPrefix + ++highestId, args[0]);
				break;
			case "UpdateMarket":
				onUpdate(args[0], args[1]);
				break;
			case "DropMarket":
				onDrop(args[0]);
				break;
			case "ShowMarket":
				onShow(args[0]);
				break;
			default:
				break;
		}

	}

	private void onCreate(String id, String name) {

		Market a = new Market();
		boolean res = a.create(id, name, false);

		if (res && nameIsUnique(name)) {
			actorRefs.get("eventStore").tell(new Put(new MarketCreated(id, name)), getSelf());
		} else {
			log.warning("Unable to create buyer " + id);
		}
	}
	// CreateMarket,Amazon

	private boolean nameIsUnique(String name) {
		//TODO: How would we do this?
		return true;
	}

	private void onUpdate(String id, String name) {
		Market a = new Market();
		remake(a, id);
		boolean res = a.update(name, false);
		if (res && nameIsUnique(name)) {
			actorRefs.get("eventStore").tell(new Put(new MarketUpdated(id, name)), getSelf());
		} else {
			log.warning("Unable to update buyer " + id);
		}
	}
	// UpdateMarket,M1,Amazon Plus

	private void onShow(String id) {
		Market a = new Market();
		remake(a, id);
		log.info("Market " + id + ": name:" + a.getName());
	}
	//ShowMarket,M1

	private void onDrop(String id) {
		Market a = new Market();
		remake(a, id);
		boolean res = a.drop(false);
		if (res && !inUse(id)) {
			actorRefs.get("eventStore").tell(new Put(new MarketDropped(id)), getSelf());
		} else {
			log.warning("Unable to drop buyer " + id);
		}
	}
	//DropMarket,M1
	
	@Override
	void onStatus(Status s) {
		String id = s.getId();
		Market a = new Market();
		remake(a, id);
		getSender().tell(new AggregateReq(a), getSelf());
	}

	private boolean inUse(String id) {
		//TODO: Figure out the non-internal reasons why it shouldn't be dropped
		return false;
	}

	@Override
	void applyEvent(AbstractDomain ad, AbstractEvent event) {

   	 	Market a = (Market) ad;

		switch (event.getType()) {
			case "MarketCreated":
				MarketCreated mc = (MarketCreated) event;
				a.create(mc.getKey(), mc.getName(), true);
				break;
			case "MarketUpdated":
				MarketUpdated mu = (MarketUpdated) event;
				a.update(mu.getName(), true);
				break;
			case "MarketDropped":
				MarketDropped md = (MarketDropped) event;
				a.drop(true);
				break;
			default:
				break;
		}
	}
	
}
