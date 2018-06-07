package com.springuni.forgetme.core.web;

import static java.util.Locale.ENGLISH;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

@Configuration
public class LocaleConfig {

  private static final String MESSAGES_BASENAME = "classpath:i18n/messages";
  private static final String MESSAGES_DEFAULT_ENCODING = "UTF-8";

  @Bean
  public LocaleResolver localeResolver() {
    AcceptHeaderLocaleResolver resolver = new AcceptHeaderLocaleResolver();
    resolver.setDefaultLocale(ENGLISH);
    return resolver;
  }

  @Bean
  public MessageSource messageSource() {
    ReloadableResourceBundleMessageSource messageSource =
        new ReloadableResourceBundleMessageSource();
    messageSource.setBasename(MESSAGES_BASENAME);
    messageSource.setDefaultEncoding(MESSAGES_DEFAULT_ENCODING);
    return messageSource;
  }

}
