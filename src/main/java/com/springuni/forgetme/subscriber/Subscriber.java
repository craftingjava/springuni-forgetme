package com.springuni.forgetme.subscriber;

import static com.springuni.forgetme.subscriber.SubscriberStatus.SUBSCRIBED;
import static javax.persistence.EnumType.STRING;

import com.springuni.forgetme.core.orm.AbstractEntity;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CollectionTable;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
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

  @ElementCollection
  @CollectionTable(name = "subscriber_status_change", joinColumns = @JoinColumn(name = "subscriber_id"))
  private List<SubscriberStatusChange> statusChanges = new ArrayList<>();

  public Subscriber(@NonNull String email) {
    this(email, SUBSCRIBED);
  }

  public Subscriber(@NonNull String email, @NonNull SubscriberStatus status) {
    this.emailHash = Sha512DigestUtils.shaHex(email);
    this.status = status;
  }

  public void setStatus(SubscriberStatus status) {
    this.status = status;
    statusChanges.add(new SubscriberStatusChange(status));
  }

}
