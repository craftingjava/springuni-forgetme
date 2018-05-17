package com.springuni.forgetme.subscriber;

import static com.springuni.forgetme.subscriber.SubscriberStatus.UNSUBSCRIBED;
import static org.junit.Assert.assertTrue;

import com.springuni.forgetme.core.orm.BaseRepositoryTest;
import java.util.Optional;
import java.util.UUID;
import org.junit.Test;

public class SubscriberRepositoryTest extends BaseRepositoryTest<Subscriber, SubscriberRepository> {

  @Test
  public void givenSubscriberStatusChanged_whenFindById_thenChangePersisted() {
    entity.setStatus(UNSUBSCRIBED);
    saveEntity();
    Subscriber subscriber = repository.findById(entity.getId()).get();
    assertTrue(subscriber.getStatusChanges().stream().anyMatch(it -> it.getStatus().equals(UNSUBSCRIBED)));
  }

  @Test
  public void givenSavedSubscriber_whenFindByEmailHash_thenFound() {
    saveEntity();
    Optional<Subscriber> subscriberOptional = repository.findByEmailHash(entity.getEmailHash());
    assertTrue(subscriberOptional.isPresent());
  }

  @Override
  protected Subscriber createEntity() throws Exception {
    return new Subscriber("github@laszlocsontos.com");
  }

}
