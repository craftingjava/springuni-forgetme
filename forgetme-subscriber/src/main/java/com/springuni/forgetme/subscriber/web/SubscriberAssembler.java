package com.springuni.forgetme.subscriber.web;

import com.springuni.forgetme.subscriber.model.Subscriber;
import org.springframework.hateoas.mvc.IdentifiableResourceAssemblerSupport;
import org.springframework.stereotype.Component;

@Component
public class SubscriberAssembler
    extends IdentifiableResourceAssemblerSupport<Subscriber, SubscriberResource> {

  public SubscriberAssembler() {
    super(SubscriberController.class, SubscriberResource.class);
  }

  @Override
  public SubscriberResource toResource(Subscriber subscriber) {
    return createResource(subscriber);
  }

  @Override
  protected SubscriberResource instantiateResource(Subscriber subscriber) {
    return new SubscriberResource(subscriber);
  }

}
