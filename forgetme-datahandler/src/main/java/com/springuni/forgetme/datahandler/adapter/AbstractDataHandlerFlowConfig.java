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

  private static final String QUALIFIED_CHANNEL_NAME_DELIMITER = "#";

  private static final String WEBHOOK_DATA_HANDLER_INBOUND_CHANNEL_NAME =
      "webhookDataHandlerInboundChannel";

  private static final String SUBSCRIBER_DATA_HANDLER_INBOUND_CHANNEL_NAME =
      "subscriberDataHandlerInboundChannel";

  @Autowired
  private ApplicationContext applicationContext;

  @Autowired
  private MappingMessageRouterManagement webhookInboundRouter;

  @Autowired
  private MappingMessageRouterManagement subscriberForgetRequestRouter;

  private ConfigurableApplicationContext dataHandlerContext;
  private SingletonBeanRegistry applicationBeanRegistry;

  @Override
  public void afterPropertiesSet() {
    String dataHandlerName = getDataHandlerName();

    dataHandlerContext = new ClassPathXmlApplicationContext(
        new String[]{"/META-INF/spring/" + dataHandlerName + "-adapter-config.xml"},
        false,
        applicationContext);

    dataHandlerContext.setId(dataHandlerName);
    dataHandlerContext.refresh();

    applicationBeanRegistry =
        (SingletonBeanRegistry) applicationContext.getAutowireCapableBeanFactory();

    // Register channel for webhook dynamically

    registerMessageChannel(WEBHOOK_DATA_HANDLER_INBOUND_CHANNEL_NAME, webhookInboundRouter);

    // Register channel for forget response dynamically

    registerMessageChannel(
        SUBSCRIBER_DATA_HANDLER_INBOUND_CHANNEL_NAME, subscriberForgetRequestRouter
    );

    log.info("Data handler {} initialized.", dataHandlerContext.getId());
  }

  protected abstract String getDataHandlerName();

  private void registerMessageChannel(
      String channelName, MappingMessageRouterManagement routerManagement) {

    String dataHandlerName = getDataHandlerName();

    String qualifiedChannelName = dataHandlerName + QUALIFIED_CHANNEL_NAME_DELIMITER + channelName;

    MessageChannel messageChannel = dataHandlerContext.getBean(channelName, MessageChannel.class);

    applicationBeanRegistry.registerSingleton(
        qualifiedChannelName,
        messageChannel
    );

    routerManagement.setChannelMapping(
        dataHandlerName,
        qualifiedChannelName
    );
  }

}
