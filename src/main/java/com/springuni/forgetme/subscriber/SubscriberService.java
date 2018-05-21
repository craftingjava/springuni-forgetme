package com.springuni.forgetme.subscriber;

import com.springuni.forgetme.core.model.WebhookData;

public interface SubscriberService {

  Subscriber getSubscriber(String email);

  void updateSubscription(WebhookData webhookData);

}
