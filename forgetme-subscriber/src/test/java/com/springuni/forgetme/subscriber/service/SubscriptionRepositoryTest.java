package com.springuni.forgetme.subscriber.service;

import static com.springuni.forgetme.Mocks.createSubscription;
import static com.springuni.forgetme.subscriber.model.SubscriberStatus.UNSUBSCRIBED;
import static org.junit.Assert.assertTrue;

import com.springuni.forgetme.core.orm.BaseRepositoryTest;
import com.springuni.forgetme.subscriber.model.Subscription;
import org.junit.Test;

public class SubscriptionRepositoryTest
    extends BaseRepositoryTest<Subscription, SubscriptionRepository> {

  @Test
  public void givenSubscriberStatusChanged_whenFindById_thenChangePersisted() {
    entity.updateStatus(UNSUBSCRIBED);
    saveEntity();
    Subscription subscription = repository.findById(entity.getId()).get();
    assertTrue(subscription.getStatusChanges().stream()
        .anyMatch(it -> it.getStatus().equals(UNSUBSCRIBED)));
  }

  @Override
  protected Subscription createEntity() {
    return createSubscription();
  }

}
