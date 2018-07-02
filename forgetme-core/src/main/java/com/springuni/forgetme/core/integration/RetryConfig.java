package com.springuni.forgetme.core.integration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.RetryOperations;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

@EnableRetry
@Configuration
public class RetryConfig {

  public static final int MAX_ATTEMPTS = 3;

  @Bean
  public RetryOperations retryTemplate() {
    RetryTemplate retryTemplate = new RetryTemplate();
    retryTemplate.setRetryPolicy(new SimpleRetryPolicy(MAX_ATTEMPTS));
    return retryTemplate;
  }

}
