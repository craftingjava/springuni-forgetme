package com.springuni.forgetme.ui.subscriber;

import com.springuni.forgetme.core.model.SubscriptionStatus;
import java.time.LocalDateTime;
import lombok.Value;

@Value
class SubscriptionViewModel {

  private String dataHandlerName;
  private SubscriptionStatus status;
  private LocalDateTime occurredAt;
  private LocalDateTime processedAt;

}
