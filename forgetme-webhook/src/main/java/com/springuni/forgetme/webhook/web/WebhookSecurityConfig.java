package com.springuni.forgetme.webhook.web;

import com.springuni.forgetme.core.security.authn.AbstractBasicAuthSecurityConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@Configuration
@EnableWebSecurity
@Order(0)
public class WebhookSecurityConfig extends AbstractBasicAuthSecurityConfig {

  private static final String WEBHOOK_URL_PATTERN = "/webhook/**";
  private static final String WEBHOOK_BASIC_AUTH_KEYS_PREFIX = "forgetme.webhook.auth";

  @Override
  protected String getBasicAuthKeysPrefix() {
    return WEBHOOK_BASIC_AUTH_KEYS_PREFIX;
  }

  @Override
  protected String getUrlPattern() {
    return WEBHOOK_URL_PATTERN;
  }

}
