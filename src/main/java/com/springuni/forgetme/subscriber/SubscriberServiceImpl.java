package com.springuni.forgetme.subscriber;

import com.springuni.forgetme.core.model.EntityNotFoundException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.security.core.token.Sha512DigestUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SubscriberServiceImpl implements SubscriberService {

  private final SubscriberRepository subscriberRepository;

  @Override
  public Subscriber getSubscriber(@NonNull String email) {
    String emailHash = Sha512DigestUtils.shaHex(email);

    return subscriberRepository.findByEmailHash(emailHash)
        .orElseThrow(() -> new EntityNotFoundException("emailHash", emailHash));
  }

  @Override
  @Transactional
  @ServiceActivator(inputChannel = "subscriberInboundChannel")
  public void updateSubscriber(@NonNull Subscriber newSubscriber) {
    Subscriber subscriber = subscriberRepository.findByEmailHash(newSubscriber.getEmailHash())
        .orElse(newSubscriber);

    // TODO: Fix this once message types have been introduced
    if (!subscriber.isNew()) {
      // newSubscriber.updateStatus(newSubscriber.getStatus());
    }

    subscriberRepository.save(subscriber);
  }

}
