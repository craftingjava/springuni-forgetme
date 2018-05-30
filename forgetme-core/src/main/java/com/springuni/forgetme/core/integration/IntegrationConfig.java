package com.springuni.forgetme.core.integration;

import java.util.HashSet;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.support.ConversionServiceFactory;
import org.springframework.core.convert.support.GenericConversionService;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.support.utils.IntegrationUtils;
import org.springframework.util.Assert;

@Configuration
@EnableIntegration
@RequiredArgsConstructor
public class IntegrationConfig implements InitializingBean {

  private final BeanFactory beanFactory;

  @Bean
  public EventHeadersValueGenerator eventHeadersValueGenerator() {
    return new EventHeadersValueGenerator();
  }

  @Override
  public void afterPropertiesSet() {
    Assert.notNull(this.beanFactory, "BeanFactory is required");

    ConversionService conversionService = IntegrationUtils.getConversionService(this.beanFactory);
    if (conversionService instanceof GenericConversionService) {
      ConversionServiceFactory.registerConverters(
          createConverters(),
          (GenericConversionService) conversionService
      );
    } else {
      Assert.notNull(conversionService,
          "Failed to locate '" + IntegrationUtils.INTEGRATION_CONVERSION_SERVICE_BEAN_NAME + "'");
    }
  }

  private Set<Converter<?, ?>> createConverters() {
    Set<Converter<?, ?>> converters = new HashSet<>();
    converters.add(new StringToLocalDateTimeConverter());
    converters.add(new InstantToLocalDateTimeConverter());
    return converters;
  }

}
