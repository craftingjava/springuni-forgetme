package com.springuni.forgetme.datahandler.adapter.mailerlite;

import static com.springuni.forgetme.core.model.SubscriptionStatus.SUBSCRIPTION_CREATED;
import static com.springuni.forgetme.core.model.SubscriptionStatus.SUBSCRIPTION_UPDATED;
import static com.springuni.forgetme.core.model.SubscriptionStatus.UNSUBSCRIBED;
import static java.util.stream.Collectors.toList;

import com.fasterxml.jackson.databind.JsonNode;
import com.springuni.forgetme.core.model.SubscriptionStatus;
import com.springuni.forgetme.datahandler.adapter.AbstractJsonNodeTransformer;
import java.time.Instant;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.StreamSupport;
import org.springframework.integration.transformer.MessageTransformationException;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.util.StringUtils;

public class MailerLiteWebhookDataTransformer extends AbstractJsonNodeTransformer {

  // https://developers.mailerlite.com/docs/webhooks#section-available-events
  static final String EVENT_TYPE_SUBSCRIBER_UNSUBSCRIBE = "subscriber.unsubscribe";
  static final String EVENT_TYPE_SUBSCRIBER_CREATE = "subscriber.create";
  static final String EVENT_TYPE_SUBSCRIBER_UPDATE = "subscriber.update";

  @Override
  protected Collection<Message<JsonNode>> extractEvents(Message<JsonNode> message) {
    JsonNode jsonNode = message.getPayload();
    return StreamSupport.stream(jsonNode.path("events").spliterator(), false)
        .map(it -> MessageBuilder.withPayload(it).copyHeaders(message.getHeaders()).build())
        .collect(toList());
  }

  @Override
  protected String extractSubscriberEmail(Message<JsonNode> event) {
    JsonNode jsonNode = event.getPayload();

    String email = jsonNode.path("data").path("subscriber").path("email").asText();
    if (!StringUtils.hasText(email)) {
      throw new MessageTransformationException(event, "missing email");
    }

    return email;
  }

  @Override
  protected SubscriptionStatus extractSubscriptionStatus(Message<JsonNode> event) {
    JsonNode jsonNode = event.getPayload();

    String eventType = jsonNode.path("type").asText();
    if (!StringUtils.hasText(eventType)) {
      throw new MessageTransformationException(event, "missing event type");
    }

    SubscriptionStatus status;
    switch (eventType) {
      case EVENT_TYPE_SUBSCRIBER_UNSUBSCRIBE:
        status = UNSUBSCRIBED;
        break;
      case EVENT_TYPE_SUBSCRIBER_CREATE:
        status = SUBSCRIPTION_CREATED;
        break;
      case EVENT_TYPE_SUBSCRIBER_UPDATE:
        status = SUBSCRIPTION_UPDATED;
        break;
      default:
        throw new MessageTransformationException(event, "invalid event type: " + eventType);
    }

    return status;
  }

  @Override
  protected Instant extractEventTimestamp(Message<JsonNode> event) {
    JsonNode jsonNode = event.getPayload();
    return Optional.of(jsonNode.path("timestamp").asLong())
        .filter(it -> it > 0)
        .map(Instant::ofEpochSecond)
        .orElseGet(Instant::now);
  }

}
