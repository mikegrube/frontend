package com.camas.frontend;

import java.util.ArrayList;

import com.camas.domain.Buyer;
import com.camas.domain.AbstractDomain;

import com.camas.event.AbstractEvent;
import com.camas.event.BuyerCreated;
import com.camas.event.BuyerUpdated;
import com.camas.event.BuyerDropped;

import com.camas.message.Put;
import com.camas.message.Read;
import com.camas.message.EventList;
import com.camas.message.Command;

import akka.actor.Props;
import akka.actor.ActorRef;
import akka.pattern.Patterns;
import akka.util.Timeout;
import scala.concurrent.duration.Duration;
import java.util.concurrent.TimeUnit;
import scala.concurrent.Await;
import scala.concurrent.Future;

import java.util.ArrayList;
import java.io.IOException;

//This command handler handles all commands for the Buyer aggregate
public class BuyerHandler extends AbstractHandler {

	private static String keyPrefix = "B";

	public BuyerHandler() {
		super("BuyerHandler", "CreateBuyer");
	}

	public static Props props() {
		return Props.create(BuyerHandler.class);
	}

	//Handle commands pertaining to the Buyer aggregate
	@Override
	void onCommand(Command cmd) {

		String[] args = cmd.getArgs();

		switch (cmd.getCommand()) {
		case "CreateBuyer":
			onCreate(keyPrefix + ++highestId, args[0]);
			break;
		case "UpdateBuyer":
			onUpdate(args[0], args[1]);
			break;
		case "DropBuyer":
			onDrop(args[0]);
			break;
		case "ShowBuyer":
			onShow(args[0]);
			break;
		default:
			break;
		}

	}

	//A Buyer is being created
	private void onCreate(String id, String name) {

		Buyer a = new Buyer();
		boolean res = a.create(id, name, false);

		//The event is only recognized if the command is valid
		if (res && nameIsUnique(name)) {
			eventStore.tell(new Put(new BuyerCreated(id, name)), getSelf());
		} else {
			log.warning("Unable to create buyer " + id);
		}
	}
	// CreateBuyer,Paul Johnson

	//Test for uniqueness
	private boolean nameIsUnique(String name) {
		//TODO: How would we do this?
		return true;
	}

	//An existing buyer is being updated
	private void onUpdate(String id, String name) {
		//First we rebuild this Buyer
		Buyer a = new Buyer();
		remake(a, id);
		//Now we attempt to apply the command
		boolean res = a.update(name, false);
		//The event is only recognized if the command is valid
		if (res && nameIsUnique(name)) {
			eventStore.tell(new Put(new BuyerUpdated(id, name)), getSelf());
		} else {
			log.warning("Unable to update buyer " + id);
		}
	}
	// UpdateBuyer,B1,Paulus Johnson

	private void onShow(String id) {
		Buyer a = new Buyer();
		remake(a, id);
		log.info("Buyer " + id + ": name:" + a.getName());
	}

	private void onDrop(String id) {
		//First we rebuild this Buyer
		Buyer a = new Buyer();
		remake(a, id);
		//Now we attempt to apply the command
		boolean res = a.drop(false);
		//The event is only recognized if the command is valid
		if (res && !inUse(id)) {
			eventStore.tell(new Put(new BuyerDropped(id)), getSelf());
		} else {
			log.warning("Unable to drop buyer " + id);
		}
	}
	//DropBuyer,B1

	//Test to determine if this instance is required
	private boolean inUse(String id) {
		//TODO: Figure out the non-internal reasons why it shouldn't be dropped
		return false;
	}

	//For rebuilds, use the event to reapply
	@Override
	void applyEvent(AbstractDomain ad, AbstractEvent event) {

		Buyer a = (Buyer) ad;

		switch (event.getType()) {
			case "BuyerCreated":
				BuyerCreated bc = (BuyerCreated) event;
				a.create(bc.getKey(), bc.getName(), true);
				break;
			case "BuyerUpdated":
				BuyerUpdated bu = (BuyerUpdated) event;
				a.update(bu.getName(), true);
				break;
			case "BuyerDropped":
				BuyerDropped bd = (BuyerDropped) event;
				a.drop(true);
				break;
			default:
				break;
		}
	}

}

