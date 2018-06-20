package com.springuni.forgetme.datahandler.adapter;

import com.springuni.forgetme.core.model.DataHandlerRegistry;
import java.util.UUID;
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

  @Autowired
  private DataHandlerRegistry dataHandlerRegistry;

  private ConfigurableApplicationContext dataHandlerContext;

  @Override
  public void afterPropertiesSet() {
    String dataHandlerName = getDataHandlerName();

    initDataHandler(dataHandlerName);

    dataHandlerContext = new ClassPathXmlApplicationContext(
        new String[]{"/META-INF/spring/" + dataHandlerName + "-adapter-config.xml"},
        false,
        applicationContext);

    dataHandlerContext.setId(dataHandlerName);
    dataHandlerContext.refresh();

    SingletonBeanRegistry applicationBeanRegistry =
        (SingletonBeanRegistry) applicationContext.getAutowireCapableBeanFactory();

    // Register channel for webhook dynamically

    registerMessageChannel(
        dataHandlerName,
        WEBHOOK_DATA_HANDLER_INBOUND_CHANNEL_NAME,
        webhookInboundRouter,
        applicationBeanRegistry
    );

    // Register channel for forget response dynamically

    registerMessageChannel(
        dataHandlerName,
        SUBSCRIBER_DATA_HANDLER_INBOUND_CHANNEL_NAME,
        subscriberForgetRequestRouter,
        applicationBeanRegistry
    );

    log.info("Data handler context {} initialized.", dataHandlerContext.getId());
  }

  protected abstract String getDataHandlerName();

  private void initDataHandler(String dataHandlerName) {
    UUID dataHandlerId = dataHandlerRegistry.register(dataHandlerName);
    log.info("Data handler UUID is {} for {}.", dataHandlerId, dataHandlerName);
  }

  private void registerMessageChannel(
      String dataHandlerName, String channelName, MappingMessageRouterManagement routerManagement,
      SingletonBeanRegistry applicationBeanRegistry) {

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
