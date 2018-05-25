package com.springuni.forgetme.datahandler.adapter.mailerlite;

import com.springuni.forgetme.core.model.ForgetRequest;
import com.springuni.forgetme.core.model.ForgetResponse;
import com.springuni.forgetme.datahandler.adapter.AbstractDataHandlerGateway;

public class MailerLiteGateway extends AbstractDataHandlerGateway {

  @Override
  public ForgetResponse handleForget(ForgetRequest forgetRequest) {
    // TODO: Add real implementaion later
    return new ForgetResponse(forgetRequest.getSubscriptionId(), true);
  }

}
