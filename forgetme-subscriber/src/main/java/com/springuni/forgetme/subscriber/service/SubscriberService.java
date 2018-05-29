package com.springuni.forgetme.subscriber.service;

import com.springuni.forgetme.core.model.ForgetResponse;
import com.springuni.forgetme.core.model.WebhookData;
import com.springuni.forgetme.subscriber.model.Subscriber;
import java.time.LocalDateTime;

public interface SubscriberService {

  Subscriber getSubscriber(String email);

  void updateSubscription(WebhookData webhookData);

  void requestForget(String email);

  void recordForgetResponse(ForgetResponse forgetResponse, LocalDateTime eventTimestamp);

}
