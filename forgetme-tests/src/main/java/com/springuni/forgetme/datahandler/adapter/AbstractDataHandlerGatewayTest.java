package com.springuni.forgetme.datahandler.adapter;

import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;
import static org.springframework.http.HttpStatus.BAD_GATEWAY;
import static org.springframework.http.HttpStatus.BANDWIDTH_LIMIT_EXCEEDED;
import static org.springframework.http.HttpStatus.GATEWAY_TIMEOUT;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE;

import com.springuni.forgetme.core.model.ForgetRequest;
import java.net.URI;
import java.util.Objects;
import java.util.UUID;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.RetryOperations;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestOperations;

@RunWith(MockitoJUnitRunner.class)
public abstract class AbstractDataHandlerGatewayTest {

  protected static final String EMAIL = "test@springuni.com";

  private static final UUID SUBSCRIPTION_ID_VALUE = UUID
      .fromString("a408b7d4-49dc-427e-ad60-e5d8a0dc5925");

  private static final int MAX_ATTEMPTS = 3;

  @Mock
  private RestOperations restOperations;

  private RetryTemplate retryTemplate;

  private DataHandlerGateway dataHandlerGateway;

  @Before
  public void setUp() {
    retryTemplate = new RetryTemplate();
    retryTemplate.setRetryPolicy(new SimpleRetryPolicy(MAX_ATTEMPTS));
    dataHandlerGateway = createDataHandlerGateway(restOperations, retryTemplate);
  }

  @Test
  public void givenIoError_whenHandleForget_thenRetried() {
    testRetry(new ResourceAccessException("error"), 3);
  }

  @Test
  public void givenBadGatewayError_whenHandleForget_thenRetried() {
    testRetry(new HttpServerErrorException(BAD_GATEWAY), 3);
  }

  @Test
  public void givenServiceUnavailablError_whenHandleForget_thenRetried() {
    testRetry(new HttpServerErrorException(SERVICE_UNAVAILABLE), 3);
  }

  @Test
  public void givenGatewayTimeoutError_whenHandleForget_thenRetried() {
    testRetry(new HttpServerErrorException(GATEWAY_TIMEOUT), 3);
  }

  @Test
  public void givenBandwidthLimitExceededError_whenHandleForget_thenRetried() {
    testRetry(new HttpServerErrorException(BANDWIDTH_LIMIT_EXCEEDED), 3);
  }

  @Test
  public void givenInternalServerError_whenHandleForget_thenRetried() {
    testRetry(new HttpServerErrorException(INTERNAL_SERVER_ERROR), 1);
  }

  @Test
  public void givenOtherError_whenHandleForget_thenNotRetried() {
    testRetry(new RestClientException("error"), 1);
  }

  private void testRetry(Object result, int expectedAttempts) {
    if (result instanceof Throwable) {
      given(restOperations.exchange(any(RequestEntity.class), (Class<?>) isNull()))
          .willThrow((Throwable) result);
    } else if (result instanceof ResponseEntity<?>) {
      given(restOperations.exchange(any(RequestEntity.class), (Class<?>) isNull()))
          .willReturn((ResponseEntity) result);
    }

    dataHandlerGateway.handleForget(new ForgetRequest(SUBSCRIPTION_ID_VALUE, EMAIL));

    ArgumentCaptor<RequestEntity<?>> requestEntityArgumentCaptor =
        ArgumentCaptor.forClass(RequestEntity.class);

    then(restOperations).should(times(expectedAttempts))
        .exchange(requestEntityArgumentCaptor.capture(), (Class<?>) isNull());

    HttpMethod expectedHttpMethod = expectedHttpMethod();
    URI expectedUrl = expectedUrl();

    assertThat(
        requestEntityArgumentCaptor.getValue(),
        RequestEntityMatcher.of(expectedHttpMethod, expectedUrl)
    );
  }

  protected abstract DataHandlerGateway createDataHandlerGateway(
      RestOperations restOperations, RetryOperations retryOperations
  );

  protected abstract HttpMethod expectedHttpMethod();

  protected abstract URI expectedUrl();

  @RequiredArgsConstructor(staticName = "of")
  private static class RequestEntityMatcher extends TypeSafeMatcher<RequestEntity<?>> {

    @NonNull
    private final HttpMethod method;

    @NonNull
    private final URI url;

    @Override
    public void describeTo(Description description) {
      description.appendText("method ").appendValue(method).appendText("url ").appendValue(url);
    }

    @Override
    protected boolean matchesSafely(RequestEntity<?> requestEntity) {
      return Objects.equals(method, requestEntity.getMethod()) &&
          Objects.equals(url, requestEntity.getUrl());
    }

  }

}
