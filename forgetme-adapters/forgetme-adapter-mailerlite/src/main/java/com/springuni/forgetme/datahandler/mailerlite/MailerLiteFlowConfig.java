package com.springuni.forgetme.datahandler.mailerlite;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.SingletonBeanRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.integration.support.management.MappingMessageRouterManagement;
import org.springframework.messaging.MessageChannel;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class MailerLiteFlowConfig implements InitializingBean {

  private final ApplicationContext applicationContext;
  private final MappingMessageRouterManagement webhookInboundRouter;

  @Override
  public void afterPropertiesSet() {
    ConfigurableApplicationContext adapterContext = new ClassPathXmlApplicationContext(
        new String[]{"/META-INF/spring/mailerlite-adapter-config.xml"},
        false, applicationContext);

    adapterContext.setId("mailerlite");
    adapterContext.refresh();

    MessageChannel webhookRouterOutboundChannel =
        adapterContext.getBean("webhookDataHandlerInboundChannel", MessageChannel.class);

    SingletonBeanRegistry applicationBeanRegistry =
        (SingletonBeanRegistry) applicationContext.getAutowireCapableBeanFactory();

    applicationBeanRegistry
        .registerSingleton("mailerlite.webhookDataHandlerInboundChannel", webhookRouterOutboundChannel);

    webhookInboundRouter
        .setChannelMapping("mailerlite", "mailerlite.webhookDataHandlerInboundChannel");

    log.info("Context {} created.", adapterContext.getId());
  }

}
