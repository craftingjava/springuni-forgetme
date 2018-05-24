package com.springuni.forgetme.subscriber.integration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.messaging.MessageChannel;

@Configuration
@EnableIntegration
public class SubscriberFlowConfig {

  @Bean
  public MessageChannel subscriberInboundChannel() {
    return new DirectChannel();
  }

}
