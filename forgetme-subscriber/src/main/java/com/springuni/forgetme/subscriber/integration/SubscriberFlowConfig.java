package com.springuni.forgetme.subscriber.integration;

import static com.springuni.forgetme.core.amqp.QueueConfig.FORGETME_DATAHANDLER_EXCHANGE_NAME;
import static com.springuni.forgetme.core.amqp.QueueConfig.FORGETME_DATAHANDLER_REQUEST_QUEUE_NAME;
import static com.springuni.forgetme.core.amqp.QueueConfig.FORGETME_DATAHANDLER_REQUEST_ROUTING_KEY_NAME;
import static com.springuni.forgetme.core.amqp.QueueConfig.FORGETME_DATAHANDLER_RESPONSE_QUEUE_NAME;
import static com.springuni.forgetme.core.amqp.QueueConfig.FORGETME_DATAHANDLER_RESPONSE_ROUTING_KEY_NAME;
import static com.springuni.forgetme.core.model.MessageHeaderNames.DATA_HANDLER_NAME;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.amqp.dsl.Amqp;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.router.HeaderValueRouter;
import org.springframework.messaging.MessageChannel;

@Configuration
@EnableIntegration
public class SubscriberFlowConfig {

  @Bean
  public MessageChannel webhookDataHandlerOutboundChannel() {
    return new DirectChannel();
  }

  @Bean
  public MessageChannel subscriberDataHandlerOutboundChannel() {
    return new DirectChannel();
  }


  @Bean
  public MessageChannel subscriberForgetRequestOutboundChannel() {
    return new DirectChannel();
  }

  @Bean
  public MessageChannel subscriberForgetResponseInboundChannel() {
    return new DirectChannel();
  }


  @Bean
  public MessageChannel subscriberForgetResponseChannel() {
    return new DirectChannel();
  }

  @Bean
  public IntegrationFlow subscriberForgetRequestOutboundFlow(
      MessageChannel subscriberForgetRequestOutboundChannel,
      AmqpTemplate amqpTemplate) {

    return IntegrationFlows.from(subscriberForgetRequestOutboundChannel)
        .handle(
            Amqp.outboundAdapter(amqpTemplate)
                .exchangeName(FORGETME_DATAHANDLER_EXCHANGE_NAME)
                .routingKey(FORGETME_DATAHANDLER_REQUEST_ROUTING_KEY_NAME)
        )
        .get();
  }

  @Bean
  public HeaderValueRouter subscriberForgetRequestRouter() {
    return new HeaderValueRouter(DATA_HANDLER_NAME);
  }

  @Bean
  public IntegrationFlow subscriberForgetRequestInboundFlow(
      ConnectionFactory connectionFactory, HeaderValueRouter subscriberForgetRequestRouter) {

    return IntegrationFlows
        .from(Amqp.inboundAdapter(connectionFactory, FORGETME_DATAHANDLER_REQUEST_QUEUE_NAME))
        .route(subscriberForgetRequestRouter)
        .get();
  }

  @Bean
  public IntegrationFlow subscriberForgetResponseOutboundFlow(
      MessageChannel subscriberDataHandlerOutboundChannel, AmqpTemplate amqpTemplate) {

    return IntegrationFlows
        .from(subscriberDataHandlerOutboundChannel)
        .handle(Amqp.outboundAdapter(amqpTemplate).exchangeName(FORGETME_DATAHANDLER_EXCHANGE_NAME)
            .routingKey(FORGETME_DATAHANDLER_RESPONSE_ROUTING_KEY_NAME))
        .get();
  }

  @Bean
  public IntegrationFlow subscriberForgetResponseInboundFlow(
      ConnectionFactory connectionFactory, MessageChannel subscriberForgetResponseInboundChannel) {

    return IntegrationFlows
        .from(Amqp.inboundAdapter(connectionFactory, FORGETME_DATAHANDLER_RESPONSE_QUEUE_NAME))
        .channel(subscriberForgetResponseInboundChannel)
        .get();
  }

}
