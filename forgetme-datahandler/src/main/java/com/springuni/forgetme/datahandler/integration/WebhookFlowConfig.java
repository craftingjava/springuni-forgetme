package com.springuni.forgetme.datahandler.integration;

import static com.springuni.forgetme.core.amqp.QueueConfig.FORGETME_WEBHOOK_EXCHANGE_NAME;
import static com.springuni.forgetme.core.amqp.QueueConfig.FORGETME_WEBHOOK_QUEUE_NAME;
import static com.springuni.forgetme.core.amqp.QueueConfig.FORGETME_WEBHOOK_ROUTING_KEY_NAME;
import static com.springuni.forgetme.core.model.MessageHeaderNames.DATA_HANDLER_NAME;
import static org.springframework.integration.handler.LoggingHandler.Level.ERROR;
import static org.springframework.integration.handler.LoggingHandler.Level.INFO;

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
import org.springframework.integration.router.HeaderValueRouter;
import org.springframework.integration.router.MessageRouter;
import org.springframework.messaging.MessageChannel;

@Configuration
public class WebhookFlowConfig {

  @Bean
  public MessageChannel webhookOutboundChannel() {
    return new DirectChannel();
  }

  @Bean
  public MessageChannel webhookDeadLetterChannel() {
    return new DirectChannel();
  }

  @Bean
  public IntegrationFlow webhookOutboundFlow(
      MessageChannel webhookOutboundChannel, AmqpTemplate amqpTemplate) {

    return IntegrationFlows.from(webhookOutboundChannel)
        .transform(Transformers.toJson())
        .handle(Amqp.outboundAdapter(amqpTemplate).exchangeName(FORGETME_WEBHOOK_EXCHANGE_NAME).routingKey(FORGETME_WEBHOOK_ROUTING_KEY_NAME))
        .get();
  }

  @Bean
  public HeaderValueRouter webhookInboundRouter(MessageChannel webhookDeadLetterChannel) {
    HeaderValueRouter router = new HeaderValueRouter(DATA_HANDLER_NAME);
    router.setSuffix(".webhookRouterOutboundChannel");
    router.setDefaultOutputChannel(webhookDeadLetterChannel);
    return router;
  }

  @Bean
  public IntegrationFlow webhookInboundFlow(
      ConnectionFactory connectionFactory, ObjectMapper objectMapper,
      HeaderValueRouter webhookInboundRouter) {

    return IntegrationFlows
        .from(Amqp.inboundAdapter(connectionFactory, FORGETME_WEBHOOK_QUEUE_NAME))
        .log(INFO)
        .transform(new ObjectToJsonNodeTransformer(objectMapper))
        .route(webhookInboundRouter)
        // .route("headers['" + DATA_HANDLER_NAME + "'].concat('InboundChannel')")
        .get();
  }

  @Bean
  public IntegrationFlow webhookDeadLetterFlow(MessageChannel webhookDeadLetterChannel) {
    return IntegrationFlows.from(webhookDeadLetterChannel).log(ERROR).get();
  }

}
