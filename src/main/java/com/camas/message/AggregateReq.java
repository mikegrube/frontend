package com.camas.message;

import com.camas.domain.AbstractDomain;

public class AggregateReq {

   AbstractDomain aggregate;

   public AggregateReq() {
   }
   
   public AggregateReq(AbstractDomain aggregate) {
      this.aggregate = aggregate;
   }
   
   public AbstractDomain getAggregate() {
      return aggregate;
   }
   
}

