package com.springuni.forgetme.ui.subscriber;

import java.time.LocalDateTime;
import lombok.Value;

@Value
class SubscriberViewModel {

  private String emailHash;
  private LocalDateTime createdDate;
  private LocalDateTime lastModifiedDate;

}
