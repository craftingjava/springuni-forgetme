package com.springuni.forgetme.datahandler;

import static com.springuni.forgetme.core.amqp.QueueConfig.FORGETME_WEBHOOK_EXCHANGE_NAME;

import com.springuni.forgetme.core.amqp.QueueConfig;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.amqp.dsl.Amqp;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.Transformers;
import org.springframework.messaging.MessageChannel;

@Configuration
public class WebhookOutboundChannelConfig {

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

}
