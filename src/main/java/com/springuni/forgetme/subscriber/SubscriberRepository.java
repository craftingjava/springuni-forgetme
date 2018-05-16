package com.springuni.forgetme.subscriber;

import com.springuni.forgetme.core.orm.BaseRepository;
import java.util.Optional;
import java.util.UUID;

public interface SubscriberRepository extends BaseRepository<Subscriber, UUID> {

  Optional<Subscriber> findByEmailHash(String emailHash);

}
