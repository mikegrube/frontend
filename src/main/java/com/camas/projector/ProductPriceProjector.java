package com.camas.projector;

import com.camas.event.AbstractEvent;
import com.camas.event.ProductPriceUpdated;

import com.camas.message.Command;
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

public class ProductPriceProjector extends AbstractProjector {

	HashMap<String, Double> prices = new HashMap<>();

	public ProductPriceProjector() {
		super("ProductPriceProjector", 0, 5);
	}

	@Override
	Object getTimerCommand() {
		return new Read("ProductPriceUpdated", "ANY", nextOffset);
	}

	@Override
	void timerPosting(Object obj) {

		EventList eventList = (EventList) obj;
		ArrayList<AbstractEvent> events = eventList.getEvents();
		for (AbstractEvent event : events) {
			ProductPriceUpdated ppu = (ProductPriceUpdated) event;
			prices.put(ppu.getKey(), Double.parseDouble(ppu.getAmount()));
			nextOffset++;
		}

	}

	public static Props props() {
		return Props.create(ProductPriceProjector.class);
	}

	@Override
	void onCommand(Command cmd) {

		switch (cmd.getCommand()) {
			case "ShowPrices":
				onShowPrices();
				break;
			case "ShowPrice":
				onShowPrice(cmd.getArgs()[0]);
				break;
		}
	}

	private void onShowPrices() {

		log.info("Current Product prices");
		for (Map.Entry<String, Double> entry : prices.entrySet()) {
			String key = entry.getKey();
			Object value = entry.getValue();
			log.info("  " + key + " has price " + value);
		}
		log.info("End pricing");
	}

	private void onShowPrice(String id) {

	log.info("Price for " + id + ":");
	log.info("  " + prices.get(id));
	log.info("End pricing...");
	}

}
