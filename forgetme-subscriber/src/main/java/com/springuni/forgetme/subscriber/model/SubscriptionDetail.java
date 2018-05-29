package com.springuni.forgetme.subscriber.model;

import static java.time.ZoneOffset.UTC;
import static javax.persistence.EnumType.STRING;

import com.springuni.forgetme.core.model.SubscriptionStatus;
import java.time.LocalDateTime;
import javax.persistence.Embeddable;
import javax.persistence.Enumerated;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@Embeddable
@NoArgsConstructor
public class SubscriptionDetail {

  @Enumerated(STRING)
  private SubscriptionStatus status;

  private LocalDateTime eventTimestamp;

  public SubscriptionDetail(
      @NonNull SubscriptionStatus status, @NonNull LocalDateTime eventTimestamp) {

    this.status = status;
    this.eventTimestamp = eventTimestamp;
  }

  public SubscriptionDetail(@NonNull SubscriptionStatus status) {
    this(status, LocalDateTime.now(UTC));
  }

}
