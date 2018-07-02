package com.springuni.forgetme.datahandler.adapter;

import com.springuni.forgetme.core.model.ForgetRequest;
import com.springuni.forgetme.core.model.ForgetResponse;
import java.net.URI;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.RetryOperations;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestOperations;

@Slf4j
@RequiredArgsConstructor
public abstract class AbstractDataHandlerGateway implements DataHandlerGateway {

  @NonNull
  private final RestOperations restOperations;

  @NonNull
  private final RetryOperations retryOperations;

  @Override
  public final ForgetResponse handleForget(ForgetRequest forgetRequest) {
    HttpMethod httpMethod = getHttpMethod();
    URI uri = buildUri(forgetRequest.getSubscriberEmail());

    RequestEntity<?> requestEntity = RequestEntity.method(httpMethod, uri).build();

    RestRetryCallback<?> retryCallback =
        RestRetryCallback.of(restOperations, requestEntity, null);

    HttpStatus httpStatus;
    try {
      ResponseEntity<?> responseEntity = retryOperations.execute(retryCallback);
      httpStatus = responseEntity.getStatusCode();
    } catch (HttpStatusCodeException e) {
      httpStatus = e.getStatusCode();
    } catch (RestClientException e) {
      log.error("Failed {} request to {}; reason: {}.", httpMethod, uri, e.getMessage(), e);
      return new ForgetResponse(forgetRequest.getSubscriptionId(), false);
    }

    boolean acknowledged = isSuccessful(httpStatus);

    if (!acknowledged) {
      log.warn("Failed {} request to {}; status: {}.", httpMethod, uri, httpStatus);
    }

    return new ForgetResponse(forgetRequest.getSubscriptionId(), acknowledged);
  }

  protected abstract URI buildUri(@NonNull String email);

  @NonNull
  protected abstract HttpMethod getHttpMethod();

  protected boolean isSuccessful(@NonNull HttpStatus httpStatus) {
    return httpStatus.is2xxSuccessful();
  }

}
