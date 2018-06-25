package com.springuni.forgetme.datahandler.adapter.mailerlite;

import com.springuni.forgetme.core.model.ForgetRequest;
import com.springuni.forgetme.core.model.ForgetResponse;
import com.springuni.forgetme.datahandler.adapter.AbstractDataHandlerGateway;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.client.RestOperations;

@RequiredArgsConstructor
public class MailerLiteGateway extends AbstractDataHandlerGateway {

  @NonNull
  private final RestOperations restOperations;

  @Override
  public ForgetResponse handleForget(ForgetRequest forgetRequest) {
    // TODO: Add real implementaion later
    return new ForgetResponse(forgetRequest.getSubscriptionId(), true);
  }

}
