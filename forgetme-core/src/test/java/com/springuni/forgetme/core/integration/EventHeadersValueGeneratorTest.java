package com.springuni.forgetme.core.integration;

import static java.time.ZoneOffset.UTC;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.time.Clock;
import java.time.Instant;
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
    Instant eventTimestamp = Instant.ofEpochSecond(0);

    EventHeadersValueGenerator eventHeadersValueGenerator =
        new EventHeadersValueGenerator(() -> eventId, Clock.fixed(eventTimestamp, UTC));

    assertEquals(eventId, eventHeadersValueGenerator.createEventId(null));
    assertEquals(eventTimestamp, eventHeadersValueGenerator.createEventTimestamp(null));
  }

}
