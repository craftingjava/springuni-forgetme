package com.springuni.forgetme.subscriber.web;

import com.springuni.forgetme.core.web.AbstractResource;
import com.springuni.forgetme.subscriber.model.Subscription;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.hateoas.core.Relation;

@Getter
@Setter
@NoArgsConstructor
@Relation(value = "subscription", collectionRelation = "subscriptions")
public class SubscriptionResource extends AbstractResource {

  public SubscriptionResource(Subscription subscription) {
    super(subscription);
  }

}
