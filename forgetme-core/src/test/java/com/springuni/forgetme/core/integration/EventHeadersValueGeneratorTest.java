package com.springuni.forgetme.core.integration;

import static java.time.Instant.EPOCH;
import static java.time.ZoneOffset.UTC;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.time.Clock;
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

    EventHeadersValueGenerator eventHeadersValueGenerator =
        new EventHeadersValueGenerator(() -> eventId, Clock.fixed(EPOCH, UTC));

    assertEquals(eventId, eventHeadersValueGenerator.createEventId(null));
    assertEquals(EPOCH, eventHeadersValueGenerator.createEventTimestamp(null));
  }

}
