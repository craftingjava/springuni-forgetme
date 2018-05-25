package com.springuni.forgetme.subscriber.web;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.springuni.forgetme.core.web.AbstractResource;
import com.springuni.forgetme.subscriber.model.Subscriber;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.hateoas.core.Relation;

@Getter
@Setter
@NoArgsConstructor
@Relation(value = "subscriber", collectionRelation = "subscribers")
public class SubscriberResource extends AbstractResource {

  @JsonProperty("subscriptions")
  private List<SubscriptionResource> subscriptionResources;

  public SubscriberResource(Subscriber subscriber) {
    super(subscriber);
    this.subscriptionResources = subscriber.getSubscriptions().stream()
        .map(SubscriptionResource::new).collect(Collectors.toList());
  }

}
