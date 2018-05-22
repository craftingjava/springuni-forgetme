package com.springuni.forgetme.subscriber.service;

import com.springuni.forgetme.core.orm.BaseRepository;
import com.springuni.forgetme.subscriber.model.Subscriber;
import java.util.Optional;
import java.util.UUID;

public interface SubscriberRepository extends BaseRepository<Subscriber, UUID> {

  Optional<Subscriber> findByEmailHash(String emailHash);

}
