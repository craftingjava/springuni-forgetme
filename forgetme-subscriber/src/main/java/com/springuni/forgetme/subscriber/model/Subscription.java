package com.springuni.forgetme.subscriber.model;

import static com.springuni.forgetme.core.model.SubscriptionStatus.SUBSCRIBED;
import static java.util.Collections.unmodifiableList;
import static javax.persistence.EnumType.STRING;

import com.springuni.forgetme.core.model.SubscriptionStatus;
import com.springuni.forgetme.core.orm.AbstractEntity;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
public class Subscription extends AbstractEntity {

  private UUID dataHandlerId;

  @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  @JoinColumn(name = "subscriber_id")
  private Subscriber subscriber;

  @Enumerated(STRING)
  private SubscriptionStatus status;

  @ElementCollection
  @CollectionTable(
      name = "subscription_status_change",
      joinColumns = @JoinColumn(name = "subscription_id")
  )
  private List<SubscriptionStatusChange> statusChanges = new ArrayList<>();

  public Subscription(UUID dataHandlerId, Subscriber subscriber) {
    this(dataHandlerId, subscriber, SUBSCRIBED);
  }

  public Subscription(
      UUID dataHandlerId, Subscriber subscriber, SubscriptionStatus status) {

    this.subscriber = subscriber;
    this.dataHandlerId = dataHandlerId;

    updateStatus(status);
  }

  void setStatus(SubscriptionStatus status) {
    this.status = status;
  }

  public void updateStatus(SubscriptionStatus status) {
    if (status.equals(this.status)) {
      return;
    }

    this.status = status;
    statusChanges.add(new SubscriptionStatusChange(status));
  }

  public List<SubscriptionStatusChange> getStatusChanges() {
    return unmodifiableList(statusChanges);
  }

}
