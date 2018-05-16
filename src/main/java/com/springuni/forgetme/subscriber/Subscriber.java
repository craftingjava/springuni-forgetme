package com.springuni.forgetme.subscriber;

import static com.springuni.forgetme.subscriber.SubscriberStatus.SUBSCRIBED;
import static javax.persistence.EnumType.STRING;

import com.springuni.forgetme.core.orm.AbstractEntity;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.springframework.security.core.token.Sha512DigestUtils;

@Data
@Entity
@NoArgsConstructor
public class Subscriber extends AbstractEntity {

  private String emailHash;

  @Enumerated(STRING)
  private SubscriberStatus status;

  public Subscriber(@NonNull String email) {
    this(email, SUBSCRIBED);
  }

  public Subscriber(@NonNull String email, @NonNull SubscriberStatus status) {
    this.emailHash = Sha512DigestUtils.shaHex(email);
    this.status = status;
  }

}
