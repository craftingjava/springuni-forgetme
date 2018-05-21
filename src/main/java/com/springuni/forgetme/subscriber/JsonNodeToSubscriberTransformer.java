package com.springuni.forgetme.subscriber;

import static com.springuni.forgetme.subscriber.SubscriberStatus.SUBSCRIBED;
import static com.springuni.forgetme.subscriber.SubscriberStatus.UNSUBSCRIBED;
import static java.util.stream.Collectors.toList;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.List;
import java.util.stream.StreamSupport;
import org.springframework.integration.transformer.GenericTransformer;
import org.springframework.util.Assert;

public class JsonNodeToSubscriberTransformer implements GenericTransformer<JsonNode, List<Subscriber>> {

  // https://developers.mailerlite.com/docs/webhooks#section-available-events
  static final String EVENT_TYPE_UNSUBSCRIBED = "subscriber.unsubscribe";
  static final String EVENT_TYPE_SUBSCRIBED = "subscriber.create";

  @Override
  public List<Subscriber> transform(JsonNode source) {
    return StreamSupport.stream(source.path("events").spliterator(), false)
        .map(this::transformEvent)
        .collect(toList());
  }

  private Subscriber transformEvent(JsonNode event) {
    String eventType = event.path("type").asText();
    Assert.hasText(eventType, "eventType cannot be null or empty");

    SubscriberStatus status;
    switch (eventType) {
      case EVENT_TYPE_UNSUBSCRIBED:
        status = UNSUBSCRIBED;
        break;
      case EVENT_TYPE_SUBSCRIBED:
        status = SUBSCRIBED;
        break;
      default:
        throw new IllegalArgumentException("Unknown event type: " + eventType);
    }

    String email = event.path("data").path("subscriber").path("email").asText();
    Assert.hasText(email, "email address cannot be null or empty");

    // TODO: return a message object here
    return null;
  }

}
