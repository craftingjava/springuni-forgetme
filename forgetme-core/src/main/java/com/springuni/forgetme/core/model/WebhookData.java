package com.springuni.forgetme.core.model;

import java.util.UUID;
import lombok.Value;

@Value(staticConstructor = "of")
public class WebhookData {

  private UUID dataHandlerId;
  private String subscriberEmail;
  private SubscriberStatus subscriberStatus;

}
