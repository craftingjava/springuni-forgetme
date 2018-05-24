package com.springuni.forgetme.datahandler.adapter.mailerlite;

import static com.springuni.forgetme.core.model.SubscriberStatus.SUBSCRIBED;
import static com.springuni.forgetme.core.model.SubscriberStatus.UNSUBSCRIBED;
import static java.util.stream.Collectors.toList;

import com.fasterxml.jackson.databind.JsonNode;
import com.springuni.forgetme.core.model.SubscriberStatus;
import com.springuni.forgetme.datahandler.adapter.AbstractJsonNodeTransformer;
import java.util.Collection;
import java.util.stream.StreamSupport;
import org.springframework.integration.transformer.MessageTransformationException;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.util.StringUtils;

public class MailerLiteWebhookDataTransformer extends AbstractJsonNodeTransformer {

  // https://developers.mailerlite.com/docs/webhooks#section-available-events
  static final String EVENT_TYPE_UNSUBSCRIBED = "subscriber.unsubscribe";
  static final String EVENT_TYPE_SUBSCRIBED = "subscriber.create";

  @Override
  protected Collection<Message<JsonNode>> extractEvents(Message<JsonNode> message) {
    JsonNode jsonNode = message.getPayload();
    return StreamSupport.stream(jsonNode.path("events").spliterator(), false)
        .map(it -> MessageBuilder.withPayload(it).copyHeaders(message.getHeaders()).build())
        .collect(toList());
  }

  @Override
  protected String extractSubscriberEmail(Message<JsonNode> message) {
    JsonNode jsonNode = message.getPayload();

    String email = jsonNode.path("data").path("subscriber").path("email").asText();
    if (!StringUtils.hasText(email)) {
      throw new MessageTransformationException(message, "missing email");
    }

    return email;
  }

  @Override
  protected SubscriberStatus extractSubscriberStatus(Message<JsonNode> message) {
    JsonNode jsonNode = message.getPayload();

    String eventType = jsonNode.path("type").asText();
    if (!StringUtils.hasText(eventType)) {
      throw new MessageTransformationException(message, "missing event type");
    }

    SubscriberStatus status;
    switch (eventType) {
      case EVENT_TYPE_UNSUBSCRIBED:
        status = UNSUBSCRIBED;
        break;
      case EVENT_TYPE_SUBSCRIBED:
        status = SUBSCRIBED;
        break;
      default:
        throw new MessageTransformationException(message, "invalid event type: " + eventType);
    }

    return status;
  }

}
