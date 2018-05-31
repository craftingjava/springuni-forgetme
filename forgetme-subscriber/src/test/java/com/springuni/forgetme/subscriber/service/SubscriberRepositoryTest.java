package com.springuni.forgetme.subscriber.service;

import static com.springuni.forgetme.subscriber.Mocks.createSubscriber;
import static org.junit.Assert.assertTrue;

import com.springuni.forgetme.core.orm.BaseRepositoryTest;
import com.springuni.forgetme.subscriber.model.Subscriber;
import java.util.Optional;
import org.junit.Test;

public class SubscriberRepositoryTest extends BaseRepositoryTest<Subscriber, SubscriberRepository> {

  @Test
  public void givenSavedSubscriber_whenFindByEmailHash_thenFound() {
    saveEntity();
    Optional<Subscriber> subscriberOptional = repository.findByEmailHash(entity.getEmailHash());
    assertTrue(subscriberOptional.isPresent());
  }

  @Override
  protected Subscriber createEntity() {
    return createSubscriber();
  }

}
