package com.springuni.forgetme.subscriber.model;

import static com.springuni.forgetme.core.model.SubscriptionStatus.FORGOTTEN;
import static com.springuni.forgetme.subscriber.Mocks.EVENT_TIMESTAMP_VALUE;
import static com.springuni.forgetme.subscriber.Mocks.createSubscription;
import static com.springuni.forgetme.core.model.SubscriptionStatus.SUBSCRIPTION_CREATED;
import static com.springuni.forgetme.core.model.SubscriptionStatus.UNSUBSCRIBED;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class SubscriptionTest {

  @Test
  public void givenNewSubscription_whenGetStatusChanges_thenThereIsOneChange() {
    Subscription subscription = createSubscription();
    assertEquals(1, subscription.getSubscriptionChanges().size());
    assertEquals(SUBSCRIPTION_CREATED, subscription.getSubscriptionChanges().get(0).getStatus());
  }

  @Test
  public void givenNewStatus_whenUpdateStatus_thenChangeRecorded() {
    Subscription subscription = createSubscription();

    subscription.updateStatus(UNSUBSCRIBED, EVENT_TIMESTAMP_VALUE);
    assertEquals(UNSUBSCRIBED, subscription.getStatus());
    assertEquals(EVENT_TIMESTAMP_VALUE, subscription.getEventTimestamp());

    assertEquals(2, subscription.getSubscriptionChanges().size());
    assertEquals(SUBSCRIPTION_CREATED, subscription.getSubscriptionChanges().get(0).getStatus());
    assertEquals(UNSUBSCRIBED, subscription.getSubscriptionChanges().get(1).getStatus());
    assertEquals(EVENT_TIMESTAMP_VALUE,
        subscription.getSubscriptionChanges().get(1).getEventTimestamp());
  }

  @Test
  public void givenForgottenSubscription_whenIsActive_thenTrue() {
    Subscription subscription = createSubscription();

    subscription.updateStatus(FORGOTTEN, EVENT_TIMESTAMP_VALUE);
    assertFalse(subscription.isActive());
  }

  @Test
  public void givenActiveSubscription_whenIsActive_thenTrue() {
    Subscription subscription = createSubscription();

    assertTrue(subscription.isActive());
  }

}
