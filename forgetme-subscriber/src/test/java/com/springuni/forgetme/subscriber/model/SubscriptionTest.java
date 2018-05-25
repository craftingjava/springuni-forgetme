package com.springuni.forgetme.subscriber.model;

import static com.springuni.forgetme.Mocks.createSubscription;
import static com.springuni.forgetme.core.model.SubscriptionStatus.SUBSCRIPTION_CREATED;
import static com.springuni.forgetme.core.model.SubscriptionStatus.UNSUBSCRIBED;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class SubscriptionTest {

  @Test
  public void givenNewSubscription_whenGetStatusChanges_thenThereIsOneChange() {
    Subscription subscription = createSubscription();
    assertEquals(1, subscription.getStatusChanges().size());
    assertEquals(SUBSCRIPTION_CREATED, subscription.getStatusChanges().get(0).getStatus());
  }

  @Test
  public void givenNewStatus_whenUpdateStatus_thenChangeRecorded() {
    Subscription subscription = createSubscription();
    subscription.updateStatus(UNSUBSCRIBED);
    assertEquals(2, subscription.getStatusChanges().size());
    assertEquals(SUBSCRIPTION_CREATED, subscription.getStatusChanges().get(0).getStatus());
    assertEquals(UNSUBSCRIBED, subscription.getStatusChanges().get(1).getStatus());
  }

  @Test
  public void givenOldStatus_whenUpdateStatus_thenNoChangeRecorded() {
    Subscription subscription = createSubscription();
    subscription.updateStatus(SUBSCRIPTION_CREATED);
    assertEquals(1, subscription.getStatusChanges().size());
    assertEquals(SUBSCRIPTION_CREATED, subscription.getStatusChanges().get(0).getStatus());
  }

}
