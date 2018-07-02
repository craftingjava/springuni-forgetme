package com.springuni.forgetme.datahandler.adapter;

import java.net.URI;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestOperations;

@Slf4j
@RequiredArgsConstructor(staticName = "of")
public class RestRetryCallback<T> implements RetryCallback<ResponseEntity<T>, RestClientException> {

  private final RestOperations restOperations;
  private final RequestEntity<?> requestEntity;
  private final Class<T> responseType;

  @Override
  public ResponseEntity<T> doWithRetry(RetryContext retryContext) throws RestClientException {
    Throwable throwable = retryContext.getLastThrowable();
    if (throwable != null) {
      logError(requestEntity.getUrl(), throwable, retryContext.getRetryCount());
    }

    try {
      return restOperations.exchange(requestEntity, responseType);
    } catch (HttpServerErrorException hsee) {
      HttpStatus httpStatus = hsee.getStatusCode();
      switch (httpStatus) {
        case BAD_GATEWAY:
        case SERVICE_UNAVAILABLE:
        case GATEWAY_TIMEOUT:
        case BANDWIDTH_LIMIT_EXCEEDED:
          // Retry for HTTP 502, 503, 504 and 509 server errors
          throw hsee;
        default:
          // Do not retry for the rest
          retryContext.setExhaustedOnly();
          throw hsee;
      }
    } catch (ResourceAccessException rse) {
      // Retry for I/O errors
      throw rse;
    } catch (RestClientException rce) {
      // Do not retry for other (eg. client) errors
      retryContext.setExhaustedOnly();
      throw rce;
    }
  }

  private void logError(URI uri, Throwable throwable, int attempt) {
    StringBuilder stringBuilder = new StringBuilder();

    if (attempt > 0) {
      stringBuilder.append("Attempt #");
      stringBuilder.append(attempt);
      stringBuilder.append(" for request");
    } else {
      stringBuilder.append("Request");
    }

    Optional<HttpStatusCodeException> hsce =
        Optional.of(throwable)
            .filter(it -> it instanceof HttpStatusCodeException)
            .map(it -> (HttpStatusCodeException) it);

    stringBuilder.append(" to ");
    stringBuilder.append(uri);
    stringBuilder.append(" has failed; status=");

    HttpStatus statusCode = hsce.map(HttpStatusCodeException::getStatusCode).orElse(null);

    stringBuilder.append(statusCode);
    stringBuilder.append(", response=");

    String responseBody = hsce.map(HttpStatusCodeException::getResponseBodyAsString).orElse("");

    stringBuilder.append(responseBody);
    stringBuilder.append(".");

    log.error(stringBuilder.toString(), throwable);
  }

}
