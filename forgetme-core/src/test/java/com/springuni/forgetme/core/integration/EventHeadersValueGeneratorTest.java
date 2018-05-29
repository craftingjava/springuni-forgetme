package com.springuni.forgetme.core.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.Test;

public class EventHeadersValueGeneratorTest {

  @Test
  public void shouldHaveIdAndTimestampSetByDefault() {
    EventHeadersValueGenerator eventHeadersValueGenerator = new EventHeadersValueGenerator();

    assertNotNull(eventHeadersValueGenerator.createEventId(null));
    assertNotNull(eventHeadersValueGenerator.createEventTimestamp(null));
  }

  @Test
  public void shouldHaveGivenIdAndTimestampSet() {
    UUID eventId = new UUID(0, 0);
    LocalDateTime eventTimestamp = LocalDateTime.MIN;

    EventHeadersValueGenerator eventHeadersValueGenerator =
        new EventHeadersValueGenerator(() -> eventId, () -> eventTimestamp);

    assertEquals(eventId, eventHeadersValueGenerator.createEventId(null));
    assertEquals(eventTimestamp, eventHeadersValueGenerator.createEventTimestamp(null));
  }

}
