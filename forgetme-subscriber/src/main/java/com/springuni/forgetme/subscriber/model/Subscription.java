package com.springuni.forgetme.subscriber.model;

import static com.springuni.forgetme.core.model.SubscriptionStatus.SUBSCRIPTION_CREATED;
import static java.time.ZoneOffset.UTC;
import static java.util.Collections.unmodifiableList;

import com.springuni.forgetme.core.model.SubscriptionStatus;
import com.springuni.forgetme.core.orm.AbstractEntity;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OrderBy;
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

  @Embedded
  private SubscriptionDetail detail;

  @ElementCollection
  @CollectionTable(
      name = "subscription_change",
      joinColumns = @JoinColumn(name = "subscription_id")
  )
  @OrderBy("eventTimestamp DESC")
  private List<SubscriptionDetail> subscriptionChanges = new ArrayList<>();

  public Subscription(UUID dataHandlerId, Subscriber subscriber) {
    this(dataHandlerId, subscriber, SUBSCRIPTION_CREATED);
  }

  public Subscription(
      UUID dataHandlerId, Subscriber subscriber, SubscriptionStatus status) {

    this.subscriber = subscriber;
    this.dataHandlerId = dataHandlerId;

    updateStatus(status);
  }

  void setDetail(SubscriptionDetail detail) {
    this.detail = detail;
  }

  public SubscriptionStatus getStatus() {
    return detail.getStatus();
  }

  public LocalDateTime getEventTimestamp() {
    return detail.getEventTimestamp();
  }

  public void updateStatus(SubscriptionStatus status, LocalDateTime eventTimestamp) {
    detail = new SubscriptionDetail(status, eventTimestamp);
    subscriptionChanges.add(detail);
  }

  public void updateStatus(SubscriptionStatus status) {
    updateStatus(status, LocalDateTime.now(UTC));
  }

  public List<SubscriptionDetail> getSubscriptionChanges() {
    return unmodifiableList(subscriptionChanges);
  }

}
