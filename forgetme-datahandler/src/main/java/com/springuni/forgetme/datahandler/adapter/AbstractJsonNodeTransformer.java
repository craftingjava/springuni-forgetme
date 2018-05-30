package com.springuni.forgetme.datahandler.adapter;

import static com.springuni.forgetme.core.model.MessageHeaderNames.DATA_HANDLER_ID;
import static com.springuni.forgetme.core.model.MessageHeaderNames.EVENT_TIMESTAMP;
import static java.util.stream.Collectors.toList;

import com.fasterxml.jackson.databind.JsonNode;
import com.springuni.forgetme.core.model.SubscriptionStatus;
import com.springuni.forgetme.core.model.WebhookData;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import org.springframework.integration.transformer.GenericTransformer;
import org.springframework.integration.transformer.MessageTransformationException;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

public abstract class AbstractJsonNodeTransformer
    implements GenericTransformer<Message<JsonNode>, List<Message<WebhookData>>> {

  @Override
  public final List<Message<WebhookData>> transform(Message<JsonNode> message) {
    try {
      UUID dataHandlerId = extractDataHandlerId(message);

      List<Message<WebhookData>> results = extractEvents(message).stream()
          .map(it -> transformEvent(dataHandlerId, it))
          .collect(toList());

      if (results == null) {
        return null;
      }
      return results;
    } catch (MessageTransformationException e) {
      throw e;
    } catch (Exception e) {
      throw new MessageTransformationException(message, "failed to transform message", e);
    }
  }

  protected Message<WebhookData> transformEvent(UUID dataHandlerId, Message<JsonNode> event) {
    String subscriberEmail = extractSubscriberEmail(event);
    SubscriptionStatus subscriptionStatus = extractSubscriptionStatus(event);

    WebhookData webhookData = WebhookData.of(dataHandlerId, subscriberEmail, subscriptionStatus);

    Instant eventTimestamp = extractEventTimestamp(event);

    return MessageBuilder.withPayload(webhookData)
        .setHeader(EVENT_TIMESTAMP, eventTimestamp)
        .build();
  }

  protected abstract Collection<Message<JsonNode>> extractEvents(Message<JsonNode> message);

  protected abstract String extractSubscriberEmail(Message<JsonNode> event);

  protected abstract SubscriptionStatus extractSubscriptionStatus(Message<JsonNode> event);

  protected abstract Instant extractEventTimestamp(Message<JsonNode> event);

  private UUID extractDataHandlerId(Message<JsonNode> message) {
    Object dataHandlerId = message.getHeaders().get(DATA_HANDLER_ID);
    if (dataHandlerId instanceof UUID) {
      return (UUID) dataHandlerId;
    }

    dataHandlerId = String.valueOf(dataHandlerId);
    try {
      return UUID.fromString((String) dataHandlerId);
    } catch (IllegalArgumentException e) {
      throw new MessageTransformationException(message, "invalid UUID: " + dataHandlerId, e);
    }
  }

}
