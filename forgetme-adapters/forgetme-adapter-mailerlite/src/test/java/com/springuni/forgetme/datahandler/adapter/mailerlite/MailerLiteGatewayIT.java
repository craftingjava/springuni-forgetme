package com.springuni.forgetme.datahandler.adapter.mailerlite;

import static java.util.Collections.singletonList;
import static org.junit.Assert.assertTrue;

import com.springuni.forgetme.core.integration.DataHandlerGateway;
import com.springuni.forgetme.core.model.ForgetRequest;
import com.springuni.forgetme.core.model.ForgetResponse;
import com.springuni.forgetme.datahandler.adapter.mailerlite.MailerLiteGatewayIT.TestConfig;
import java.util.UUID;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

@Ignore
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {TestConfig.class})
public class MailerLiteGatewayIT {

  @Autowired
  private DataHandlerGateway dataHandlerGateway;

  @Test
  public void shouldForgetSubscriber() {
    ForgetRequest forgetRequest = new ForgetRequest(UUID.randomUUID(), "test@springuni.com");
    ForgetResponse forgetResponse = dataHandlerGateway.handleForget(forgetRequest);
    assertTrue(forgetResponse.isAcknowledged());
  }

  @Configuration
  static class TestConfig {

    @Bean
    RestTemplate restTemplate(Environment environment) {
      RestTemplate restTemplate = new RestTemplate();
      ClientHttpRequestInterceptor requestInterceptor =
          new MailerLiteRequestInterceptor(environment.getProperty("MAILERLITE_API_KEY"));

      restTemplate.setInterceptors(singletonList(requestInterceptor));

      return restTemplate;
    }

    @Bean
    RetryTemplate retryTemplate() {
      return new RetryTemplate();
    }

    @Bean
    DataHandlerGateway dataHandlerGateway(RestTemplate restTemplate, RetryTemplate retryTemplate) {
      return new MailerLiteGateway(restTemplate, retryTemplate);
    }

  }

}
