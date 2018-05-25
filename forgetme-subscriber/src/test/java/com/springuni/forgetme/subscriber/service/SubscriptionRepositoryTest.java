package com.springuni.forgetme.subscriber.service;

import static com.springuni.forgetme.Mocks.DATA_HANDLER_ID_VALUE;
import static com.springuni.forgetme.Mocks.createSubscription;
import static com.springuni.forgetme.core.model.SubscriptionStatus.UNSUBSCRIBED;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.springuni.forgetme.core.orm.BaseRepositoryTest;
import com.springuni.forgetme.subscriber.model.Subscription;
import java.util.List;
import java.util.UUID;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.junit.Test;

public class SubscriptionRepositoryTest
    extends BaseRepositoryTest<Subscription, SubscriptionRepository> {

  @PersistenceContext
  private EntityManager entityManager;

  @Override
  public void setUp() throws Exception {
    entityManager
        .createNativeQuery("insert into data_handler (id, name, key, version_) values (?, ?, ?, ?)")
        .setParameter(1, DATA_HANDLER_ID_VALUE)
        .setParameter(2, "test")
        .setParameter(3, UUID.randomUUID().toString())
        .setParameter(4, 0)
        .executeUpdate();

    super.setUp();
  }

  @Test
  public void givenSubscriptionStatusChanged_whenFindById_thenChangePersisted() {
    entity.updateStatus(UNSUBSCRIBED);
    saveEntity();
    Subscription subscription = repository.findById(entity.getId()).get();
    assertTrue(subscription.getStatusChanges().stream()
        .anyMatch(it -> it.getStatus().equals(UNSUBSCRIBED)));
  }

  @Test
  public void givenSubscriber_whenFindBySubscriber_thenSubscriptionsReturned() {
    saveEntity();
    List<Subscription> subscriptions = repository
        .findBySubscriberId(entity.getSubscriber().getId());
    assertFalse(subscriptions.isEmpty());
  }

  @Override
  protected Subscription createEntity() {
    return createSubscription();
  }

}
