package com.springuni.forgetme.subscriber.service;

import static com.springuni.forgetme.subscriber.Mocks.createSubscription;
import static org.junit.Assert.assertTrue;

import com.springuni.forgetme.core.orm.BaseRepositoryTest;
import com.springuni.forgetme.subscriber.model.Subscriber;
import com.springuni.forgetme.subscriber.model.Subscription;
import java.util.Optional;
import java.util.UUID;
import org.junit.Test;

public class SubscriberRepositoryTest extends BaseRepositoryTest<Subscriber, SubscriberRepository> {

  @Test
  public void givenSavedSubscriber_whenFindByEmailHash_thenFound() {
    saveEntity();
    Optional<Subscriber> subscriberOptional = repository.findByEmailHash(entity.getEmailHash());
    assertTrue(subscriberOptional.isPresent());
  }

  @Test
  public void givenExistingSubscription_whenFindBySubscriptionId_thenSubscriberReturned() {
    saveEntity();
    UUID subscriptionId = entity.getSubscriptions().get(0).getId();
    Optional<Subscriber> subscriberOptional = repository.findBySubscriptionId(subscriptionId);
    assertTrue(subscriberOptional.isPresent());
  }

  @Override
  protected Subscriber createEntity() {
    Subscription subscription = createSubscription();
    return subscription.getSubscriber();
  }

}
