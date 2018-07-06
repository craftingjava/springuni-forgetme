package com.springuni.forgetme.core.model;

import lombok.Value;

@Value(staticConstructor = "of")
public class WebhookData {

  private String subscriberEmail;
  private SubscriptionStatus subscriptionStatus;

}
