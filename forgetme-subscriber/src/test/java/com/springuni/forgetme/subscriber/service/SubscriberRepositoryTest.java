package com.springuni.forgetme.subscriber.service;

import static com.springuni.forgetme.subscriber.Mocks.DATA_HANDLER_ID_VALUE;
import static com.springuni.forgetme.subscriber.Mocks.createSubscription;
import static org.junit.Assert.assertTrue;

import com.springuni.forgetme.core.orm.BaseRepositoryTest;
import com.springuni.forgetme.subscriber.model.Subscriber;
import com.springuni.forgetme.subscriber.model.Subscription;
import java.util.Optional;
import java.util.UUID;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.junit.Before;
import org.junit.Test;

public class SubscriberRepositoryTest extends BaseRepositoryTest<Subscriber, SubscriberRepository> {

  @PersistenceContext
  private EntityManager entityManager;

  @Override
  @Before
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
  public void givenSavedSubscriber_whenFindByEmailHash_thenFound() {
    saveEntity();
    Optional<Subscriber> subscriberOptional = repository.findByEmailHash(entity.getEmailHash());
    assertTrue(subscriberOptional.isPresent());
  }

  @Test
  public void givenExistingSubscription_whenFindBySubscriptionId_thenSubscriberReturned() {
    saveEntity();
    UUID subscriptionId = entity.getSubscriptions().get(0).getId();
    Optional<Subscriber> subscriberOptional = repository.findBySubscriptionId(subscriptionId);
    assertTrue(subscriberOptional.isPresent());
  }

  @Override
  protected Subscriber createEntity() {
    Subscription subscription = createSubscription();
    return subscription.getSubscriber();
  }

}
