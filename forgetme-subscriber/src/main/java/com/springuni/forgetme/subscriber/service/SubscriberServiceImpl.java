package com.springuni.forgetme.subscriber.service;

import static com.springuni.forgetme.core.model.MessageHeaderNames.DATA_HANDLER_ID;
import static com.springuni.forgetme.core.model.MessageHeaderNames.DATA_HANDLER_NAME;
import static com.springuni.forgetme.core.model.SubscriberStatus.FORGET_FAILED;
import static com.springuni.forgetme.core.model.SubscriberStatus.FORGET_PENDING;
import static com.springuni.forgetme.core.model.SubscriberStatus.FORGOTTEN;

import com.springuni.forgetme.core.model.EntityNotFoundException;
import com.springuni.forgetme.core.model.ForgetRequest;
import com.springuni.forgetme.core.model.ForgetResponse;
import com.springuni.forgetme.core.model.SubscriberStatus;
import com.springuni.forgetme.core.model.WebhookData;
import com.springuni.forgetme.subscriber.model.Subscriber;
import com.springuni.forgetme.subscriber.model.Subscription;
import java.util.List;
import java.util.UUID;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.security.core.token.Sha512DigestUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubscriberServiceImpl implements SubscriberService {

  private final SubscriberRepository subscriberRepository;
  private final SubscriptionRepository subscriptionRepository;
  private final MessageChannel subscriberForgetRequestOutboundChannel;

  @Override
  public Subscriber getSubscriber(@NonNull String email) {
    String emailHash = Sha512DigestUtils.shaHex(email);

    return subscriberRepository.findByEmailHash(emailHash)
        .orElseThrow(() -> new EntityNotFoundException("emailHash", emailHash));
  }

  @Override
  @Transactional
  @ServiceActivator(inputChannel = "webhookDataHandlerOutboundChannel")
  public void updateSubscription(@NonNull WebhookData webhookData) {
    Subscriber newSubscriber = new Subscriber(webhookData.getSubscriberEmail());
    Subscriber subscriber = subscriberRepository.findByEmailHash(newSubscriber.getEmailHash())
        .orElse(newSubscriber);

    subscriber
        .updateSubscription(webhookData.getDataHandlerId(), webhookData.getSubscriberStatus());

    subscriber = subscriberRepository.save(subscriber);

    log.info("Updated subscriber {}.", subscriber.getId());
  }

  @Override
  @Transactional
  public void requestForget(@NonNull String email) {
    Subscriber subscriber = getSubscriber(email);

    List<Subscription> subscriptions = subscriber.getSubscriptions();

    subscriptions.forEach(it -> {
      it.updateStatus(FORGET_PENDING);

      Message<ForgetRequest> forgetRequestMessage = MessageBuilder
          .withPayload(new ForgetRequest(it.getId(), email))
          .setHeader(DATA_HANDLER_NAME, "mailerlite") // TODO get DH name
          .setHeader(DATA_HANDLER_ID, it.getDataHandlerId())
          .build();

      subscriberForgetRequestOutboundChannel.send(forgetRequestMessage);

      subscriptionRepository.save(it);

      log.info("Forget requested for subscription {}.", it.getId());
    });
  }

  @Override
  @Transactional
  @ServiceActivator(inputChannel = "subscriberForgetResponseInboundChannel")
  public void recordForgetResponse(@NonNull ForgetResponse forgetResponse) {
    UUID id = forgetResponse.getSubscriptionId();
    Subscription subscription = subscriptionRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("id", id));

    SubscriberStatus status = forgetResponse.isAcknowledged() ? FORGOTTEN : FORGET_FAILED;

    subscription.updateStatus(status);

    subscriptionRepository.save(subscription);

    log.info(
        "Forget recorded for subscription {}; new status is {}.",
        subscription.getId(),
        subscription.getStatus()
    );
  }

}
