package com.springuni.forgetme;

import com.springuni.forgetme.subscriber.model.Subscriber;
import com.springuni.forgetme.subscriber.model.Subscription;
import java.util.UUID;
import org.springframework.security.core.token.Sha512DigestUtils;

public class Mocks {

  public static final String EMAIL = "test@springuni.com";
  public static final String EMAIL_HASH = Sha512DigestUtils.shaHex(EMAIL);

  public static final UUID DATA_HANDLER_ID_VALUE =
      UUID.fromString("e408b7d4-49dc-427e-ad60-e5d8a0dc5925");

  public static Subscriber createSubscriber() {
    Subscriber subscriber = new Subscriber(EMAIL);
    return subscriber;
  }

  public static Subscription createSubscription() {
    Subscriber subscriber = createSubscriber();
    return new Subscription(DATA_HANDLER_ID_VALUE, subscriber);
  }

}
