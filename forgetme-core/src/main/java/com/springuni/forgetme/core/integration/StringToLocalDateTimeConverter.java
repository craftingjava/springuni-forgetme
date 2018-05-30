package com.springuni.forgetme.core.integration;

import static java.time.ZoneOffset.UTC;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.amqp.rabbit.support.DefaultMessagePropertiesConverter;
import org.springframework.core.convert.converter.Converter;
import org.springframework.messaging.Message;

/**
 * {@link EventHeadersValueGenerator#createEventTimestamp(Message)} generates an {@code Instant} and
 * {@link DefaultMessagePropertiesConverter#convertHeaderValueIfNecessary(Object)} simply uses
 * {@code toString()} to convert it.
 */
public class StringToLocalDateTimeConverter implements Converter<String, LocalDateTime> {

  @Override
  public LocalDateTime convert(String source) {
    return Optional.ofNullable(source)
        .map(Instant::parse)
        .map(it -> LocalDateTime.ofInstant(it, UTC))
        .orElse(null);
  }

}
