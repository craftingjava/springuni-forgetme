package com.springuni.forgetme.subscriber;

import static com.springuni.forgetme.subscriber.SubscriberStatus.SUBSCRIBED;
import static com.springuni.forgetme.subscriber.SubscriberStatus.UNSUBSCRIBED;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class SubscriberTest {

  @Test
  public void givenNewSubscriber_whenGetStatusChanges_thenThereIsOneChange() {
    Subscriber subscriber = new Subscriber("test@springuni.com", SUBSCRIBED);
    assertEquals(1, subscriber.getStatusChanges().size());
    assertEquals(SUBSCRIBED, subscriber.getStatusChanges().get(0).getStatus());
  }

  @Test
  public void givenNewStatus_whenUpdateStatus_thenChangeRecorded() {
    Subscriber subscriber = new Subscriber("test@springuni.com", SUBSCRIBED);
    subscriber.updateStatus(UNSUBSCRIBED);
    assertEquals(2, subscriber.getStatusChanges().size());
    assertEquals(SUBSCRIBED, subscriber.getStatusChanges().get(0).getStatus());
    assertEquals(UNSUBSCRIBED, subscriber.getStatusChanges().get(1).getStatus());
  }

  @Test
  public void givenOldStatus_whenUpdateStatus_thenNoChangeRecorded() {
    Subscriber subscriber = new Subscriber("test@springuni.com", SUBSCRIBED);
    subscriber.updateStatus(SUBSCRIBED);
    assertEquals(1, subscriber.getStatusChanges().size());
    assertEquals(SUBSCRIBED, subscriber.getStatusChanges().get(0).getStatus());
  }

}
