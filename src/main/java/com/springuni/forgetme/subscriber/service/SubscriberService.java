package com.springuni.forgetme.subscriber.service;

import com.springuni.forgetme.core.model.WebhookData;
import com.springuni.forgetme.subscriber.model.Subscriber;

public interface SubscriberService {

  Subscriber getSubscriber(String email);

  void updateSubscription(WebhookData webhookData);

}
