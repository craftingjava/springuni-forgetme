package com.springuni.forgetme.datahandler.adapter.mailerlite;

import static org.springframework.http.HttpMethod.POST;

import com.springuni.forgetme.datahandler.adapter.AbstractDataHandlerGateway;
import java.net.URI;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.retry.RetryOperations;
import org.springframework.web.client.RestOperations;

@Slf4j
public class MailerLiteGateway extends AbstractDataHandlerGateway {

  static final String MAILERLITE_API_BASE = "http://api.mailerlite.com/api/v2";

  public MailerLiteGateway(RestOperations restOperations, RetryOperations retryOperations) {
    super(restOperations, retryOperations);
  }

  @Override
  protected URI buildUri(String email) {
    return URI.create(MAILERLITE_API_BASE + "/subscribers/" + email + "/forget");
  }

  @Override
  protected HttpMethod getHttpMethod() {
    return POST;
  }
}
