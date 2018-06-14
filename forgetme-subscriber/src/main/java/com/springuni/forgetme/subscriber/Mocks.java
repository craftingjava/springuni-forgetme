package com.springuni.forgetme.subscriber;

import static com.springuni.forgetme.core.model.SubscriptionStatus.SUBSCRIPTION_CREATED;

import com.springuni.forgetme.subscriber.model.Subscriber;
import com.springuni.forgetme.subscriber.model.Subscription;
import java.time.LocalDateTime;
import java.util.UUID;
import org.springframework.security.core.token.Sha512DigestUtils;

public class Mocks {

  public static final String EMAIL = "test@springuni.com";
  public static final String EMAIL_HASH = Sha512DigestUtils.shaHex(EMAIL);

  public static final String DATA_HANDLER_NAME_VALUE = "mailerlite";

  public static final UUID DATA_HANDLER_ID_VALUE =
      UUID.fromString("e408b7d4-49dc-427e-ad60-e5d8a0dc5925");

  public static final UUID SUBSCRIBER_ID_VALUE =
      UUID.fromString("f408b7d4-49dc-427e-ad60-e5d8a0dc5925");

  public static final UUID SUBSCRIPTION_ID_VALUE =
      UUID.fromString("a408b7d4-49dc-427e-ad60-e5d8a0dc5925");

  public static final LocalDateTime EVENT_TIMESTAMP_VALUE = LocalDateTime.MIN;

  public static Subscriber createSubscriber() {
    Subscriber subscriber = new Subscriber(EMAIL);
    return subscriber;
  }

  public static Subscription createSubscription() {
    Subscriber subscriber = createSubscriber();
    subscriber.updateSubscription(DATA_HANDLER_ID_VALUE, SUBSCRIPTION_CREATED, LocalDateTime.now());
    return subscriber.getSubscriptions().get(0);
  }

}
