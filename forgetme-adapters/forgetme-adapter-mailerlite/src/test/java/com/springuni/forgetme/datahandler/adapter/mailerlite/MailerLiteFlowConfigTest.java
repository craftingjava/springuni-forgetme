package com.springuni.forgetme.datahandler.adapter.mailerlite;

import static com.springuni.forgetme.datahandler.adapter.mailerlite.MailerLiteFlowConfig.DATA_HANDLER_NAME;

import com.springuni.forgetme.datahandler.adapter.AbstractAdapterFlowConfigTest;
import com.springuni.forgetme.datahandler.adapter.mailerlite.MailerLiteFlowConfigTest.TestConfig;
import java.util.Map;
import org.assertj.core.util.Maps;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

@ContextConfiguration(classes = TestConfig.class)
@TestPropertySource(properties = "MAILERLITE_API_KEY = test")
public class MailerLiteFlowConfigTest extends AbstractAdapterFlowConfigTest {

  @Override
  protected Map<String, String> expectedDataHandlerNameProperties() {
    return Maps.newHashMap("api-key", "test");
  }

  @Override
  protected String getDataHandlerName() {
    return DATA_HANDLER_NAME;
  }

  @Configuration
  @Import(MailerLiteFlowConfig.class)
  static class TestConfig {

  }

}
