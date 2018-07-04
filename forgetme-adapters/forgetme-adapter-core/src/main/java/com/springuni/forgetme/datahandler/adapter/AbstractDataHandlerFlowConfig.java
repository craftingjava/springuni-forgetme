package com.springuni.forgetme.datahandler.adapter;

import static com.springuni.forgetme.core.adapter.DataHandlerRegistration.DATA_HANDLER_PROVIDER_BINDABLE;
import static com.springuni.forgetme.core.adapter.DataHandlerRegistration.DATA_HANDLER_PROVIDER_PREFIX;
import static com.springuni.forgetme.core.adapter.DataHandlerRegistration.DATA_HANDLER_REGISTRATION_BINDABLE;
import static com.springuni.forgetme.core.adapter.DataHandlerRegistration.DATA_HANDLER_REGISTRATION_PREFIX;

import com.springuni.forgetme.core.adapter.DataHandlerRegistration;
import java.util.Collections;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.SingletonBeanRegistry;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertySource;
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

  @Override
  public void afterPropertiesSet() {
    String dataHandlerName = getDataHandlerName();

    SingletonBeanRegistry applicationBeanRegistry =
        (SingletonBeanRegistry) applicationContext.getAutowireCapableBeanFactory();

    dataHandlerContext = createDataHandlerContext(dataHandlerName, applicationBeanRegistry);

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

  private ConfigurableApplicationContext createDataHandlerContext(
      String dataHandlerName, SingletonBeanRegistry applicationBeanRegistry) {

    // Create child application context

    ConfigurableApplicationContext dataHandlerContext = new ClassPathXmlApplicationContext(
        new String[]{"/META-INF/spring/" + dataHandlerName + "-adapter-config.xml"},
        false,
        applicationContext);

    String dataHandlerContextId = dataHandlerName + "-adapter-context";

    dataHandlerContext.setId(dataHandlerContextId);

    String propertyName = DATA_HANDLER_PROVIDER_PREFIX + "." + dataHandlerName;

    Environment environment = applicationContext.getEnvironment();

    Map<String, Object> properties = Binder.get(applicationContext.getEnvironment())
        .bind(propertyName, DATA_HANDLER_PROVIDER_BINDABLE)
        .map(Collections::<String, Object>unmodifiableMap)
        .orElse(Collections.emptyMap());

    ConfigurableEnvironment dataHandlerEnvironment = new DataHandlerEnvironment(properties);

    dataHandlerContext.setEnvironment(dataHandlerEnvironment);
    dataHandlerContext.refresh();

    // Register child context as a bean to the parent context

    applicationBeanRegistry.registerSingleton(
        dataHandlerContextId,
        dataHandlerContext
    );

    // Register data handler

    String dataHandlerRegistrationName = DATA_HANDLER_REGISTRATION_PREFIX + "." + dataHandlerName;
    DataHandlerRegistration dataHandlerRegistration = Binder.get(environment)
        .bind(dataHandlerRegistrationName, DATA_HANDLER_REGISTRATION_BINDABLE)
        .get();

    applicationContext.publishEvent(dataHandlerRegistration);

    return dataHandlerContext;
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

  private static class DataHandlerEnvironment extends AbstractEnvironment {

    DataHandlerEnvironment(Map<String, Object> properties) {
      PropertySource<?> propertySource =
          new MapPropertySource("dataHandlerProperties", properties);

      getPropertySources().addLast(propertySource);
    }

  }

}
