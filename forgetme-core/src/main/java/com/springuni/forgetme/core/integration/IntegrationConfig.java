package com.springuni.forgetme.core.integration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.config.EnableIntegration;

@Configuration
@EnableIntegration
public class IntegrationConfig {

  @Bean
  public EventHeadersValueGenerator eventHeadersValueGenerator() {
    return new EventHeadersValueGenerator();
  }

}
