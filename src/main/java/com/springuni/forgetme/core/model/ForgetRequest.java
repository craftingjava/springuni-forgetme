package com.springuni.forgetme.core.model;

import java.beans.ConstructorProperties;
import java.util.UUID;
import lombok.Data;

@Data
public class ForgetRequest {

  private final UUID subscriptionId;
  private final String subscriberEmail;

  @ConstructorProperties({"subscriptionId", "subscriberEmail"})
  public ForgetRequest(UUID subscriptionId, String subscriberEmail) {
    this.subscriptionId = subscriptionId;
    this.subscriberEmail = subscriberEmail;
  }

}
