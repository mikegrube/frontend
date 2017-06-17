package com.camas.frontend;

import com.camas.domain.Product;
import com.camas.domain.AbstractDomain;

import com.camas.event.AbstractEvent;
import com.camas.event.ProductCreated;
import com.camas.event.ProductUpdated;
import com.camas.event.ProductInventoryAdjusted;
import com.camas.event.ProductPriceUpdated;
import com.camas.event.ProductDropped;

import com.camas.message.Put;
import com.camas.message.Read;
import com.camas.message.EventList;
import com.camas.message.AggregateReq;
import com.camas.message.Status;
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

/* ProductHandler
 *
 */
public class ProductHandler extends AbstractHandler {

   static String keyPrefix = "P";

  private int highestId = 0;

  public ProductHandler() {
	  super("ProductHandler", "CreateProduct");
  }

  public static Props props() {
    return Props.create(ProductHandler.class);
  }

  @Override
  void onCommand(Command cmd) {
     
     boolean res;
     
  String[] args = cmd.getArgs();
    
     switch (cmd.getCommand()) {
        case "CreateProduct":
           onCreate(keyPrefix + ++highestId, args[0], args[1]);
           break;
        case "UpdateProduct":
           onUpdate(args[0], args[1], args[2]);
           break;
        case "AdjustInventory":
           onAdjustInventory(args[0], args[1]);
           break;
        case "UpdateProductPrice":
           onUpdatePrice(args[0], args[1]);
           break;
        case "DropProduct":
           onDrop(args[0]);
           break;
        case "ShowProduct":
           onShow(args[0]);
           break;
        default:
        break;
     }
     
  }
  
  private void onCreate(String id, String name, String description) {

     Product a = new Product();
     boolean res = a.create(id, name, description, false);
     //Name must be unique
     if (res && nameIsUnique(name)) {
			actorRefs.get("eventStore").tell(new Put(new ProductCreated(id, name, description)), getSelf());
     } else {
        log.warning("Unable to create product " + id);
     }
  }
  // CreateProduct,Shirt,A nice shirt
  
  private boolean nameIsUnique(String name) {
     //TODO: How would we do this?
     return true;
  }

  private void onUpdate(String id, String name, String description) {
      Product a = new Product();
	  remake(a, id);
     
     //Name must be unique
     boolean res = a.update(name, description, false);
     if (res && nameIsUnique(name)) {
			actorRefs.get("eventStore").tell(new Put(new ProductUpdated(id, name, description)), getSelf());
     } else {
        log.warning("Unable to create product " + id);
     }
  }
  // UpdateProduct,P1,Shirt,A very nice shirt
  
  private void onAdjustInventory(String id, String amount) {
      Product a = new Product();
	  remake(a, id);
     boolean res = a.adjustInventory(Integer.parseInt(amount), false);
     if (res) {
			actorRefs.get("eventStore").tell(new Put(new ProductInventoryAdjusted(id, amount)), getSelf());
     } else {
        log.warning("Unable to update product " + id);
     }
  }
  // AdjustInventory,P1,10
  
  private void onUpdatePrice(String id, String amount) {
      Product a = new Product();
	  remake(a, id);
     boolean res = a.updatePrice(Double.parseDouble(amount), false);
     if (res) {
			actorRefs.get("eventStore").tell(new Put(new ProductPriceUpdated(id, amount)), getSelf());
      } else {  
        log.warning("Unable to update product price for " + id);
     }
  }
  // UpdateProductPrice,P1,18.0
  
  private void onShow(String id) {
      Product a = new Product();
	  remake(a, id);
     log.info("Product " + id + ": name:" + a.getName() + ", desc:" + a.getDescription() + ", price:" + a.getPrice() + ", inventory:" + a.getInventory());
  }
  
  private void onDrop(String id) {
      Product a = new Product();
	  remake(a, id);
     boolean res = a.drop(false);
     if (res && !inUse(id)) {
		actorRefs.get("eventStore").tell(new Put(new ProductDropped(id)), getSelf());
     } else {
        log.warning("Unable to drop product " + id);
     }
  }
  
	@Override
	void onStatus(Status s) {
		String id = s.getId();
		Product a = new Product();
		remake(a, id);
		getSender().tell(new AggregateReq(a), getSelf());
	}

  private boolean inUse(String id) {
     //TODO: Figure out a reason why this shouldn't be dropped
     return false;
  }
  
  @Override
  void applyEvent(AbstractDomain ad, AbstractEvent event) {

	 Product a = (Product) ad;

     switch (event.getType()) {
        case "ProductCreated":
			ProductCreated pc = (ProductCreated) event;
           a.create(pc.getKey(), pc.getName(), pc.getDescription(), true);
           break;
        case "ProductUpdated":
			ProductUpdated pu = (ProductUpdated) event;
           a.update(pu.getName(), pu.getDescription(), true);
           break;
        case "ProductInventoryAdjusted":
			ProductInventoryAdjusted pia = (ProductInventoryAdjusted) event;
           a.adjustInventory(Integer.parseInt(pia.getAmount()), true);
           break;
        case "ProductPriceUpdated":
			ProductPriceUpdated ppu = (ProductPriceUpdated) event;
           a.updatePrice(Double.parseDouble(ppu.getAmount()), true);
           break;
        case "ProductDropped":
           a.drop(true);
           break;
        default:
        break;
     }
  }
  
}

