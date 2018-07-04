package com.springuni.forgetme.subscriber.service;

import static com.springuni.forgetme.core.model.MessageHeaderNames.DATA_HANDLER_NAME;
import static com.springuni.forgetme.core.model.SubscriptionStatus.FORGET_FAILED;
import static com.springuni.forgetme.core.model.SubscriptionStatus.FORGET_PENDING;
import static com.springuni.forgetme.core.model.SubscriptionStatus.FORGOTTEN;
import static com.springuni.forgetme.core.model.SubscriptionStatus.SUBSCRIPTION_CREATED;
import static com.springuni.forgetme.core.model.SubscriptionStatus.UNSUBSCRIBED;
import static com.springuni.forgetme.subscriber.Mocks.DATA_HANDLER_ID_VALUE;
import static com.springuni.forgetme.subscriber.Mocks.DATA_HANDLER_NAME_VALUE;
import static com.springuni.forgetme.subscriber.Mocks.EMAIL;
import static com.springuni.forgetme.subscriber.Mocks.EMAIL_HASH;
import static com.springuni.forgetme.subscriber.Mocks.EVENT_TIMESTAMP_VALUE;
import static com.springuni.forgetme.subscriber.Mocks.SUBSCRIBER_ID_VALUE;
import static com.springuni.forgetme.subscriber.Mocks.SUBSCRIPTION_ID_VALUE;
import static com.springuni.forgetme.subscriber.Mocks.createSubscriber;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.when;

import com.springuni.forgetme.core.model.EntityNotFoundException;
import com.springuni.forgetme.core.model.ForgetRequest;
import com.springuni.forgetme.core.model.ForgetResponse;
import com.springuni.forgetme.core.model.SubscriptionStatus;
import com.springuni.forgetme.core.model.WebhookData;
import com.springuni.forgetme.subscriber.model.Subscriber;
import com.springuni.forgetme.subscriber.model.Subscription;
import java.util.Optional;
import java.util.UUID;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.transaction.support.TransactionSynchronizationUtils;

@RunWith(MockitoJUnitRunner.class)
public class SubscriberServiceTest {

  @Mock
  private SubscriberRepository subscriberRepository;

  @Mock
  private SubscriptionRepository subscriptionRepository;

  @Mock
  private MessageChannel subscriberForgetRequestOutboundChannel;

  @InjectMocks
  private SubscriberServiceImpl subscriberService;

  private Subscriber subscriber;

  @Before
  public void setUp() {
    subscriber = createSubscriber();

    subscriber.updateSubscription(
        DATA_HANDLER_NAME_VALUE,
        SUBSCRIPTION_CREATED,
        EVENT_TIMESTAMP_VALUE
    );

    subscriber.setId(SUBSCRIBER_ID_VALUE);
    subscriber.getSubscriptions().get(0).setId(SUBSCRIPTION_ID_VALUE);

    when(subscriberRepository.save(any(Subscriber.class))).thenAnswer(returnsFirstArg());

    TransactionSynchronizationManager.initSynchronization();
  }

  @After
  public void tearDown() {
    TransactionSynchronizationManager.clearSynchronization();
  }

  /// getSubscriber ///

  @Test
  public void givenKnownEmail_whenGetSubscriber_thenSubscriberReturned() {
    given(subscriberRepository.findByEmailHash(EMAIL_HASH)).willReturn(Optional.of(subscriber));
    Subscriber subscriber = subscriberService.getSubscriber(EMAIL);
    assertNotNull(subscriber);
  }

  @Test(expected = EntityNotFoundException.class)
  public void givenUnknownEmail_whenGetSubscriber_thenEntityNotFoundException() {
    given(subscriberRepository.findByEmailHash(EMAIL_HASH)).willReturn(Optional.empty());
    subscriberService.getSubscriber(EMAIL);
  }

  /// updateSubscription ///

  @Test
  public void givenKnownEmail_whenUpdateSubscription_thenSubscriptionUpdated() {
    given(subscriberRepository.findByEmailHash(EMAIL_HASH)).willReturn(Optional.of(subscriber));

    subscriberService.updateSubscription(
        WebhookData.of(EMAIL, UNSUBSCRIBED),
        DATA_HANDLER_NAME_VALUE,
        EVENT_TIMESTAMP_VALUE
    );

    assertSubscriptionStatusFromSavedSubscriber(UNSUBSCRIBED, DATA_HANDLER_NAME_VALUE);
  }

  @Test
  public void givenUnknownEmail_whenUpdateSubscriber_thenNewSubscriberSaved() {
    given(subscriberRepository.findByEmailHash(EMAIL_HASH)).willReturn(Optional.empty());

    subscriberService.updateSubscription(
        WebhookData.of(EMAIL, SUBSCRIPTION_CREATED),
        DATA_HANDLER_NAME_VALUE,
        EVENT_TIMESTAMP_VALUE
    );

    assertSubscriptionStatusFromSavedSubscriber(SUBSCRIPTION_CREATED, DATA_HANDLER_NAME_VALUE);
  }

  /// requestForget ///

