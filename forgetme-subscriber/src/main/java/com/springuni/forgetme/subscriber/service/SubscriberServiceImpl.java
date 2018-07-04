package com.springuni.forgetme.subscriber.service;

import static com.springuni.forgetme.core.model.MessageHeaderNames.DATA_HANDLER_ID;
import static com.springuni.forgetme.core.model.MessageHeaderNames.DATA_HANDLER_NAME;
import static com.springuni.forgetme.core.model.MessageHeaderNames.EVENT_TIMESTAMP;
import static com.springuni.forgetme.core.model.SubscriptionStatus.FORGET_FAILED;
import static com.springuni.forgetme.core.model.SubscriptionStatus.FORGET_PENDING;
import static com.springuni.forgetme.core.model.SubscriptionStatus.FORGOTTEN;

import com.springuni.forgetme.core.adapter.DataHandlerRegistration;
import com.springuni.forgetme.core.adapter.DataHandlerRegistry;
import com.springuni.forgetme.core.model.EntityNotFoundException;
import com.springuni.forgetme.core.model.ForgetRequest;
import com.springuni.forgetme.core.model.ForgetResponse;
import com.springuni.forgetme.core.model.SubscriptionStatus;
import com.springuni.forgetme.core.model.WebhookData;
import com.springuni.forgetme.subscriber.model.Subscriber;
import com.springuni.forgetme.subscriber.model.Subscription;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.TransientDataAccessException;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.retry.annotation.Retryable;
import org.springframework.security.core.token.Sha512DigestUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class SubscriberServiceImpl implements SubscriberService {

  private final SubscriberRepository subscriberRepository;
  private final SubscriptionRepository subscriptionRepository;
  private final MessageChannel subscriberForgetRequestOutboundChannel;


  @Override
  public Optional<Subscriber> findSubscriber(String email) {
    String emailHash = Sha512DigestUtils.shaHex(email);
    return subscriberRepository.findByEmailHash(emailHash);
  }

  @Override
  public Subscriber getSubscriber(@NonNull String email) {
    String emailHash = Sha512DigestUtils.shaHex(email);
    return subscriberRepository.findByEmailHash(emailHash)
        .orElseThrow(() -> new EntityNotFoundException("emailHash", emailHash));
  }

  @Override
  @Retryable(include = TransientDataAccessException.class)
  @ServiceActivator(inputChannel = "webhookDataHandlerOutboundChannel")
  public void updateSubscription(
      @NonNull @Payload WebhookData webhookData,
      @NonNull @Header(DATA_HANDLER_NAME) String dataHandlerName,
      @NonNull @Header(EVENT_TIMESTAMP) LocalDateTime eventTimestamp) {

    Subscriber newSubscriber = new Subscriber(webhookData.getSubscriberEmail());
    Subscriber subscriber = subscriberRepository.findByEmailHash(newSubscriber.getEmailHash())
        .orElse(newSubscriber);

    subscriber.updateSubscription(
        dataHandlerName,
        webhookData.getSubscriptionStatus(),
        eventTimestamp
    );

    subscriber = subscriberRepository.save(subscriber);

    log.info("Updated subscriber {}.", subscriber.getId());
  }

  @Override
  @Retryable(include = TransientDataAccessException.class)
  public void requestForget(@NonNull String email) {
    Subscriber subscriber = getSubscriber(email);

    List<Message<ForgetRequest>> forgetRequestMessages = new ArrayList<>();

    for (Subscription subscription : subscriber.getSubscriptions()) {
      SubscriptionStatus status = subscription.getStatus();
      if (FORGOTTEN.equals(status) || FORGET_PENDING.equals(status)) {
        log.warn(
            "Subscription {} has already been forgotten or it's being forgotten",
            subscription.getId()
        );
        continue;
      }

      subscription.updateStatus(FORGET_PENDING);

      Message<ForgetRequest> forgetRequestMessage = MessageBuilder
          .withPayload(new ForgetRequest(subscription.getId(), email))
          .setHeader(DATA_HANDLER_NAME, subscription.getDataHandlerName())
          .build();

      forgetRequestMessages.add(forgetRequestMessage);

      subscriptionRepository.save(subscription);

      log.info("Forget requested for subscription {}.", subscription.getId());
    }

    TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {

      @Override
      public void afterCommit() {
        forgetRequestMessages.forEach(subscriberForgetRequestOutboundChannel::send);
      }

    });
  }

  @Override
  @Retryable(include = TransientDataAccessException.class)
  @ServiceActivator(inputChannel = "subscriberForgetResponseInboundChannel")
  public void recordForgetResponse(
      @NonNull @Payload ForgetResponse forgetResponse,
      @NonNull @Header(EVENT_TIMESTAMP) LocalDateTime eventTimestamp) {

    UUID subscriptionId = forgetResponse.getSubscriptionId();
    Subscriber subscriber = subscriberRepository.findBySubscriptionId(subscriptionId)
        .orElseThrow(() -> new EntityNotFoundException("subscriptionId", subscriptionId));

    Subscription subscription = subscriber.getSubscriptions()
        .stream()
        .filter(it -> subscriptionId.equals(it.getId()))
        .findFirst()
        .orElseThrow(() -> new EntityNotFoundException("subscriptionId", subscriptionId));

    SubscriptionStatus status = forgetResponse.isAcknowledged() ? FORGOTTEN : FORGET_FAILED;

    subscription.updateStatus(status, eventTimestamp);

    subscriptionRepository.save(subscription);

    log.info(
        "Forget recorded for subscription {}; new status is {}.",
        subscription.getId(),
        subscription.getStatus()
    );
  }

}
