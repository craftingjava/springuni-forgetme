package com.springuni.forgetme.datahandler.adapter.mailerlite;

import static com.springuni.forgetme.datahandler.adapter.mailerlite.MailerLiteRequestInterceptor.MAILERLITE_API_KEY;
import static java.util.Collections.singletonList;
import static org.hamcrest.Matchers.hasEntry;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.mock.http.client.MockClientHttpRequest;

public class MailerLiteRequestInterceptorTest {

  @Test
  public void shouldSendMailerLiteApiKey() throws IOException {
    HttpRequest httpRequest = new MockClientHttpRequest();
    byte[] httpRequestBody = new byte[0];
    ClientHttpRequestExecution requestExecution = mock(ClientHttpRequestExecution.class);

    ClientHttpRequestInterceptor requestInterceptor = new MailerLiteRequestInterceptor("test");
    requestInterceptor.intercept(httpRequest, httpRequestBody, requestExecution);

    ArgumentCaptor<HttpRequest> requestArgumentCaptor = ArgumentCaptor.forClass(HttpRequest.class);
    verify(requestExecution).execute(requestArgumentCaptor.capture(), eq(httpRequestBody));

    assertThat(
        requestArgumentCaptor.getValue().getHeaders(),
        hasEntry(MAILERLITE_API_KEY, singletonList("test"))
    );
  }

}
