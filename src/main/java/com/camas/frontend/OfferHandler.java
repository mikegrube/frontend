package com.camas.frontend;

import com.camas.domain.Offer;
import com.camas.domain.OfferProduct;
import com.camas.domain.Market;
import com.camas.domain.Product;
import com.camas.domain.AbstractDomain;

import com.camas.event.AbstractEvent;
import com.camas.event.OfferCreated;
import com.camas.event.OfferUpdated;
import com.camas.event.OfferProductAdded;
import com.camas.event.OfferProductRemoved;
import com.camas.event.OfferProductInventoryAdjusted;
import com.camas.event.OfferProductPriceUpdated;
import com.camas.event.OfferDropped;
import com.camas.event.ProductPurchased;

import com.camas.message.Put;
import com.camas.message.Read;
import com.camas.message.EventList;
import com.camas.message.Status;
import com.camas.message.AggregateReq;
import com.camas.message.Command;

import akka.actor.Props;
import akka.actor.ActorRef;
import akka.pattern.Patterns;
import akka.util.Timeout;
import scala.concurrent.duration.Duration;
import java.util.concurrent.TimeUnit;
import scala.concurrent.Await;
import scala.concurrent.Future;
import akka.util.Timeout;

import java.util.ArrayList;
import java.io.IOException;

/* OfferHandler
*
*/
public class OfferHandler extends AbstractHandler {
	
	ActorRef marketHandler;
	ActorRef productHandler;
	
	static String keyPrefix = "O";

	private int highestId = 0;

	public OfferHandler() {
		super("OfferHandler", "CreateOffer");
		marketHandler = getSingleActorRefFromPath(getContext().getSystem(), "/user/command-handler/market-01");
		productHandler = getSingleActorRefFromPath(getContext().getSystem(), "/user/command-handler/product-01");
	}

	public static Props props() {
		return Props.create(OfferHandler.class);
	}

	@Override
	void onCommand(Command cmd) {

		boolean res;
		String[] args = cmd.getArgs();
		switch (cmd.getCommand()) {
			case "CreateOffer":
				onCreate(keyPrefix + ++highestId, args[0], args[1]);
				break;
			case "UpdateOffer":
				onUpdate(args[0], args[1]);
				break;
			case "AddProductToOffer":
				onProductAddition(args[0], args[1]);
				break;
			case "RemoveProductFromOffer":
				onProductRemoval(args[0], args[1]);
				break;
			case "AdjustOfferProductInventory":
				onAdjustProductInventory(args[0], args[1], args[2]);
				break;
			case "UpdateOfferProductPrice":
				onUpdateProductPrice(args[0], args[1], args[2]);
				break;
			case "DropOffer":
				onDrop(args[0]);
				break;
			case "ShowOffer":
				onShow(args[0]);
				break;
			case "PurchaseFromOffer":
				onPurchase(args[0],args[1],args[2], args[3]);
				break;
			default:
				break;
		}

	}

	private void onCreate(String id, String marketId, String title) {
		Offer a = new Offer();
		boolean res = true;
		Market m = getMarket(marketId);
		if (m.getName() == null || m.getName().equals("") || m.isDropped()) {
			res = false;
			log.warning("Market " + marketId + " is not available");
		} else {
			res = a.create(id, marketId, title, false);
			if (res && titleIsUnique(title)) {
				eventStore.tell(new Put(new OfferCreated(id, marketId, title)), getSelf());
			} else {
				log.warning("Unable to create offer " + id);
			}
		}
	}
	// CreateOffer,Amazon-6/11-6/18

	private Market getMarket(String marketId) {
		AggregateReq req = (AggregateReq) request(marketHandler, new Status(marketId));
		return (Market) req.getAggregate();
	}
	
	private Product getProduct(String productId) {
		AggregateReq req = (AggregateReq) request(productHandler, new Status(productId));
		return (Product) req.getAggregate();
	}

	private boolean titleIsUnique(String name) {
		//TODO: How would we do this?
		return true;
	}

	private void onUpdate(String id, String title) {
		Offer a = new Offer();
		remake(a, id);
		boolean res = a.update(title, false);
		if (res && titleIsUnique(title)) {
			eventStore.tell(new Put(new OfferUpdated(id, title)), getSelf());
		} else {
			log.warning("Unable to update offer " + id);
		}
	}
	// UpdateOffer,M1,Amazon-Unlimited

	private void onProductAddition(String id, String productId) {
		Offer a = new Offer();
		remake(a, id);
		boolean res = true;
		Product p = getProduct(productId);
		if (p.getName() == null || p.getName().equals("") || p.isDropped()) {
			res = false;
			log.warning("Product " + productId + " is not available");
		} else {
			res = a.addProduct(productId, p.getInventory(), p.getPrice(), false);
			if (res) {
				eventStore.tell(new Put(new OfferProductAdded(id, productId, "" + p.getInventory(), "" + p.getPrice())), getSelf());
			} else {
				log.warning("Product " + productId + " could not be added to " + id);
			}
		}
	}
	//AddProductToOffer,O1,P1

