package com.springuni.forgetme.datahandler.adapter;

import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import com.springuni.forgetme.core.integration.RetryConfig;
import com.springuni.forgetme.core.adapter.DataHandlerRegistry;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import org.springframework.integration.support.management.MappingMessageRouterManagement;
import org.springframework.messaging.MessageChannel;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@SpringBootTest
@Import(RetryConfig.class)
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

  private ApplicationContext dataHandlerContext;

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

  @Test
  public void contextShouldContainEnvironmentProperties() {
    Map<String, String> properties = expectedDataHandlerNameProperties();
    assertThat(dataHandlerContext.getEnvironment(), EnvironmentMatcher.of(properties));
  }

  protected abstract Map<String, String> expectedDataHandlerNameProperties();

  protected <T> T getBean(String name, Class<T> requiredType) {
    return dataHandlerContext.getBean(name, requiredType);
  }

  protected abstract String getDataHandlerName();

  @RequiredArgsConstructor(staticName = "of")
  private static class EnvironmentMatcher extends TypeSafeMatcher<Environment> {

    private final Map<String, String> properties;

    @Override
    public void describeTo(Description description) {
      description.appendText("properties ").appendValue(properties);
    }

    @Override
    protected boolean matchesSafely(Environment environment) {
      return properties.entrySet()
          .stream()
          .allMatch(it -> it.getValue().equals(environment.getProperty(it.getKey())));
    }

  }

}
