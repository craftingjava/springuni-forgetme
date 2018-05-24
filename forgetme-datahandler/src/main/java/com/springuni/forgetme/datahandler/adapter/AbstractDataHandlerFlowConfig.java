package com.springuni.forgetme.datahandler.adapter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.SingletonBeanRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.integration.support.management.MappingMessageRouterManagement;
import org.springframework.messaging.MessageChannel;

@Slf4j
@Configuration
public abstract class AbstractDataHandlerFlowConfig implements InitializingBean {

  private static final String WEBHOOK_DATA_HANDLER_INBOUND_CHANNEL_NAME =
      "webhookDataHandlerInboundChannel";

  @Autowired
  private ApplicationContext applicationContext;

  @Autowired
  private MappingMessageRouterManagement webhookInboundRouter;

  @Override
  public void afterPropertiesSet() {
    String dataHandlerName = getDataHandlerName();

    ConfigurableApplicationContext dataHandlerContext = new ClassPathXmlApplicationContext(
        new String[]{"/META-INF/spring/" + dataHandlerName + "-adapter-config.xml"},
        false,
        applicationContext);

    dataHandlerContext.setId(dataHandlerName);
    dataHandlerContext.refresh();

    MessageChannel webhookDataHandlerInboundChannel =
        dataHandlerContext.getBean(WEBHOOK_DATA_HANDLER_INBOUND_CHANNEL_NAME, MessageChannel.class);

    SingletonBeanRegistry applicationBeanRegistry =
        (SingletonBeanRegistry) applicationContext.getAutowireCapableBeanFactory();

    String webhookDataHandlerInboundChannelName =
        dataHandlerName + "." + WEBHOOK_DATA_HANDLER_INBOUND_CHANNEL_NAME;

    applicationBeanRegistry.registerSingleton(
        webhookDataHandlerInboundChannelName,
        webhookDataHandlerInboundChannel
    );

    webhookInboundRouter.setChannelMapping(dataHandlerName, webhookDataHandlerInboundChannelName);

    log.info("Data handler {} initialized.", dataHandlerContext.getId());
  }

  protected abstract String getDataHandlerName();

}
