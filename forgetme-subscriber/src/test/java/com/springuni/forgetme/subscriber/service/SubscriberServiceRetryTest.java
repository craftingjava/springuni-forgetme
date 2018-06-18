package com.springuni.forgetme.subscriber.service;

import static com.springuni.forgetme.core.model.SubscriptionStatus.SUBSCRIPTION_CREATED;
import static com.springuni.forgetme.subscriber.Mocks.DATA_HANDLER_ID_VALUE;
import static com.springuni.forgetme.subscriber.Mocks.EMAIL;
import static com.springuni.forgetme.subscriber.Mocks.EMAIL_HASH;
import static com.springuni.forgetme.subscriber.Mocks.SUBSCRIBER_ID_VALUE;
import static com.springuni.forgetme.subscriber.Mocks.SUBSCRIPTION_ID_VALUE;
import static com.springuni.forgetme.subscriber.Mocks.createSubscription;
import static java.util.Collections.singletonList;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.NONE;

import com.springuni.forgetme.core.integration.RetryConfig;
import com.springuni.forgetme.core.model.DataHandlerRegistry;
import com.springuni.forgetme.core.model.ForgetResponse;
import com.springuni.forgetme.core.model.WebhookData;
import com.springuni.forgetme.subscriber.model.Subscriber;
import com.springuni.forgetme.subscriber.model.Subscription;
import com.springuni.forgetme.subscriber.service.SubscriberServiceRetryTest.TestConfig;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runner.RunWith;
import org.junit.runners.model.Statement;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.NonTransientDataAccessException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.dao.TransientDataAccessException;
import org.springframework.messaging.MessageChannel;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.RetryListener;
import org.springframework.test.context.junit4.SpringRunner;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = NONE, classes = TestConfig.class)
public class SubscriberServiceRetryTest {

  private static final TransientDataAccessException TRANSIENT_DATA_ACCESS_EXCEPTION =
      new OptimisticLockingFailureException("test");

  private static final NonTransientDataAccessException NON_TRANSIENT_DATA_ACCESS_EXCEPTION =
      new DuplicateKeyException("test");

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Rule
  public ExpectedRetry retried = new ExpectedRetry();

  @MockBean
  private DataHandlerRegistry dataHandlerRegistry;

  @MockBean
  private SubscriberRepository subscriberRepository;

  @MockBean
  private SubscriptionRepository subscriptionRepository;

  @MockBean
  private MessageChannel subscriberForgetRequestOutboundChannel;

  @Autowired
  private RetryListener retryListener;

  @Autowired
  private SubscriberService subscriberService;

  private Subscriber subscriber;

  @Before
  public void setUp() {
    reset(retryListener);
    when(retryListener.open(any(RetryContext.class), any(RetryCallback.class))).thenReturn(true);

    Subscription subscription = createSubscription();
    subscriber = subscription.getSubscriber();
    subscriber.setId(SUBSCRIBER_ID_VALUE);

    when(subscriberRepository.findByEmailHash(EMAIL_HASH)).thenReturn(Optional.of(subscriber));

    when(subscriptionRepository.findBySubscriberId(SUBSCRIBER_ID_VALUE))
        .then((Answer<List<Subscription>>) invocation -> singletonList(createSubscription()));

    when(subscriptionRepository.findById(SUBSCRIPTION_ID_VALUE))
        .thenReturn(Optional.of(subscription));
  }

  @Test
  public void givenTransientDataAccessException_whenUpdateSubscription_shouldRetry() {
    thrown.expect(TransientDataAccessException.class);
    retried.times(3);

    given(subscriberRepository.save(any(Subscriber.class)))
        .willThrow(TRANSIENT_DATA_ACCESS_EXCEPTION);

    subscriberService.updateSubscription(
        WebhookData.of(DATA_HANDLER_ID_VALUE, EMAIL, SUBSCRIPTION_CREATED),
        LocalDateTime.MIN
    );
  }

  @Test
  public void givenNonTransientDataAccessException_whenUpdateSubscription_shouldNotRetry() {
    thrown.expect(NonTransientDataAccessException.class);
    retried.times(1);

    given(subscriberRepository.save(any(Subscriber.class)))
        .willThrow(NON_TRANSIENT_DATA_ACCESS_EXCEPTION);

    subscriberService.updateSubscription(
        WebhookData.of(DATA_HANDLER_ID_VALUE, EMAIL, SUBSCRIPTION_CREATED),
        LocalDateTime.MIN
    );
  }

  @Test
  public void givenTransientDataAccessException_whenRequestForget_shouldRetry() {
    thrown.expect(TransientDataAccessException.class);
    retried.times(3);

    given(subscriptionRepository.save(any(Subscription.class)))
        .willThrow(TRANSIENT_DATA_ACCESS_EXCEPTION);

    subscriberService.requestForget(EMAIL);
  }

  @Test
  public void givenNonTransientDataAccessException_whenRequestForget_shouldNotRetry() {
    thrown.expect(NonTransientDataAccessException.class);
    retried.times(1);

    given(subscriptionRepository.save(any(Subscription.class)))
        .willThrow(NON_TRANSIENT_DATA_ACCESS_EXCEPTION);

    subscriberService.requestForget(EMAIL);
  }

  @Test
  public void givenTransientDataAccessException_whenRecordForgetResponse_shouldRetry() {
    thrown.expect(TransientDataAccessException.class);
    retried.times(3);

    given(subscriptionRepository.save(any(Subscription.class)))
        .willThrow(TRANSIENT_DATA_ACCESS_EXCEPTION);

    subscriberService.recordForgetResponse(
        new ForgetResponse(SUBSCRIPTION_ID_VALUE, true), LocalDateTime.MIN
    );
  }

  @Test
  public void givenNonTransientDataAccessException_whenRecordForgetResponse_shouldNotRetry() {
    thrown.expect(NonTransientDataAccessException.class);
    retried.times(1);

    given(subscriptionRepository.save(any(Subscription.class)))
        .willThrow(NON_TRANSIENT_DATA_ACCESS_EXCEPTION);

    subscriberService.recordForgetResponse(
        new ForgetResponse(SUBSCRIPTION_ID_VALUE, true), LocalDateTime.MIN
    );
  }

  @Configuration
  @Import(RetryConfig.class)
  public static class TestConfig {

    @Bean
    RetryListener retryListener() {
      return mock(RetryListener.class);
    }

    @Bean
    List<RetryListener> retryListeners(RetryListener retryListener) {
      return singletonList(retryListener);
    }

    @Bean
    SubscriberService subscriberService(
        DataHandlerRegistry dataHandlerRegistry, SubscriberRepository subscriberRepository,
        SubscriptionRepository subscriptionRepository,
        MessageChannel subscriberForgetRequestOutboundChannel) {

      return new SubscriberServiceImpl(
          dataHandlerRegistry,
          subscriberRepository,
          subscriptionRepository,
          subscriberForgetRequestOutboundChannel
      );
    }

  }

  private class ExpectedRetry implements TestRule {

    private int times = 1;

    void times(int times) {
      this.times = times;
    }

    @Override
    public Statement apply(Statement base, Description description) {
      return new Statement() {

        @Override
        public void evaluate() throws Throwable {
          try {
            base.evaluate();
          } finally {
            verify(retryListener, Mockito.times(times))
                .onError(any(RetryContext.class), any(RetryCallback.class), any(Throwable.class));
          }
        }

      };
    }

  }

}
