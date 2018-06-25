package com.springuni.forgetme.datahandler.adapter.mailerlite;

import java.io.IOException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

@RequiredArgsConstructor
public class MailerLiteRequestInterceptor implements ClientHttpRequestInterceptor {

  static final String MAILERLITE_API_KEY = "MailerLite-ApiKey";

  @NonNull
  private final String apiKey;

  @Override
  public ClientHttpResponse intercept(
      HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {

    request.getHeaders().add(MAILERLITE_API_KEY, apiKey);
    return execution.execute(request, body);
  }

  String getApiKey() {
    return apiKey;
  }

}
