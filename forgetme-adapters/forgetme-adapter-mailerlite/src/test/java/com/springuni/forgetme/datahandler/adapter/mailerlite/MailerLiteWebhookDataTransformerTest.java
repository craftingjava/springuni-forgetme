package com.springuni.forgetme.datahandler.adapter.mailerlite;

import static com.springuni.forgetme.core.model.MessageHeaderNames.DATA_HANDLER_ID;
import static com.springuni.forgetme.core.model.MessageHeaderNames.EVENT_TIMESTAMP;
import static com.springuni.forgetme.core.model.SubscriptionStatus.SUBSCRIPTION_CREATED;
import static com.springuni.forgetme.core.model.SubscriptionStatus.UNSUBSCRIBED;
import static com.springuni.forgetme.datahandler.adapter.mailerlite.MailerLiteWebhookDataTransformer.EVENT_TYPE_SUBSCRIBER_CREATE;
import static com.springuni.forgetme.datahandler.adapter.mailerlite.MailerLiteWebhookDataTransformer.EVENT_TYPE_SUBSCRIBER_UNSUBSCRIBE;
import static org.junit.Assert.assertEquals;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.springuni.forgetme.core.model.SubscriptionStatus;
import com.springuni.forgetme.core.model.WebhookData;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.springframework.integration.transformer.MessageTransformationException;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

@Slf4j
public class MailerLiteWebhookDataTransformerTest {

  private static final String EMAIL = "test@springuni.com";

  private static final Instant EVENT_TIMESTAMP_VALUE = Instant.ofEpochMilli(1495551003);

  private static final UUID DATA_HANDLER_ID_VALUE =
      UUID.fromString("e408b7d4-49dc-427e-ad60-e5d8a0dc5925");

  private final MailerLiteWebhookDataTransformer transformer = new MailerLiteWebhookDataTransformer();

  private JsonNode jsonNode;

  @Before
  public void setUp() throws Exception {
    jsonNode = JsonNodeFactory.instance.objectNode();
  }

  @Test
  public void givenUnsubscribedEvent_whenTransform_thenTransformedToSubscriber() {
    testTransform(EMAIL, UNSUBSCRIBED, EMAIL, EVENT_TYPE_SUBSCRIBER_UNSUBSCRIBE);
  }

  @Test
  public void givenSubscribedEvent_whenTransform_thenTransformedToSubscriber() {
    testTransform(EMAIL, SUBSCRIPTION_CREATED, EMAIL, EVENT_TYPE_SUBSCRIBER_CREATE);
  }

  @Test(expected = MessageTransformationException.class)
  public void givenUnknownEvent_whenTransform_thenTransformedToSubscriber() {
    testTransform(EMAIL, SUBSCRIPTION_CREATED, EMAIL, "unknown.event");
  }

  private void populateJsonNode(String eventType, String email) {
    ObjectNode eventsObject = ((ObjectNode) jsonNode).putArray("events").addObject();

    eventsObject.put("type", eventType);
    eventsObject.put("timestamp", EVENT_TIMESTAMP_VALUE.toEpochMilli());
    eventsObject.putObject("data").putObject("subscriber").put("email", email);
  }

  private void testTransform(
      String expectEmail, SubscriptionStatus expectedStatus, String email, String eventType) {

    populateJsonNode(eventType, email);

    Message<JsonNode> message = MessageBuilder.withPayload(jsonNode)
        .setHeader(DATA_HANDLER_ID, DATA_HANDLER_ID_VALUE)
        .build();

    List<Message<WebhookData>> webhookDataMessageList = transformer.transform(message);
    assertEquals(1, webhookDataMessageList.size());

    Message<WebhookData> webhookDataMessage = webhookDataMessageList.get(0);
    assertEquals(EVENT_TIMESTAMP_VALUE, webhookDataMessage.getHeaders().get(EVENT_TIMESTAMP));

    WebhookData webhookData = webhookDataMessage.getPayload();
    assertEquals(expectEmail, webhookData.getSubscriberEmail());
    assertEquals(expectedStatus, webhookData.getSubscriptionStatus());
  }

}
