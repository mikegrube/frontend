package com.camas.frontend;

import com.camas.store.EventStore;

import com.camas.event.AbstractEvent;

import com.camas.projector.EventProjector;
import com.camas.projector.ProductPriceProjector;
import com.camas.projector.BuyerStatusProjector;

import com.camas.reactor.BuyerStatusReactor;

import com.camas.message.Read;
import com.camas.message.Command;
import com.camas.message.EventList;
import com.camas.message.ActorSet;

import java.util.ArrayList;

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

//The CommandHandler distributes commands to its actor children
public class CommandHandler extends AbstractActor {
	private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

	//This is a list of all known commands, in code so brittle
	private String[] commandList = {
		"CreateProduct", "UpdateProduct", "AdjustInventory", "UpdateProductPrice", "DropProduct", "ShowProduct",
		"CreateBuyer", "UpdateBuyer", "DropBuyer", "ShowBuyer",
		"CreateMarket", "UpdateMarket", "DropMarket", "ShowMarket",
		"CreateOffer", "UpdateOffer", "AddProductToOffer", "RemoveProductFromOffer", "UpdateOfferProductPrice", "AdjustOfferProductInventory", "DropOffer", "ShowOffer",
		"PurchaseFromOffer",
		"ShowEvents",
		"ShowPrices", "ShowPrice",
		"ShowStatuses", "ShowStatus"
	 };

	//These are the actors that make up the system, all children of the command handler
	//Command handlers
	ActorRef productHandler;
	ActorRef buyerHandler;
	ActorRef marketHandler;
	ActorRef offerHandler;
	ActorRef eventStore;
	//Query projectors
	ActorRef eventProjector;
	ActorRef productPriceProjector;
	ActorRef buyerStatusProjector;
	//Automated reactors
	ActorRef buyerStatusReactor;

	//Constructor - instantiate the actors
	public CommandHandler() {
		//Instantiate
		eventStore = getContext().actorOf(EventStore.props(), "eventstore-01");
		productHandler = getContext().actorOf(ProductHandler.props(), "product-01");
		buyerHandler = getContext().actorOf(BuyerHandler.props(), "buyer-01");
		marketHandler = getContext().actorOf(MarketHandler.props(), "market-01");
		offerHandler = getContext().actorOf(OfferHandler.props(), "offer-01");

		//Some of these need to know about their siblings
		ActorSet actorSet = new ActorSet(buyerHandler, marketHandler, offerHandler, productHandler, eventStore);
		productHandler.tell(actorSet, getSelf());
		buyerHandler.tell(actorSet, getSelf());
		marketHandler.tell(actorSet, getSelf());
		offerHandler.tell(actorSet, getSelf());

		eventProjector = getContext().actorOf(EventProjector.props(), "eventProjector-01");
		eventProjector.tell(actorSet, getSelf());
		productPriceProjector = getContext().actorOf(ProductPriceProjector.props(), "productPriceProjector-01");
		productPriceProjector.tell(actorSet, getSelf());
		buyerStatusProjector = getContext().actorOf(BuyerStatusProjector.props(), "buyerStatusProjector-01");
		buyerStatusProjector.tell(actorSet, getSelf());

		buyerStatusReactor = getContext().actorOf(BuyerStatusReactor.props(), "buyerstatus-01");
		buyerStatusReactor.tell(actorSet, getSelf());
	}

	public static Props props() {
		return Props.create(CommandHandler.class);
	}

	@Override
	public void preStart() {
		log.info("Command handler started");
	}

	@Override
	public void postStop() {
		log.info("Command handler stopped");
	}

	//Treat Strings coming from the FrontEnd as commands
	@Override
	public Receive createReceive() {
		return receiveBuilder()
			.match(String.class, this::onProcess)
			.build();
	}

	//Distribute the commands to the appropriate handler
	private void onProcess(String cmd) {

		String result = "No such command";

		//Command line elements are separated by commas
		String[] cmdParts = cmd.split(",");
		
		//The first element is the command
		String head = cmdParts[0];
		String[] tail = tail(cmdParts);
		//log.info("Head is '" + head + "' and tail is '" + tail + "'");

		for (String candidate : commandList) {

			if (candidate.equals(head)) {

			switch (head) {
				case "CreateProduct":
				case "UpdateProduct":
				case "AdjustInventory":
				case "UpdateProductPrice":
				case "DropProduct":
				case "ShowProduct":			//This is bogus; it should move to a projection
					productHandler.tell(new Command(head, tail), getSelf());
					result = "Product command processed";
					break;
				case "CreateBuyer":
				case "UpdateBuyer":
				case "DropBuyer":
				case "ShowBuyer":				//This is bogus, it should move to a projection
					buyerHandler.tell(new Command(head, tail), getSelf());
					result = "Buyer command processed";
					break;
				case "CreateMarket":
				case "UpdateMarket":
				case "DropMarket":
				case "ShowMarket":				//This is bogus, it should move to a projection
					marketHandler.tell(new Command(head, tail), getSelf());
					result = "Market command processed";
					break;
				case "CreateOffer":
				case "UpdateOffer":
				case "AddProductToOffer":
				case "RemoveProductFromOffer":
				case "UpdateOfferProductPrice":
				case "AdjustOfferProductInventory":
				case "DropOffer":
				case "ShowOffer":
				case "PurchaseFromOffer":
					offerHandler.tell(new Command(head, tail), getSelf());
					result = "Offer command processed";
					break;
				case "ShowEvents":
					eventProjector.tell(new Command(head, tail), getSelf());
					result = "Event projector command processed";
					break;
				case "ShowPrice":
				case "ShowPrices":
					productPriceProjector.tell(new Command(head, tail), getSelf());
					result = "Produce price projector command processed";
					break;
				case "ShowStatus":
				case "ShowStatuses":
					buyerStatusProjector.tell(new Command(head, tail), getSelf());
					result = "Buyer status projector command processed";
					break;
				default:
					result = "The command " + head + " has not yet been implemented.";
					break;
				}
			}
		}
		log.info(result);
	}

	//Split off the command parameters
	private String[] tail(String[] in) {
		String [] out = new String[in.length - 1];
		for (int i = 1; i < in.length; i++) {
			out[i-1] = in[i];
		}
		return out;
	}

}
