package com.springuni.forgetme.subscriber.service;

import com.springuni.forgetme.core.orm.BaseRepository;
import com.springuni.forgetme.subscriber.model.Subscription;
import java.util.UUID;

public interface SubscriptionRepository extends BaseRepository<Subscription, UUID> {

}
