package com.springuni.forgetme.datahandler.mailerlite;

import static com.springuni.forgetme.core.amqp.QueueConfig.FORGETME_DATAHANDLER_EXCHANGE_NAME;
import static com.springuni.forgetme.core.amqp.QueueConfig.FORGETME_DATAHANDLER_REQUEST_QUEUE_NAME;
import static com.springuni.forgetme.core.amqp.QueueConfig.FORGETME_DATAHANDLER_REQUEST_ROUTING_KEY_NAME;
import static com.springuni.forgetme.core.amqp.QueueConfig.FORGETME_DATAHANDLER_RESPONSE_ROUTING_KEY_NAME;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.amqp.dsl.Amqp;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.http.dsl.Http;
import org.springframework.messaging.MessageChannel;
import org.springframework.web.client.RestTemplate;

//@Configuration
public class MailerLiteChannelConfig {

  @Bean
  public MessageChannel mailerLiteRequestChannel() {
    return new DirectChannel();
  }

  @Bean
  public MessageChannel mailerLiteResponseChannel() {
    return new DirectChannel();
  }

  @Bean
  public IntegrationFlow mailerLiteOutboundFlow(
      MessageChannel mailerLiteRequestChannel, MessageChannel mailerLiteResponseChannel,
      AmqpTemplate amqpTemplate) {

    return IntegrationFlows.from(mailerLiteRequestChannel)
        .handle(
            Amqp.outboundGateway(amqpTemplate)
                .exchangeName(FORGETME_DATAHANDLER_EXCHANGE_NAME)
                .routingKey(FORGETME_DATAHANDLER_REQUEST_ROUTING_KEY_NAME)
        )
        .channel(mailerLiteResponseChannel)
        .get();
  }

  @Bean
  public IntegrationFlow mailerLiteInboundFlow(
      ConnectionFactory connectionFactory, AmqpTemplate amqpTemplate,
      RestTemplate mailerLiterRestTemplate) {

    return IntegrationFlows
        .from(Amqp.inboundAdapter(connectionFactory, FORGETME_DATAHANDLER_REQUEST_QUEUE_NAME))
        .handle(Http.outboundGateway(
            "https://api.mailerlite.com/api/v2/subscribers/email/forget",
            mailerLiterRestTemplate
        ).uriVariable("email", message -> message.toString()))
        .handle(Amqp.outboundAdapter(amqpTemplate).exchangeName(FORGETME_DATAHANDLER_EXCHANGE_NAME)
            .routingKey(FORGETME_DATAHANDLER_RESPONSE_ROUTING_KEY_NAME))
        .get();
  }

}
