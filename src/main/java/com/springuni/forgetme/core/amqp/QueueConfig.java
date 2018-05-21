package com.springuni.forgetme.core.amqp;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QueueConfig {

  /// WEBHOOK ///

  public static final String FORGETME_WEBHOOK_EXCHANGE_NAME = "forgetme-webhook.exchange";
  public static final String FORGETME_WEBHOOK_DEAD_LETTER_EXCHANGE_NAME =
      "forgetme-webhook.dead-letter.exchange";

  public static final String FORGETME_WEBHOOK_QUEUE_NAME = "forgetme-webhook.queue";
  public static final String FORGETME_WEBHOOK_DEAD_LETTER_QUEUE_NAME =
      "forgetme-webhook.dead-letter.queue";

  public static final String FORGETME_WEBHOOK_ROUTING_KEY_NAME = "forgetme-webhook";

  /// Exchanges ///
  public static final String FORGETME_DATAHANDLER_EXCHANGE_NAME = "forgetme-datahandler.exchange";
  public static final String FORGETME_DATAHANDLER_REQUEST_QUEUE_NAME = "forgetme-datahandler-request.queue";

  /// Queues ///
  public static final String FORGETME_DATAHANDLER_RESPONSE_QUEUE_NAME = "forgetme-datahandler-response.queue";
  public static final String FORGETME_DATAHANDLER_REQUEST_ROUTING_KEY_NAME = "forgetme-datahandler-request";

  /// Bindings ///
  public static final String FORGETME_DATAHANDLER_RESPONSE_ROUTING_KEY_NAME = "forgetme-datahandler-response";

  @Bean
  public DirectExchange forgetmeWebhookExchange() {
    return new DirectExchange(FORGETME_WEBHOOK_EXCHANGE_NAME);
  }

  /// DATA HANDLER ///

  @Bean
  public DirectExchange forgetmeWebhookDeadLetterExchange() {
    return new DirectExchange(FORGETME_WEBHOOK_DEAD_LETTER_EXCHANGE_NAME);
  }

  @Bean
  public Queue forgetmeWebhookQueue() {
    return QueueBuilder
        .durable(FORGETME_WEBHOOK_QUEUE_NAME)
        .withArgument("x-dead-letter-exchange", FORGETME_WEBHOOK_DEAD_LETTER_EXCHANGE_NAME)
        .build();
  }

  @Bean
  public Queue forgetmeWebhookDeadLetterQueue() {
    return QueueBuilder.durable(FORGETME_WEBHOOK_DEAD_LETTER_QUEUE_NAME).build();
  }

  @Bean
  public Binding orderBinding(DirectExchange forgetmeWebhookExchange, Queue forgetmeWebhookQueue) {
    return BindingBuilder
        .bind(forgetmeWebhookQueue)
        .to(forgetmeWebhookExchange)
        .with(FORGETME_WEBHOOK_ROUTING_KEY_NAME);
  }

  @Bean
  public Binding forgetmeWebhookDeadLetterBinding(
      DirectExchange forgetmeWebhookDeadLetterExchange, Queue forgetmeWebhookDeadLetterQueue) {

    return BindingBuilder
        .bind(forgetmeWebhookDeadLetterQueue)
        .to(forgetmeWebhookDeadLetterExchange)
        .with(FORGETME_WEBHOOK_ROUTING_KEY_NAME);
  }

  /// Exchanges ///

  @Bean
  public DirectExchange forgetmeDatahandlerExchange() {
    return new DirectExchange(FORGETME_DATAHANDLER_EXCHANGE_NAME);
  }

  /// Queues ///

  @Bean
  public Queue forgetmeDatahandlerRequestQueue() {
    return QueueBuilder
        .durable(FORGETME_DATAHANDLER_REQUEST_QUEUE_NAME)
        .build();
  }

  @Bean
  public Queue forgetmeDatahandlerResponseQueue() {
    return QueueBuilder
        .durable(FORGETME_DATAHANDLER_RESPONSE_QUEUE_NAME)
        .build();
  }

  /// Bindings ///

  @Bean
  public Binding forgetmeDatahandlerRequestBinding(
      DirectExchange forgetmeDatahandlerExchange, Queue forgetmeDatahandlerRequestQueue) {

    return BindingBuilder
        .bind(forgetmeDatahandlerRequestQueue)
        .to(forgetmeDatahandlerExchange)
        .with(FORGETME_DATAHANDLER_REQUEST_ROUTING_KEY_NAME);
  }

  @Bean
  public Binding forgetmeDatahandlerResponseBinding(
      DirectExchange forgetmeDatahandlerExchange, Queue forgetmeDatahandlerResponseQueue) {

    return BindingBuilder
        .bind(forgetmeDatahandlerResponseQueue)
        .to(forgetmeDatahandlerExchange)
        .with(FORGETME_DATAHANDLER_RESPONSE_ROUTING_KEY_NAME);
  }

}
