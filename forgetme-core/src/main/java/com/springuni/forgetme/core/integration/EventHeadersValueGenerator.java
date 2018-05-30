package com.springuni.forgetme.core.integration;

import java.time.Clock;
import java.time.Instant;
import java.util.UUID;
import lombok.NonNull;
import org.springframework.messaging.Message;
import org.springframework.util.AlternativeJdkIdGenerator;
import org.springframework.util.IdGenerator;

public class EventHeadersValueGenerator {

  private static final IdGenerator DEFAULT_ID_GENERATOR = new AlternativeJdkIdGenerator();
  private static final Clock DEFAULT_CLOCK = Clock.systemUTC();

  private final IdGenerator idGenerator;
  private final Clock clock;

  public EventHeadersValueGenerator() {
    this(DEFAULT_ID_GENERATOR);
  }

  public EventHeadersValueGenerator(IdGenerator idGenerator) {
    this(idGenerator, DEFAULT_CLOCK);
  }

  EventHeadersValueGenerator(
      @NonNull IdGenerator idGenerator, @NonNull Clock clock) {

    this.idGenerator = idGenerator;
    this.clock = clock;
  }

  /**
   * @param message isn't used; it was added for making this method usable with the Java DSL.
   */
  public UUID createEventId(Message<?> message) {
    return idGenerator.generateId();
  }

  /**
   * @param message isn't used; it was added for making this method usable with the Java DSL.
   */
  public Instant createEventTimestamp(Message<?> message) {
    return Instant.now(clock);
  }

}
