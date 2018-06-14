package com.springuni.forgetme.subscriber.service;

import static com.springuni.forgetme.core.model.MessageHeaderNames.DATA_HANDLER_ID;
import static com.springuni.forgetme.core.model.MessageHeaderNames.DATA_HANDLER_NAME;
import static com.springuni.forgetme.core.model.MessageHeaderNames.EVENT_TIMESTAMP;
import static com.springuni.forgetme.core.model.SubscriptionStatus.FORGET_FAILED;
import static com.springuni.forgetme.core.model.SubscriptionStatus.FORGET_PENDING;
import static com.springuni.forgetme.core.model.SubscriptionStatus.FORGOTTEN;

import com.springuni.forgetme.core.model.DataHandlerRegistry;
import com.springuni.forgetme.core.model.EntityNotFoundException;
import com.springuni.forgetme.core.model.ForgetRequest;
import com.springuni.forgetme.core.model.ForgetResponse;
import com.springuni.forgetme.core.model.SubscriptionStatus;
import com.springuni.forgetme.core.model.WebhookData;
import com.springuni.forgetme.subscriber.model.Subscriber;
import com.springuni.forgetme.subscriber.model.Subscription;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.security.core.token.Sha512DigestUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubscriberServiceImpl implements SubscriberService {

  private final DataHandlerRegistry dataHandlerRegistry;
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
  @Transactional
  @ServiceActivator(inputChannel = "webhookDataHandlerOutboundChannel")
  public void updateSubscription(
      @NonNull @Payload WebhookData webhookData,
      @NonNull @Header(EVENT_TIMESTAMP) LocalDateTime eventTimestamp) {

    Subscriber newSubscriber = new Subscriber(webhookData.getSubscriberEmail());
    Subscriber subscriber = subscriberRepository.findByEmailHash(newSubscriber.getEmailHash())
        .orElse(newSubscriber);

    subscriber.updateSubscription(
        webhookData.getDataHandlerId(),
        webhookData.getSubscriptionStatus(),
        eventTimestamp
    );

    subscriber = subscriberRepository.save(subscriber);

    log.info("Updated subscriber {}.", subscriber.getId());
  }

  @Override
  @Transactional
  public void requestForget(@NonNull String email) {
    Subscriber subscriber = getSubscriber(email);

    List<Subscription> subscriptions =
        subscriptionRepository.findBySubscriberId(subscriber.getId());

    for (Subscription subscription : subscriptions) {
      SubscriptionStatus status = subscription.getStatus();
      if (FORGOTTEN.equals(status) || FORGET_PENDING.equals(status)) {
        log.warn(
            "Subscription {} has already been forgotten or it's being forgotten",
            subscription.getId()
        );
        continue;
      }

      subscription.updateStatus(FORGET_PENDING);

      UUID dataHandlerId = subscription.getDataHandlerId();
      String dataHandlerName = dataHandlerRegistry.lookup(dataHandlerId);

      Message<ForgetRequest> forgetRequestMessage = MessageBuilder
          .withPayload(new ForgetRequest(subscription.getId(), email))
          .setHeader(DATA_HANDLER_ID, subscription.getDataHandlerId())
          .setHeader(DATA_HANDLER_NAME, dataHandlerName)
          .build();

      subscriberForgetRequestOutboundChannel.send(forgetRequestMessage);

      subscriptionRepository.save(subscription);

      log.info("Forget requested for subscription {}.", subscription.getId());
    }
  }

  @Override
  @Transactional
  @ServiceActivator(inputChannel = "subscriberForgetResponseInboundChannel")
  public void recordForgetResponse(
      @NonNull @Payload ForgetResponse forgetResponse,
      @NonNull @Header(EVENT_TIMESTAMP) LocalDateTime eventTimestamp) {

    UUID id = forgetResponse.getSubscriptionId();
    Subscription subscription = subscriptionRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("id", id));

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
