package com.springuni.forgetme.subscriber.model;

import static java.time.ZoneOffset.UTC;
import static javax.persistence.EnumType.STRING;

import com.springuni.forgetme.core.model.SubscriptionStatus;
import java.time.LocalDateTime;
import javax.persistence.Embeddable;
import javax.persistence.Enumerated;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Embeddable
@NoArgsConstructor
public class SubscriptionStatusChange {

  @Enumerated(STRING)
  private SubscriptionStatus status;

  private LocalDateTime changedDate;

  public SubscriptionStatusChange(SubscriptionStatus status) {
    this.status = status;
    changedDate = LocalDateTime.now(UTC);
  }

}
