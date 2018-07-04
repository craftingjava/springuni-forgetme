package com.springuni.forgetme.webhook.service;

import static com.springuni.forgetme.core.model.MessageHeaderNames.DATA_HANDLER_NAME;

import com.springuni.forgetme.core.adapter.DataHandlerRegistration;
import com.springuni.forgetme.core.adapter.DataHandlerRegistry;
import com.springuni.forgetme.core.model.EntityNotFoundException;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class WebhookServiceImpl implements WebhookService {

  private final DataHandlerRegistry dataHandlerRegistry;
  private final MessageChannel webhookOutboundChannel;

  @Override
  @Transactional
  public void submitData(String dataHandlerName, UUID key, Map<String, Object> data) {
    // For security reasons, it's safer to throw EntityNotFoundException, which will be exposed as
    // HTTP 404, so that an attacker couldn't differentiate a non-existent ID from a bad key.
    /*
    if (!dataHandler.getKey().equals(key)) {
      throw new EntityNotFoundException("key", key);
    }
    */
    Optional<DataHandlerRegistration> dataHandlerRegistration =
        dataHandlerRegistry.lookup(dataHandlerName);

    if (!dataHandlerRegistration.isPresent()) {
      throw new EntityNotFoundException("dataHandlerName", dataHandlerName);
    }

    Message<Map<String, Object>> message = MessageBuilder
        .withPayload(data)
        .setHeader(DATA_HANDLER_NAME, dataHandlerName)
        .build();

    webhookOutboundChannel.send(message);
  }

}
