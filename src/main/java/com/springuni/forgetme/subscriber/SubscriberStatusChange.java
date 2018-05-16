package com.springuni.forgetme.subscriber;

import static java.time.ZoneOffset.UTC;
import static javax.persistence.EnumType.STRING;

import java.time.LocalDateTime;
import javax.persistence.Embeddable;
import javax.persistence.Enumerated;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Embeddable
@NoArgsConstructor
public class SubscriberStatusChange {

  @Enumerated(STRING)
  private SubscriberStatus status;

  private LocalDateTime changedDate;

  public SubscriberStatusChange(SubscriberStatus status) {
    this.status = status;
    changedDate = LocalDateTime.now(UTC);
  }

}
