package com.springuni.forgetme.subscriber;

import static com.springuni.forgetme.Mocks.createSubscription;
import static com.springuni.forgetme.subscriber.SubscriberStatus.UNSUBSCRIBED;
import static org.junit.Assert.assertTrue;

import com.springuni.forgetme.core.orm.BaseRepositoryTest;
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
  protected Subscription createEntity() throws Exception {
    return createSubscription();
  }

}
