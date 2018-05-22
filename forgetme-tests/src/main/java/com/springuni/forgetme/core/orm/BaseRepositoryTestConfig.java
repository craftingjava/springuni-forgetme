package com.springuni.forgetme.core.orm;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.auditing.DateTimeProvider;

@Configuration
@Import(JpaConfig.class)
public class BaseRepositoryTestConfig {

  @Bean
  DateTimeProvider utcLocalDateTimeProvider() {
    return new UtcLocalDateTimeProvider();
  }

}