  @Test
  public void givenKnownEmail_whenRequestForget_thenSubscriptionsUpdated() {
    given(subscriberRepository.findByEmailHash(EMAIL_HASH)).willReturn(Optional.of(subscriber));

    subscriberService.requestForget(EMAIL);
    TransactionSynchronizationUtils.triggerAfterCommit();

    then(subscriberForgetRequestOutboundChannel).should().send(any(Message.class));

    assertSubscriptionStatusFromSavedSubscription(FORGET_PENDING, DATA_HANDLER_ID_VALUE);

    assertForgetRequestMessage(DATA_HANDLER_NAME_VALUE, SUBSCRIPTION_ID_VALUE, EMAIL);
  }

  @Test
  public void givenKnownEmail_andForgotten_whenRequestForget_thenSubscriptionsUpdated() {
    subscriber.getSubscriptions().forEach(it -> it.updateStatus(FORGOTTEN));

    given(subscriberRepository.findByEmailHash(EMAIL_HASH)).willReturn(Optional.of(subscriber));

    subscriberService.requestForget(EMAIL);

    then(subscriberForgetRequestOutboundChannel).should(never()).send(any(Message.class));
    then(subscriptionRepository).should(never()).save(any(Subscription.class));
  }

  @Test(expected = EntityNotFoundException.class)
  public void givenUnknownEmail_whenRequestForget_thenEntityNotFoundException() {
    given(subscriberRepository.findByEmailHash(EMAIL_HASH)).willReturn(Optional.empty());

    subscriberService.requestForget(EMAIL);
  }

  /// recordForgetResponse ///

  @Test(expected = EntityNotFoundException.class)
  public void givenUnknownSubscriptionId_whenRecordForgetResponse_thenEntityNotFoundException() {
    given(subscriberRepository.findBySubscriptionId(SUBSCRIPTION_ID_VALUE))
        .willReturn(Optional.empty());

    subscriberService.recordForgetResponse(
        new ForgetResponse(SUBSCRIPTION_ID_VALUE, true), EVENT_TIMESTAMP_VALUE
    );
  }

  @Test
  public void givenKnownSubscriptionId_withACK_whenRecordForgetResponse_thenSubscriptionsUpdated() {
    given(subscriberRepository.findBySubscriptionId(SUBSCRIPTION_ID_VALUE))
        .willReturn(Optional.of(subscriber));

    subscriberService.recordForgetResponse(
        new ForgetResponse(SUBSCRIPTION_ID_VALUE, true), EVENT_TIMESTAMP_VALUE
    );

    assertSubscriptionStatusFromSavedSubscription(FORGOTTEN, DATA_HANDLER_ID_VALUE);
  }

  @Test
  public void givenKnownSubscriptionId_withNAK_whenRecordForgetResponse_thenSubscriptionsUpdated() {
    given(subscriberRepository.findBySubscriptionId(SUBSCRIPTION_ID_VALUE))
        .willReturn(Optional.of(subscriber));

    subscriberService.recordForgetResponse(
        new ForgetResponse(SUBSCRIPTION_ID_VALUE, false), EVENT_TIMESTAMP_VALUE
    );

    assertSubscriptionStatusFromSavedSubscription(FORGET_FAILED, DATA_HANDLER_ID_VALUE);
  }

  private void assertForgetRequestMessage(
      String expectedDataHandlerName, UUID expectedSubscriptionId, String expectedEmail) {

    ArgumentCaptor<Message<ForgetRequest>> messageArgumentCaptor =
        ArgumentCaptor.forClass(Message.class);

    then(subscriberForgetRequestOutboundChannel).should().send(messageArgumentCaptor.capture());

    Message<ForgetRequest> forgetRequestMessage = messageArgumentCaptor.getValue();
    assertEquals(expectedDataHandlerName, forgetRequestMessage.getHeaders().get(
        DATA_HANDLER_NAME));
    assertEquals(expectedSubscriptionId, forgetRequestMessage.getPayload().getSubscriptionId());
    assertEquals(expectedEmail, forgetRequestMessage.getPayload().getSubscriberEmail());
  }

  private void assertSubscriptionStatusFromSavedSubscriber(
      SubscriptionStatus expectedStatus, String dataHandlerName) {

    ArgumentCaptor<Subscriber> subscriberArgumentCaptor = ArgumentCaptor.forClass(Subscriber.class);
    then(subscriberRepository).should().save(subscriberArgumentCaptor.capture());

    Subscription subscription = subscriber.getSubscriptions()
        .stream()
        .filter(it -> dataHandlerName.equals(it.getDataHandlerName()))
        .findFirst()
        .get();

    assertEquals(expectedStatus, subscription.getStatus());
  }

  private void assertSubscriptionStatusFromSavedSubscription(
      SubscriptionStatus expectedStatus, UUID dataHandlerId) {

    ArgumentCaptor<Subscription> subscriptionArgumentCaptor = ArgumentCaptor
        .forClass(Subscription.class);
    then(subscriptionRepository).should().save(subscriptionArgumentCaptor.capture());

    assertEquals(expectedStatus, subscriptionArgumentCaptor.getValue().getStatus());
  }

}
