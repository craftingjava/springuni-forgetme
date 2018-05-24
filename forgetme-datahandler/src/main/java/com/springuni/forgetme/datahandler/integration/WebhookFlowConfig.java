package com.springuni.forgetme.datahandler.integration;

import static com.springuni.forgetme.core.amqp.QueueConfig.FORGETME_WEBHOOK_EXCHANGE_NAME;
import static com.springuni.forgetme.core.amqp.QueueConfig.FORGETME_WEBHOOK_QUEUE_NAME;
import static com.springuni.forgetme.core.model.MessageHeaderNames.DATA_HANDLER_NAME;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springuni.forgetme.core.integration.ObjectToJsonNodeTransformer;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.amqp.dsl.Amqp;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.Transformers;
import org.springframework.messaging.MessageChannel;

@Configuration
public class WebhookFlowConfig {

  @Bean
  public MessageChannel webhookOutboundChannel() {
    return new DirectChannel();
  }

  @Bean
  public IntegrationFlow webhookOutboundFlow(
      MessageChannel webhookOutboundChannel, AmqpTemplate amqpTemplate) {

    return IntegrationFlows.from(webhookOutboundChannel)
        .transform(Transformers.toJson())
        .handle(Amqp.outboundAdapter(amqpTemplate).exchangeName(FORGETME_WEBHOOK_EXCHANGE_NAME))
        .get();
  }

  @Bean
  public IntegrationFlow webhookInboundFlow(
      ConnectionFactory connectionFactory, ObjectMapper objectMapper) {

    return IntegrationFlows
        .from(Amqp.inboundAdapter(connectionFactory, FORGETME_WEBHOOK_QUEUE_NAME))
        .transform(new ObjectToJsonNodeTransformer(objectMapper))
        .route("headers['" + DATA_HANDLER_NAME + "InboundChannel']")
        .get();
  }

}
