package com.springuni.forgetme.core.model;

import java.util.UUID;
import lombok.Value;

@Value
public class ForgetResponse {

  private UUID subscriptionId;
  private boolean acknowledged;

}
