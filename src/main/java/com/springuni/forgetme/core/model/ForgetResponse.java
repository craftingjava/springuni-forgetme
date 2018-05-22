package com.springuni.forgetme.core.model;

import java.beans.ConstructorProperties;
import java.util.UUID;
import lombok.Data;
import lombok.Getter;
import lombok.Value;

@Data
public class ForgetResponse {

  private final UUID subscriptionId;
  private final boolean acknowledged;

  @ConstructorProperties({"subscriptionId", "acknowledged"})
  public ForgetResponse(UUID subscriptionId, boolean acknowledged) {
    this.subscriptionId = subscriptionId;
    this.acknowledged = acknowledged;
  }

}
