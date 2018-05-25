package com.springuni.forgetme.subscriber.model;

import static com.springuni.forgetme.Mocks.DATA_HANDLER_ID_VALUE;
import static com.springuni.forgetme.Mocks.createSubscription;
import static com.springuni.forgetme.core.model.SubscriptionStatus.SUBSCRIBED;
import static com.springuni.forgetme.core.model.SubscriptionStatus.UNSUBSCRIBED;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;

import com.springuni.forgetme.Mocks;
import java.util.ArrayList;
import org.junit.Before;
import org.junit.Test;

public class SubscriberTest {

  private Subscriber subscriber;

  @Before
  public void setUp() throws Exception {
    subscriber = Mocks.createSubscriber();
  }

  @Test
  public void givenNoSubscriptions_whenUpdateSubscription_thenCreated() {
    subscriber.setSubscriptions(new ArrayList<>());

    subscriber.updateSubscription(DATA_HANDLER_ID_VALUE, SUBSCRIBED);

    assertEquals(SUBSCRIBED, subscriber.getSubscriptions().get(0).getStatus());
  }

  @Test
  public void givenExistingSubscription_whenUpdateSubscription_thenUpdated() {
    Subscription subscription = createSubscription();
    subscription.setStatus(UNSUBSCRIBED);
    subscriber.setSubscriptions(singletonList(subscription));

    subscriber.updateSubscription(DATA_HANDLER_ID_VALUE, SUBSCRIBED);

    assertEquals(SUBSCRIBED, subscriber.getSubscriptions().get(0).getStatus());
  }

}
