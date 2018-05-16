package com.springuni.forgetme.core.orm;

import static java.time.ZoneOffset.UTC;

import java.time.LocalDateTime;
import java.time.temporal.TemporalAccessor;
import java.util.Optional;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.stereotype.Component;

@Component("utcLocalDateTimeProvider")
public class UtcLocalDateTimeProvider implements DateTimeProvider {

  @Override
  public Optional<TemporalAccessor> getNow() {
    return Optional.of(LocalDateTime.now(UTC));
  }

}
