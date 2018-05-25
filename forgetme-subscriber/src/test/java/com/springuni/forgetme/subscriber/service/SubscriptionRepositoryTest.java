package com.springuni.forgetme.subscriber.service;

import static com.springuni.forgetme.Mocks.createSubscription;
import static com.springuni.forgetme.core.model.SubscriberStatus.UNSUBSCRIBED;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.springuni.forgetme.core.orm.BaseRepositoryTest;
import com.springuni.forgetme.subscriber.model.Subscription;
import java.util.List;
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

  @Test
  public void givenSubscriber_whenFindBySubscriber_thenSubscriptionsReturned() {
    saveEntity();
    List<Subscription> subscriptions = repository
        .findBySubscriberId(entity.getSubscriber().getId());
    assertFalse(subscriptions.isEmpty());
  }

  @Override
  protected Subscription createEntity() {
    return createSubscription();
  }

}
