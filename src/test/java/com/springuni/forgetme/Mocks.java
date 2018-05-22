package com.springuni.forgetme;

import static com.springuni.forgetme.subscriber.SubscriberStatus.SUBSCRIBED;

import com.springuni.forgetme.core.model.WebhookData;
import com.springuni.forgetme.datahandler.DataHandler;
import com.springuni.forgetme.subscriber.Subscriber;
import com.springuni.forgetme.subscriber.Subscription;
import java.util.UUID;
import org.springframework.security.core.token.Sha512DigestUtils;

public class Mocks {

  public static final String EMAIL = "test@springuni.com";
  public static final String EMAIL_HASH = Sha512DigestUtils.shaHex(EMAIL);

  public static final UUID DATA_HANDLER_ID = UUID.randomUUID();
  public static final String DATA_HANDLER_NAME = "mailerlite";

  public static DataHandler createDataHandler() {
    return new DataHandler("mailerlite");
  }

  public static Subscriber createSubscriber() {
    Subscriber subscriber = new Subscriber(EMAIL);
    DataHandler dataHandler = createDataHandler();
    subscriber.updateSubscription(DATA_HANDLER_ID, SUBSCRIBED);
    return subscriber;
  }

  public static Subscription createSubscription() {
    Subscriber subscriber = createSubscriber();
    DataHandler dataHandler = createDataHandler();
    return new Subscription(subscriber, DATA_HANDLER_ID);
  }

  public static WebhookData createWebhookData() {
    return WebhookData.of(DATA_HANDLER_ID, EMAIL, SUBSCRIBED);
  }

}
