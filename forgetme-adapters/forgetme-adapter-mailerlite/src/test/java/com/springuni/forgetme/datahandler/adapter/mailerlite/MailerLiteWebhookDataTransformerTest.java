package com.springuni.forgetme.datahandler.adapter.mailerlite;

import static com.springuni.forgetme.core.model.MessageHeaderNames.DATA_HANDLER_ID;
import static com.springuni.forgetme.core.model.SubscriberStatus.SUBSCRIBED;
import static com.springuni.forgetme.core.model.SubscriberStatus.UNSUBSCRIBED;
import static com.springuni.forgetme.datahandler.adapter.mailerlite.MailerLiteWebhookDataTransformer.EVENT_TYPE_SUBSCRIBED;
import static com.springuni.forgetme.datahandler.adapter.mailerlite.MailerLiteWebhookDataTransformer.EVENT_TYPE_UNSUBSCRIBED;
import static org.junit.Assert.assertEquals;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.springuni.forgetme.core.model.SubscriberStatus;
import com.springuni.forgetme.core.model.WebhookData;
import com.springuni.forgetme.datahandler.adapter.mailerlite.MailerLiteWebhookDataTransformer;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.springframework.integration.transformer.MessageTransformationException;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.security.core.token.Sha512DigestUtils;

@Slf4j
public class MailerLiteWebhookDataTransformerTest {

  public static final String EMAIL = "test@springuni.com";
  public static final String EMAIL_HASH = Sha512DigestUtils.shaHex(EMAIL);

  public static final UUID DATA_HANDLER_ID_VALUE =
      UUID.fromString("e408b7d4-49dc-427e-ad60-e5d8a0dc5925");

  private final MailerLiteWebhookDataTransformer transformer = new MailerLiteWebhookDataTransformer();

  private JsonNode jsonNode;

  @Before
  public void setUp() throws Exception {
    jsonNode = JsonNodeFactory.instance.objectNode();
  }

  @Test
  public void givenUnsubscribedEvent_whenTransform_thenTransformedToSubscriber() {
    testTransform(EMAIL, UNSUBSCRIBED, EMAIL, EVENT_TYPE_UNSUBSCRIBED);
  }

  @Test
  public void givenSubscribedEvent_whenTransform_thenTransformedToSubscriber() {
    testTransform(EMAIL, SUBSCRIBED, EMAIL, EVENT_TYPE_SUBSCRIBED);
  }

  @Test(expected = MessageTransformationException.class)
  public void givenUnknownEvent_whenTransform_thenTransformedToSubscriber() {
    testTransform(EMAIL, SUBSCRIBED, EMAIL, "unknown.event");
  }

  private void populateJsonNode(String eventType, String email) {
    ObjectNode eventsObject = ((ObjectNode) jsonNode).putArray("events").addObject();

    eventsObject.put("type", eventType);
    eventsObject.putObject("data").putObject("subscriber").put("email", email);
  }

  private void testTransform(
      String expectEmail, SubscriberStatus expectedStatus, String email, String eventType) {

    populateJsonNode(eventType, email);

    Message<JsonNode> message = MessageBuilder.withPayload(jsonNode)
        .setHeader(DATA_HANDLER_ID, DATA_HANDLER_ID_VALUE)
        .build();

    List<WebhookData> webhookDataList = new ArrayList<>(
        transformer.transform(message).getPayload());

    assertEquals(1, webhookDataList.size());

    WebhookData webhookData = webhookDataList.get(0);
    assertEquals(expectEmail, webhookData.getSubscriberEmail());

    assertEquals(expectedStatus, webhookData.getSubscriberStatus());
  }

}
