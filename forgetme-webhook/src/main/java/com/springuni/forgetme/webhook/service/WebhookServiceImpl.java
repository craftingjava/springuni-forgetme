package com.springuni.forgetme.webhook.service;

import static com.springuni.forgetme.core.model.MessageHeaderNames.DATA_HANDLER_ID;
import static com.springuni.forgetme.core.model.MessageHeaderNames.DATA_HANDLER_NAME;

import com.springuni.forgetme.core.model.EntityNotFoundException;
import com.springuni.forgetme.webhook.model.DataHandler;
import java.util.Map;
import java.util.UUID;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class WebhookServiceImpl implements WebhookService {

  private final DataHandlerRepository dataHandlerRepository;
  private final MessageChannel webhookOutboundChannel;

  public WebhookServiceImpl(
      DataHandlerRepository dataHandlerRepository,
      MessageChannel webhookOutboundChannel) {

    this.dataHandlerRepository = dataHandlerRepository;
    this.webhookOutboundChannel = webhookOutboundChannel;
  }

  @Override
  @Transactional
  public void submitData(UUID dataHandlerId, UUID key, Map<String, Object> data) {
    DataHandler dataHandler = dataHandlerRepository
        .findById(dataHandlerId)
        .orElseThrow(() -> new EntityNotFoundException("id", dataHandlerId));

    // For security reasons, it's safer to throw EntityNotFoundException, which will be exposed as
    // HTTP 404, so that an attacker couldn't differentiate a non-existent ID from a bad key.
    if (!dataHandler.getKey().equals(key)) {
      throw new EntityNotFoundException("key", key);
    }

    Message<Map<String, Object>> message = MessageBuilder
        .withPayload(data)
        .setHeader(DATA_HANDLER_ID, dataHandlerId)
        .setHeader(DATA_HANDLER_NAME, dataHandler.getName())
        .build();

    webhookOutboundChannel.send(message);
  }

}
