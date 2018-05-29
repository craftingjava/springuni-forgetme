package com.springuni.forgetme.subscriber.model;

import static com.springuni.forgetme.core.model.MessageHeaderNames.EVENT_TIMESTAMP;
import static java.util.Collections.unmodifiableList;

import com.springuni.forgetme.core.model.SubscriptionStatus;
import com.springuni.forgetme.core.orm.AbstractEntity;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.security.core.token.Sha512DigestUtils;

@Data
@Entity
@NoArgsConstructor
public class Subscriber extends AbstractEntity {

  private String emailHash;

  @OneToMany(mappedBy = "subscriber", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Subscription> subscriptions = new ArrayList<>();

  public Subscriber(@NonNull String email) {
    this.emailHash = Sha512DigestUtils.shaHex(email);
  }

  public List<Subscription> getSubscriptions() {
    return unmodifiableList(subscriptions);
  }

  void setSubscriptions(List<Subscription> subscriptions) {
    this.subscriptions = subscriptions;
  }

  public void updateSubscription(
      UUID dataHandlerId, SubscriptionStatus status, LocalDateTime eventTimestamp) {

    Subscription subscription = subscriptions.stream()
        .filter(it -> dataHandlerId.equals(it.getDataHandlerId()))
        .findFirst()
        .orElseGet(() -> createSubscription(dataHandlerId));

    subscription.updateStatus(status, eventTimestamp);
  }

  private Subscription createSubscription(UUID dataHandlerId) {
    Subscription newSubscription = new Subscription(dataHandlerId, this);
    subscriptions.add(newSubscription);
    return newSubscription;
  }

}
