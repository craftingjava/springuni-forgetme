package com.springuni.forgetme.core.integration;

import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME;
import static org.junit.Assert.assertEquals;

import java.time.LocalDateTime;
import org.junit.Test;
import org.springframework.core.convert.converter.Converter;

public class StringToLocalDateTimeConverterTest {

  private static final String LOCAL_DATE_TIME_STR = "2018-05-29T05:45:34";
  private static final String LOCAL_DATE_TIME_STR_ZULU = LOCAL_DATE_TIME_STR + "Z";

  private static final LocalDateTime LOCAL_DATE_TIME =
      LocalDateTime.parse(LOCAL_DATE_TIME_STR, ISO_LOCAL_DATE_TIME);

  private final Converter<String, LocalDateTime> converter = new StringToLocalDateTimeConverter();

  @Test
  public void givenIsoTimestamp_whenConvert_thenConverted() {
    LocalDateTime converted = converter.convert(LOCAL_DATE_TIME_STR_ZULU);
    assertEquals(LOCAL_DATE_TIME, converted);
  }

}
