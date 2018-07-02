package com.springuni.forgetme.datahandler.adapter;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.springuni.forgetme.datahandler.adapter.DataHandlerConfiguredConditionTest.TestConfig;
import java.util.Map;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@SpringBootTest(classes = TestConfig.class)
public class DataHandlerConfiguredConditionTest {

  @Autowired
  private ApplicationContext applicationContext;

  @Test
  public void shouldHaveMatchingConditionalBean() {
    assertTrue(applicationContext.containsBean("matchingConditionalBean"));
  }

  @Test
  public void shouldNotHaveNotMatchingConditionalBean() {
    assertFalse(applicationContext.containsBean("notMatchingConditionalBean"));
  }

  static class MatchingDataHandlerConfiguredCondition
      extends AbstractDataHandlerConfiguredCondition {

    public MatchingDataHandlerConfiguredCondition() {
      super("mailerlite");
    }

    @Override
    protected boolean isDataHandlerConfigured(Map<String, String> dataHandlerProperties) {
      return dataHandlerProperties.containsKey("api-key");
    }

  }

  static class NotMatchingDataHandlerConfiguredCondition
      extends AbstractDataHandlerConfiguredCondition {

    public NotMatchingDataHandlerConfiguredCondition() {
      super("not-matching");
    }

    @Override
    protected boolean isDataHandlerConfigured(Map<String, String> dataHandlerProperties) {
      return dataHandlerProperties.containsKey("key");
    }

  }

  @Configuration
  static class TestConfig {

    @Bean
    @Conditional(MatchingDataHandlerConfiguredCondition.class)
    String matchingConditionalBean() {
      return "matching";
    }

    @Bean
    @Conditional(NotMatchingDataHandlerConfiguredCondition.class)
    String notMatchingConditionalBean() {
      return "not-matching";
    }

  }

}
