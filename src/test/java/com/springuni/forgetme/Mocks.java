package com.springuni.forgetme;

import static com.springuni.forgetme.subscriber.SubscriberStatus.SUBSCRIBED;

import com.springuni.forgetme.core.model.WebhookData;
import com.springuni.forgetme.datahandler.DataHandler;
import com.springuni.forgetme.subscriber.Subscriber;
import com.springuni.forgetme.subscriber.Subscription;
import org.springframework.security.core.token.Sha512DigestUtils;

public class Mocks {

  public static final String EMAIL = "test@springuni.com";
  public static final String EMAIL_HASH = Sha512DigestUtils.shaHex(EMAIL);

  public static final String DATA_HANDLER_NAME = "mailerlite";

  public static DataHandler createDataHandler() {
    return new DataHandler("mailerlite");
  }

  public static Subscriber createSubscriber() {
    Subscriber subscriber = new Subscriber(EMAIL);
    DataHandler dataHandler = createDataHandler();
    subscriber.updateSubscription(dataHandler, SUBSCRIBED);
    return subscriber;
  }

  public static Subscription createSubscription() {
    Subscriber subscriber = createSubscriber();
    DataHandler dataHandler = createDataHandler();
    return new Subscription(subscriber, dataHandler);
  }

  public static WebhookData createWebhookData() {
    return new WebhookData(DATA_HANDLER_NAME, EMAIL, SUBSCRIBED);
  }

}
