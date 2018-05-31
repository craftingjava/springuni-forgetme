package com.springuni.forgetme.subscriber.model;

import static com.springuni.forgetme.subscriber.Mocks.DATA_HANDLER_ID_VALUE;
import static com.springuni.forgetme.subscriber.Mocks.EVENT_TIMESTAMP_VALUE;
import static com.springuni.forgetme.subscriber.Mocks.createSubscription;
import static com.springuni.forgetme.core.model.SubscriptionStatus.SUBSCRIPTION_CREATED;
import static com.springuni.forgetme.core.model.SubscriptionStatus.SUBSCRIPTION_UPDATED;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;

import com.springuni.forgetme.subscriber.Mocks;
import java.util.ArrayList;
import org.junit.Before;
import org.junit.Test;

public class SubscriberTest {

  private Subscriber subscriber;

  @Before
  public void setUp() {
    subscriber = Mocks.createSubscriber();
  }

  @Test
  public void givenNoSubscriptions_whenUpdateSubscription_thenCreated() {
    subscriber.setSubscriptions(new ArrayList<>());

    subscriber.updateSubscription(
        DATA_HANDLER_ID_VALUE,
        SUBSCRIPTION_CREATED,
        EVENT_TIMESTAMP_VALUE
    );

    assertEquals(SUBSCRIPTION_CREATED, subscriber.getSubscriptions().get(0).getStatus());
  }

  @Test
  public void givenExistingSubscription_whenUpdateSubscription_thenUpdated() {
    Subscription subscription = createSubscription();
    subscriber.setSubscriptions(singletonList(subscription));

    subscriber.updateSubscription(
        DATA_HANDLER_ID_VALUE,
        SUBSCRIPTION_UPDATED,
        EVENT_TIMESTAMP_VALUE
    );

    assertEquals(SUBSCRIPTION_UPDATED, subscriber.getSubscriptions().get(0).getStatus());
  }

}