	private void onProductRemoval(String id, String productId) {
		Offer a = new Offer();
		remake(a, id);
		boolean res = true;
		Product p = getProduct(productId);
		if (p.getName() == null || p.getName().equals("") || p.isDropped()) {
			res = false;
			log.warning("Product " + productId + " is unknown");
		} else {
			res = a.removeProduct(productId, false);
			if (res) {
				eventStore.tell(new Put(new OfferProductRemoved(id, productId)), getSelf());
			} else {
				log.warning("Product " + productId + " could not be removed from " + id);
			}
		}
	}
	//RemoveProductFromOffer,O1,P1

	private void onAdjustProductInventory(String id, String productId, String amount) {
		Offer a = new Offer();
		remake(a, id);
		boolean res = true;
		Product p = getProduct(productId);
		if (p.getName() == null || p.getName().equals("") || p.isDropped()) {
			res = false;
			log.warning("Product " + productId + " is unknown or retired");
		} else {
			res = a.adjustProductInventory(productId, Integer.parseInt(amount), false);
			if (res) {
				eventStore.tell(new Put(new OfferProductInventoryAdjusted(id, productId, amount)), getSelf());
			} else {
				log.warning("Unable to adjust product " + productId + " inventory on offer " + id);
			}
		}
	}
	// AdjustOfferProductInventory,O1,P1,10

	private void onUpdateProductPrice(String id, String productId, String amount) {
		Offer a = new Offer();
		remake(a, id);
		boolean res = true;
		Product p = getProduct(productId);
		if (p.getName() == null || p.getName().equals("") || p.isDropped()) {
			res = false;
			log.warning("Product " + productId + " is unknown or retired");
		} else {
			res = a.updateProductPrice(productId, Double.parseDouble(amount), false);
			if (res) {
				eventStore.tell(new Put(new OfferProductPriceUpdated(id, productId, amount)), getSelf());
			} else {
				log.warning("Unable to update product " + productId + " price on offer " + id);
			}
		}
	}
	// UpdateOfferProductPrice,O1,P1,19.0

	private void onShow(String id) {
		Offer a = new Offer();
		remake(a, id);
		log.info("Offer " + id + ": market:" + a.getMarketId() + ", title:" + a.getTitle());
		ArrayList<OfferProduct> products = a.getProducts();
		for (OfferProduct op : products) {
			log.info("  Product " + op.getProductId() + ", inventory: " + op.getInventory() + ", price: " + op.getPrice());
		}
	}

	private void onDrop(String id) {
		Offer a = new Offer();
		remake(a, id);
		//TODO: Any external reasons why we can't drop?
		boolean res = a.drop(false);
		if (res && !inUse(id)) {
			eventStore.tell(new Put(new OfferDropped(id)), getSelf());
		} else {
			log.warning("Unable to drop product " + id);
		}
	}
	
	private void onPurchase(String id, String productId, String quantity, String buyerId) {
		Offer a = new Offer();
		remake(a, id);
		Product p = getProduct(productId);
		OfferProduct op = a.getOfferProduct(productId);
		int qty = -Integer.parseInt(quantity);
		boolean res = a.adjustProductInventory(productId, qty, false);
		if (res && p.adjustInventory(qty, false)) {
			eventStore.tell(new Put(new ProductPurchased(id, productId, quantity, "" + op.getPrice(), buyerId)), getSelf());
		} else {
			log.warning("Unable to purchase " + quantity + " of  product " + productId + " from offer " + id);
		}
	}

	private boolean inUse(String id) {
		//TODO: Figure out a reason why this shouldn't be dropped
		return false;
	}

	@Override
	void applyEvent(AbstractDomain ad, AbstractEvent event) {

		Offer a = (Offer) ad;

		switch (event.getType()) {
			case "OfferCreated":
				OfferCreated oc = (OfferCreated) event;
				a.create(oc.getKey(), oc.getMarketId(), oc.getTitle(), true);
				break;
			case "OfferUpdated":
				OfferUpdated ou = (OfferUpdated) event;
				a.update(ou.getTitle(), true);
				break;
			case "OfferProductAdded":
				OfferProductAdded opa = (OfferProductAdded) event;
				a.addProduct(opa.getProductId(), Integer.parseInt(opa.getInventory()), Double.parseDouble(opa.getPrice()), true);
				break;
			case "OfferProductRemoved":
				OfferProductRemoved opr = (OfferProductRemoved) event;
				a.removeProduct(opr.getProductId(), true);
				break;
			case "OfferProductInventoryAdjusted":
				OfferProductInventoryAdjusted opia = (OfferProductInventoryAdjusted) event;
				a.adjustProductInventory(opia.getProductId(), Integer.parseInt(opia.getAmount()), true);
				break;
			case "OfferProductPriceUpdated":
			OfferProductPriceUpdated oppu = (OfferProductPriceUpdated) event;
				a.updateProductPrice(oppu.getProductId(), Double.parseDouble(oppu.getAmount()), true);
				break;
			case "OfferDropped":
				OfferDropped od = (OfferDropped) event;
				a.drop(true);
				break;
			case "ProductPurchased":
			ProductPurchased pp = (ProductPurchased) event;
				int qty = -Integer.parseInt(pp.getQuantity());
				a.adjustProductInventory(pp.getProductId(), qty, true);
				break;
			default:
				break;
		}
	}

}

