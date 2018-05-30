package com.springuni.forgetme.core.integration;

import static java.time.ZoneOffset.UTC;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.core.convert.converter.Converter;

public class InstantToLocalDateTimeConverter implements Converter<Instant, LocalDateTime> {

  @Override
  public LocalDateTime convert(Instant source) {
    return Optional.ofNullable(source).map(it -> LocalDateTime.ofInstant(it, UTC)).orElse(null);
  }

}
