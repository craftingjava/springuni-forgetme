package com.springuni.forgetme.subscriber.service;

import static javax.persistence.LockModeType.OPTIMISTIC_FORCE_INCREMENT;

import com.springuni.forgetme.core.orm.BaseRepository;
import com.springuni.forgetme.subscriber.model.Subscriber;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

public interface SubscriberRepository extends BaseRepository<Subscriber, UUID> {

  @Lock(OPTIMISTIC_FORCE_INCREMENT)
  Optional<Subscriber> findByEmailHash(String emailHash);

  @Lock(OPTIMISTIC_FORCE_INCREMENT)
  @Query("select s from Subscriber s join s.subscriptions ss where ss.id = ?1")
  Optional<Subscriber> findBySubscriptionId(UUID subscriptionId);

}
