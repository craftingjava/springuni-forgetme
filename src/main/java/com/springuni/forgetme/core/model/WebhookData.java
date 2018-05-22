package com.springuni.forgetme.core.model;

import com.springuni.forgetme.subscriber.SubscriberStatus;
import java.util.UUID;
import lombok.Value;

@Value(staticConstructor = "of")
public class WebhookData {

  private UUID dataHandlerId;
  private String subscriberEmail;
  private SubscriberStatus subscriberStatus;

}
