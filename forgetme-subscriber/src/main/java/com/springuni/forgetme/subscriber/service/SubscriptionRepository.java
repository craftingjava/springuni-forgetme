package com.springuni.forgetme.subscriber.service;

import static javax.persistence.LockModeType.PESSIMISTIC_WRITE;

import com.springuni.forgetme.core.orm.BaseRepository;
import com.springuni.forgetme.subscriber.model.Subscription;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

public interface SubscriptionRepository extends BaseRepository<Subscription, UUID> {

  @Override
  @Lock(PESSIMISTIC_WRITE)
  Optional<Subscription> findById(UUID id);

  @Lock(PESSIMISTIC_WRITE)
  @Query("select s from Subscription s where s.subscriber.id = ?1")
  List<Subscription> findBySubscriberId(UUID subscriberId);

}
