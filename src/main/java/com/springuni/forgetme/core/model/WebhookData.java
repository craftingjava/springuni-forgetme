package com.springuni.forgetme.core.model;

import com.springuni.forgetme.subscriber.SubscriberStatus;
import lombok.Value;

@Value
public class WebhookData {

  private String dataHandlerName;
  private String subscriberEmail;
  private SubscriberStatus subscriberStatus;

}
