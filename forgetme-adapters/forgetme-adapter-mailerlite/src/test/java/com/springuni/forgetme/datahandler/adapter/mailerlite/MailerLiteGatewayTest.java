package com.springuni.forgetme.datahandler.adapter.mailerlite;

import static org.springframework.http.HttpMethod.POST;

import com.springuni.forgetme.core.integration.DataHandlerGateway;
import com.springuni.forgetme.datahandler.adapter.AbstractDataHandlerGatewayTest;
import java.net.URI;
import org.springframework.http.HttpMethod;
import org.springframework.retry.RetryOperations;
import org.springframework.web.client.RestOperations;

public class MailerLiteGatewayTest extends AbstractDataHandlerGatewayTest {

  @Override
  protected DataHandlerGateway createDataHandlerGateway(
      RestOperations restOperations, RetryOperations retryOperations) {

    return new MailerLiteGateway(restOperations, retryOperations);
  }

  @Override
  protected HttpMethod expectedHttpMethod() {
    return POST;
  }

  @Override
  protected URI expectedUrl() {
    return URI.create("https://api.mailerlite.com/api/v2/subscribers/" + EMAIL + "/forget");
  }

}
