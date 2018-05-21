package com.springuni.forgetme;

import com.springuni.forgetme.datahandler.DataHandler;
import com.springuni.forgetme.subscriber.Subscriber;
import com.springuni.forgetme.subscriber.Subscription;
import org.springframework.security.core.token.Sha512DigestUtils;

public class Mocks {

  public static final String EMAIL = "test@springuni.com";
  public static final String EMAIL_HASH = Sha512DigestUtils.shaHex(EMAIL);

  public static DataHandler createDataHandler() {
    return new DataHandler("mailerlite");
  }

  public static Subscriber createSubscriber() {
    return new Subscriber(EMAIL);
  }

  public static Subscription createSubscription() {
    Subscriber subscriber = createSubscriber();
    DataHandler dataHandler = createDataHandler();
    return new Subscription(subscriber, dataHandler);
  }

}
