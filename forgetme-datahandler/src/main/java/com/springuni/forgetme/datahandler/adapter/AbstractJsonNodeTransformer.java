package com.springuni.forgetme.datahandler.adapter;

import static com.springuni.forgetme.core.model.MessageHeaderNames.DATA_HANDLER_ID;
import static java.util.stream.Collectors.toList;

import com.fasterxml.jackson.databind.JsonNode;
import com.springuni.forgetme.core.model.SubscriptionStatus;
import com.springuni.forgetme.core.model.WebhookData;
import java.util.Collection;
import java.util.UUID;
import org.springframework.integration.transformer.GenericTransformer;
import org.springframework.integration.transformer.MessageTransformationException;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

public abstract class AbstractJsonNodeTransformer
    implements GenericTransformer<Message<JsonNode>, Message<Collection<WebhookData>>> {

  @Override
  public final Message<Collection<WebhookData>> transform(Message<JsonNode> message) {
    try {
      UUID dataHandlerId = extractDataHandlerId(message);

      Collection<WebhookData> results = extractEvents(message).stream()
          .map(it -> doTransform(dataHandlerId, it))
          .collect(toList());

      if (results == null) {
        return null;
      }
      return MessageBuilder.withPayload(results).copyHeaders(message.getHeaders()).build();
    } catch (MessageTransformationException e) {
      throw e;
    } catch (Exception e) {
      throw new MessageTransformationException(message, "failed to transform message", e);
    }
  }

  protected WebhookData doTransform(UUID dataHandlerId, Message<JsonNode> message) {
    String subscriberEmail = extractSubscriberEmail(message);
    SubscriptionStatus subscriptionStatus = extractSubscriptionStatus(message);

    return WebhookData.of(dataHandlerId, subscriberEmail, subscriptionStatus);
  }

  protected abstract Collection<Message<JsonNode>> extractEvents(Message<JsonNode> message);

  protected UUID extractDataHandlerId(Message<JsonNode> message) {
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

  protected abstract String extractSubscriberEmail(Message<JsonNode> message);

  protected abstract SubscriptionStatus extractSubscriptionStatus(Message<JsonNode> message);

}
