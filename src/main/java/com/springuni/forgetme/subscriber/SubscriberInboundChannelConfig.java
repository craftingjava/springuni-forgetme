package com.springuni.forgetme.subscriber;

import static com.springuni.forgetme.core.amqp.QueueConfig.FORGETME_WEBHOOK_QUEUE_NAME;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springuni.forgetme.integration.ObjectToJsonNodeTransformer;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.amqp.dsl.Amqp;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.messaging.MessageChannel;

@Configuration
@EnableIntegration
public class SubscriberInboundChannelConfig {

  @Bean
  public MessageChannel subscriberInboundChannel() {
    return new DirectChannel();
  }

  @Bean
  public IntegrationFlow subscriberInboundFlow(
      ConnectionFactory connectionFactory, MessageChannel subscriberInboundChannel,
      ObjectMapper objectMapper) {

    return IntegrationFlows
        .from(Amqp.inboundAdapter(connectionFactory, FORGETME_WEBHOOK_QUEUE_NAME))
        .transform(new ObjectToJsonNodeTransformer(objectMapper))
        .transform(new JsonNodeToWebhookDataList())
        .split()
        .channel(subscriberInboundChannel)
        .get();
  }

}
