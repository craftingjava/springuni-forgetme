package com.springuni.forgetme.subscriber;

import static com.springuni.forgetme.Mocks.EMAIL;
import static com.springuni.forgetme.Mocks.EMAIL_HASH;
import static com.springuni.forgetme.subscriber.SubscriberStatus.SUBSCRIBED;
import static com.springuni.forgetme.subscriber.SubscriberStatus.UNSUBSCRIBED;
import static com.springuni.forgetme.subscriber.SubscriberTransformer.EVENT_TYPE_SUBSCRIBED;
import static com.springuni.forgetme.subscriber.SubscriberTransformer.EVENT_TYPE_UNSUBSCRIBED;
import static org.junit.Assert.assertEquals;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.springframework.integration.transformer.GenericTransformer;

@Slf4j
public class SubscriberTransformerTest {

  private final GenericTransformer<JsonNode, List<Subscriber>> transformer =
      new SubscriberTransformer();

  private ObjectNode jsonNode;

  @Before
  public void setUp() throws Exception {
    jsonNode = JsonNodeFactory.instance.objectNode();
  }

  @Test
  public void givenUnsubscribedEvent_whenTransform_thenTransformedToSubscriber() {
    testTransform(EMAIL_HASH, UNSUBSCRIBED, EMAIL, EVENT_TYPE_UNSUBSCRIBED);
  }

  @Test
  public void givenSubscribedEvent_whenTransform_thenTransformedToSubscriber() {
    testTransform(EMAIL_HASH, SUBSCRIBED, EMAIL, EVENT_TYPE_SUBSCRIBED);
  }

  @Test(expected = IllegalArgumentException.class)
  public void givenUnknownEvent_whenTransform_thenTransformedToSubscriber() {
    testTransform(EMAIL_HASH, SUBSCRIBED, EMAIL, "unknown.event");
  }

  private void populateJsonNode(String eventType, String email) {
    ObjectNode eventsObject = jsonNode.putArray("events").addObject();

    eventsObject.put("type", eventType);
    eventsObject.putObject("data").putObject("subscriber").put("email", email);
  }

  private void testTransform(
      String expectEmailHash, SubscriberStatus expectedStatus, String email, String eventType) {

    populateJsonNode(eventType, email);

    List<Subscriber> subscribers = transformer.transform(jsonNode);

    assertEquals(1, subscribers.size());

    Subscriber subscriber = subscribers.get(0);
    assertEquals(expectEmailHash, subscriber.getEmailHash());
    assertEquals(expectedStatus, subscriber.getStatus());
  }

}
