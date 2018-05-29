package com.springuni.forgetme.core.integration;

import static java.time.ZoneOffset.UTC;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.function.Supplier;
import lombok.NonNull;
import org.springframework.messaging.Message;
import org.springframework.util.AlternativeJdkIdGenerator;
import org.springframework.util.IdGenerator;

public class EventHeadersValueGenerator {

  private static final IdGenerator DEFAULT_ID_GENERATOR = new AlternativeJdkIdGenerator();
  private static final Supplier<LocalDateTime> DEFAULT_TIMESTAMP_GENERATOR =
      () -> LocalDateTime.now(UTC);

  private final IdGenerator idGenerator;
  private final Supplier<LocalDateTime> timestampSupplier;

  public EventHeadersValueGenerator() {
    this(DEFAULT_ID_GENERATOR);
  }

  public EventHeadersValueGenerator(IdGenerator idGenerator) {
    this(idGenerator, DEFAULT_TIMESTAMP_GENERATOR);
  }

  EventHeadersValueGenerator(
      @NonNull IdGenerator idGenerator, @NonNull Supplier<LocalDateTime> timestampSupplier) {

    this.idGenerator = idGenerator;
    this.timestampSupplier = timestampSupplier;
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
  public LocalDateTime createEventTimestamp(Message<?> message) {
    return timestampSupplier.get();
  }

}
