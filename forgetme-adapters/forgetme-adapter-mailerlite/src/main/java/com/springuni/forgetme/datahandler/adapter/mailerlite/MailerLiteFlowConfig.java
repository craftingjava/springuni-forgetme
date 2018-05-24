package com.springuni.forgetme.datahandler.adapter.mailerlite;

import com.springuni.forgetme.datahandler.adapter.AbstractDataHandlerFlowConfig;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MailerLiteFlowConfig extends AbstractDataHandlerFlowConfig {

  private static final String DATA_HANDLER_NAME = "mailerlite";

  @Override
  protected String getDataHandlerName() {
    return DATA_HANDLER_NAME;
  }

}
