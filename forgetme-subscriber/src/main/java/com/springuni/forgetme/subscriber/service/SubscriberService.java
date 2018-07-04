package com.springuni.forgetme.subscriber.service;

import com.springuni.forgetme.core.model.ForgetResponse;
import com.springuni.forgetme.core.model.WebhookData;
import com.springuni.forgetme.subscriber.model.Subscriber;
import java.time.LocalDateTime;
import java.util.Optional;

public interface SubscriberService {

  Optional<Subscriber> findSubscriber(String email);

  Subscriber getSubscriber(String email);

  void updateSubscription(
      WebhookData webhookData, String dataHandlerName, LocalDateTime eventTimestamp
  );

  void requestForget(String email);

  void recordForgetResponse(ForgetResponse forgetResponse, LocalDateTime eventTimestamp);

}
