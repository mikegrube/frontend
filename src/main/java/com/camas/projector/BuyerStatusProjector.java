package com.camas.projector;

import com.camas.event.AbstractEvent;
import com.camas.event.BuyerStatusChanged;

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

public class BuyerStatusProjector extends AbstractProjector {

	HashMap<String, String> statuses = new HashMap<>();

	public BuyerStatusProjector() {
		super("BuyerStatusProjector", 0, 5);
	}

	@Override
	Object getTimerCommand() {
		return new Read("BuyerStatusChanged", "ANY", nextOffset);
	}

	@Override
	void timerPosting(Object obj) {

		EventList eventList = (EventList) obj;
		if (eventList.getLastOffsetRead() >= 0) {
			ArrayList<AbstractEvent> events = eventList.getEvents();
			for (AbstractEvent event : events) {
				BuyerStatusChanged ppu = (BuyerStatusChanged) event;
				statuses.put(ppu.getKey(), ppu.getStatus());
			}
			nextOffset = eventList.getLastOffsetRead() + 1;
		}
	}

	public static Props props() {
		return Props.create(BuyerStatusProjector.class);
	}

	@Override
	void onCommand(Command cmd) {

		switch (cmd.getCommand()) {
			case "ShowStatuses":
				onShowStatuses();
				break;
			case "ShowStatus":
				onShowStatus(cmd.getArgs()[0]);
				break;
		}
	}

	private void onShowStatuses() {

		log.info("Current Buyer statuses");
		for (Map.Entry<String, String> entry : statuses.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();
			log.info("  " + key + " has status " + value);
		}
		log.info("End status");
	}

	private void onShowStatus(String id) {

		log.info("Status for " + id + ":");
		String res = statuses.get(id);
		if (res == null) {
			log.info("  none");			
		} else {
			log.info("  " + res);
		}
		log.info("End status");
	}

}
