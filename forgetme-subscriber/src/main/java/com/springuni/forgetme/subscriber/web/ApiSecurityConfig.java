package com.springuni.forgetme.subscriber.web;

import static org.springframework.boot.autoconfigure.security.SecurityProperties.BASIC_AUTH_ORDER;

import com.springuni.forgetme.core.security.authn.AbstractBasicAuthSecurityConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@Configuration
@EnableWebSecurity
@Order(1)
public class ApiSecurityConfig extends AbstractBasicAuthSecurityConfig {

  private static final String API_URL_PATTERN = "/api/**";
  private static final String API_BASIC_AUTH_KEYS_PREFIX = "forgetme.api.auth";

  @Override
  protected String getBasicAuthKeysPrefix() {
    return API_BASIC_AUTH_KEYS_PREFIX;
  }

  @Override
  protected String getUrlPattern() {
    return API_URL_PATTERN;
  }

}
