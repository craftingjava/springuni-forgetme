package com.springuni.forgetme.subscriber;

import static com.springuni.forgetme.Mocks.DATA_HANDLER_ID_VALUE;
import static com.springuni.forgetme.Mocks.EMAIL;
import static com.springuni.forgetme.core.model.MessageHeaderNames.DATA_HANDLER_ID;
import static com.springuni.forgetme.subscriber.JsonNodeToWebhookDataList.EVENT_TYPE_SUBSCRIBED;
import static com.springuni.forgetme.subscriber.JsonNodeToWebhookDataList.EVENT_TYPE_UNSUBSCRIBED;
import static com.springuni.forgetme.subscriber.SubscriberStatus.SUBSCRIBED;
import static com.springuni.forgetme.subscriber.SubscriberStatus.UNSUBSCRIBED;
import static org.junit.Assert.assertEquals;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.springuni.forgetme.core.model.MessageHeaderNames;
import com.springuni.forgetme.core.model.WebhookData;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.springframework.integration.transformer.MessageTransformationException;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

@Slf4j
public class JsonNodeToWebhookDataListTest {

  private final JsonNodeToWebhookDataList transformer = new JsonNodeToWebhookDataList();

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
