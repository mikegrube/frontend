package com.camas.reactor;

import com.camas.event.AbstractEvent;
import com.camas.event.ProductPurchased;
import com.camas.event.BuyerStatusChanged;

import com.camas.message.Put;
import com.camas.message.Read;
import com.camas.message.EventList;

import akka.actor.Props;
import akka.util.Timeout;
import scala.concurrent.duration.Duration;
import java.util.concurrent.TimeUnit;
import scala.concurrent.Await;
import scala.concurrent.Future;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.io.IOException;

public class BuyerStatusReactor extends AbstractReactor {
	
	HashMap<String, Double> purchases = new HashMap<>();

	public BuyerStatusReactor() {
		super("BuyerStatusReactor", 0, 5);
	}

	@Override
	Object getTimerCommand() {
		return new Read("ProductPurchased", "ANY", nextOffset);
	}

	@Override
	void timerPosting(Object obj) {

		EventList eventList = (EventList) obj;
		ArrayList<AbstractEvent> events = eventList.getEvents();
		for (AbstractEvent event : events) {
			ProductPurchased pp = (ProductPurchased) event;
			String buyerId = pp.getBuyerId();
			Double prevTotal = purchases.get(buyerId);
			if (prevTotal == null) {
				prevTotal = 0.0;
			}
			String prevStatus = getStatus(prevTotal);
			int quantity = Integer.parseInt(pp.getQuantity());
			double price = Double.parseDouble(pp.getPrice());
			double total = prevTotal + (quantity * price);
			purchases.put(buyerId, total);
			String status = getStatus(total);
			//If status has changed, post the new status
			if (!status.equals(prevStatus)) {
				eventStore.tell(new Put(new BuyerStatusChanged(buyerId, status)), getSelf());
			}
			nextOffset++;
		}

	}
	
	public static boolean isBetween(double x, double lower, double upper) {
		return lower <= x && x < upper;
	}

	String getStatus(double amount) {
		String stat;
		if (isBetween(amount, 0.0, 100.0)) {
			stat = "New";
		} else if (isBetween(amount, 100.0, 500.0)) {
			stat = "Bronze";
		} else if (isBetween(amount, 500.0, 1500.0)) {
			stat = "Silver";
		} else if (isBetween(amount, 1500.0, 5000.0)) {
			stat = "Gold";
		} else {
			stat = "Platinum";
		}
		return stat;
	}

	public static Props props() {
		return Props.create(BuyerStatusReactor.class);
	}

}