package com.springuni.forgetme.subscriber;

import static com.springuni.forgetme.Mocks.createSubscriber;
import static org.junit.Assert.assertTrue;

import com.springuni.forgetme.core.orm.BaseRepositoryTest;
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
  protected Subscriber createEntity() throws Exception {
    return createSubscriber();
  }

}
