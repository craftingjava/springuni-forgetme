package com.springuni.forgetme.subscriber.model;

import static com.springuni.forgetme.Mocks.createSubscription;
import static com.springuni.forgetme.core.model.SubscriptionStatus.SUBSCRIPTION_CREATED;
import static com.springuni.forgetme.core.model.SubscriptionStatus.UNSUBSCRIBED;
import static org.junit.Assert.assertEquals;

import java.time.LocalDateTime;
import org.junit.Test;

public class SubscriptionTest {

  private static final LocalDateTime EVENT_TIMESTAMP = LocalDateTime.MIN;

  @Test
  public void givenNewSubscription_whenGetStatusChanges_thenThereIsOneChange() {
    Subscription subscription = createSubscription();
    assertEquals(1, subscription.getSubscriptionChanges().size());
    assertEquals(SUBSCRIPTION_CREATED, subscription.getSubscriptionChanges().get(0).getStatus());
  }

  @Test
  public void givenNewStatus_whenUpdateStatus_thenChangeRecorded() {
    Subscription subscription = createSubscription();

    subscription.updateStatus(UNSUBSCRIBED, EVENT_TIMESTAMP);
    assertEquals(UNSUBSCRIBED, subscription.getStatus());
    assertEquals(EVENT_TIMESTAMP, subscription.getEventTimestamp());

    assertEquals(2, subscription.getSubscriptionChanges().size());
    assertEquals(SUBSCRIPTION_CREATED, subscription.getSubscriptionChanges().get(0).getStatus());
    assertEquals(UNSUBSCRIBED, subscription.getSubscriptionChanges().get(1).getStatus());
    assertEquals(EVENT_TIMESTAMP, subscription.getSubscriptionChanges().get(1).getEventTimestamp());
  }

}
