package com.springuni.forgetme.datahandler.adapter;

import static org.junit.Assert.assertTrue;

import com.springuni.forgetme.core.model.DataHandlerRegistry;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.integration.support.management.MappingMessageRouterManagement;
import org.springframework.messaging.MessageChannel;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@SpringBootTest
public abstract class AbstractAdapterFlowConfigTest {

  @MockBean
  private MappingMessageRouterManagement mappingMessageRouterManagement;

  @MockBean
  private DataHandlerRegistry dataHandlerRegistry;

  @MockBean(name = "webhookDataHandlerOutboundChannel")
  private MessageChannel webhookDataHandlerOutboundChannel;

  @MockBean(name = "subscriberDataHandlerOutboundChannel")
  private MessageChannel subscriberDataHandlerOutboundChannel;

  @Autowired
  private ApplicationContext applicationContext;

  ApplicationContext dataHandlerContext;

  @Before
  public void setUp() {
    dataHandlerContext =
        applicationContext
            .getBean(getDataHandlerName() + "-adapter-context", ApplicationContext.class);
  }

  @Test
  public void contextShouldContainSubscriberDataHandlerGateway() {
    assertTrue(dataHandlerContext.containsBean("subscriberDataHandlerGateway"));
  }

  @Test
  public void contextShouldContainWebhookDataTransformer() {
    assertTrue(dataHandlerContext.containsBean("webhookDataTransformer"));
  }

  protected abstract String getDataHandlerName();

}
