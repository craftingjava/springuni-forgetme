package com.springuni.forgetme.core.model;

import java.util.UUID;
import lombok.Value;

@Value
public class ForgetRequest {

  private UUID subscriptionId;
  private String subscriberEmail;

}
