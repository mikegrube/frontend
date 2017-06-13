package com.camas.projector;

import com.camas.event.AbstractEvent;

import com.camas.message.Command;
import com.camas.message.Read;
import com.camas.message.EventList;

import java.util.Timer;
import java.util.TimerTask;

import akka.actor.Props;
import akka.util.Timeout;
import scala.concurrent.duration.Duration;
import java.util.concurrent.TimeUnit;
import scala.concurrent.Await;
import scala.concurrent.Future;

import java.util.ArrayList;
import java.io.IOException;

public class EventProjector extends AbstractProjector {

	ArrayList<String> strungEvents = new ArrayList<String>();

    public EventProjector() {
		super("EventProjector", 0, 5);
    }

	@Override
	Object getTimerCommand() {
		return new Read("ANY", "ANY", nextOffset);
	}

	@Override
	void timerPosting(Object obj) {

		EventList eventList = (EventList) obj;
		ArrayList<AbstractEvent> events = eventList.getEvents();
		for (AbstractEvent event : events) {
			strungEvents.add(event.toString());
			nextOffset++;
		}

	}

    public static Props props() {
      return Props.create(EventProjector.class);
    }

	@Override
	void onCommand(Command cmd) {

		switch (cmd.getCommand()) {
			case "ShowEvents":
			onShowEvents(cmd.getArgs()[0]);
				break;
		}
	}

	private void onShowEvents(String requestedOffset) {

		log.info("Events (from " + requestedOffset + "):");
		for (int i = Integer.parseInt(requestedOffset); i < strungEvents.size(); i++) {
			log.info("  " + strungEvents.get(i));
		}
		log.info("End events...");
	}
}
