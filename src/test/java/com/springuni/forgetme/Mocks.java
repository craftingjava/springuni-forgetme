package com.springuni.forgetme;

import static com.springuni.forgetme.subscriber.SubscriberStatus.SUBSCRIBED;

import com.springuni.forgetme.subscriber.Subscriber;
import org.springframework.security.core.token.Sha512DigestUtils;

public class Mocks {

  public static final String EMAIL = "test@springuni.com";
  public static final String EMAIL_HASH = Sha512DigestUtils.shaHex(EMAIL);

  public static Subscriber createSubscriber() {
    return new Subscriber(EMAIL, SUBSCRIBED);
  }

}
