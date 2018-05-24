package com.springuni.forgetme.datahandler.mailerlite;

import static java.util.Collections.singletonList;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;

//@Configuration
@ConfigurationProperties(prefix = "mailerlite")
public class MailerLiteClientConfig {

  private String apiKey;

  @Bean
  public ClientHttpRequestInterceptor mailerLiterRequestInterceptor() {
    return (request, body, execution) -> {
      request.getHeaders().add("X-MailerLite-ApiKey", apiKey);
      return execution.execute(request, body);
    };
  }

  @Bean
  public RestTemplate mailerLiterRestTemplate(
      ClientHttpRequestInterceptor mailerLiterRequestInterceptor) {

    RestTemplate restTemplate = new RestTemplate();
    restTemplate.setInterceptors(singletonList(mailerLiterRequestInterceptor));
    return restTemplate;
  }

}
