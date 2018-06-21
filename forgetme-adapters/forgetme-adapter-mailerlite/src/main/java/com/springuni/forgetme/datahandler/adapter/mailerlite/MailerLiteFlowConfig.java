package com.springuni.forgetme.datahandler.adapter.mailerlite;

import com.springuni.forgetme.datahandler.adapter.AbstractDataHandlerConfiguredCondition;
import com.springuni.forgetme.datahandler.adapter.AbstractDataHandlerFlowConfig;
import com.springuni.forgetme.datahandler.adapter.mailerlite.MailerLiteFlowConfig.MailerLiteConfiguredCondition;
import java.util.Map;
import java.util.Optional;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

@Configuration
@Conditional(MailerLiteConfiguredCondition.class)
public class MailerLiteFlowConfig extends AbstractDataHandlerFlowConfig {

  static final String DATA_HANDLER_NAME = "mailerlite";

  @Override
  protected String getDataHandlerName() {
    return DATA_HANDLER_NAME;
  }

  static class MailerLiteConfiguredCondition extends AbstractDataHandlerConfiguredCondition {

    public MailerLiteConfiguredCondition() {
      super(DATA_HANDLER_NAME);
    }

    @Override
    protected boolean isDataHandlerConfigured(Map<String, String> dataHandlerProperties) {
      return Optional.ofNullable(dataHandlerProperties.get("api-key"))
          .filter(StringUtils::hasText)
          .isPresent();
    }

  }

}
