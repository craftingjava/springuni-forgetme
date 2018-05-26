package com.springuni.forgetme.subscriber.service;

import static com.springuni.forgetme.Mocks.DATA_HANDLER_ID_VALUE;
import static com.springuni.forgetme.Mocks.DATA_HANDLER_NAME_VALUE;
import static com.springuni.forgetme.Mocks.EMAIL;
import static com.springuni.forgetme.Mocks.EMAIL_HASH;
import static com.springuni.forgetme.Mocks.SUBSCRIBER_ID_VALUE;
import static com.springuni.forgetme.Mocks.SUBSCRIPTION_ID_VALUE;
import static com.springuni.forgetme.Mocks.createSubscriber;
import static com.springuni.forgetme.core.model.MessageHeaderNames.DATA_HANDLER_ID;
import static com.springuni.forgetme.core.model.MessageHeaderNames.DATA_HANDLER_NAME;
import static com.springuni.forgetme.core.model.SubscriptionStatus.FORGET_FAILED;
import static com.springuni.forgetme.core.model.SubscriptionStatus.FORGET_PENDING;
import static com.springuni.forgetme.core.model.SubscriptionStatus.FORGOTTEN;
import static com.springuni.forgetme.core.model.SubscriptionStatus.SUBSCRIPTION_CREATED;
import static com.springuni.forgetme.core.model.SubscriptionStatus.UNSUBSCRIBED;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.when;

import com.springuni.forgetme.core.model.DataHandlerRegistry;
import com.springuni.forgetme.core.model.EntityNotFoundException;
import com.springuni.forgetme.core.model.ForgetRequest;
import com.springuni.forgetme.core.model.ForgetResponse;
import com.springuni.forgetme.core.model.SubscriptionStatus;
import com.springuni.forgetme.core.model.WebhookData;
import com.springuni.forgetme.subscriber.model.Subscriber;
import com.springuni.forgetme.subscriber.model.Subscription;
import java.util.Optional;
import java.util.UUID;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;

@RunWith(MockitoJUnitRunner.class)
public class SubscriberServiceTest {

  @Mock
  private DataHandlerRegistry dataHandlerRegistry;

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
    subscriber.updateSubscription(DATA_HANDLER_ID_VALUE, SUBSCRIPTION_CREATED);
    subscriber.setId(SUBSCRIBER_ID_VALUE);
    subscriber.getSubscriptions().get(0).setId(SUBSCRIPTION_ID_VALUE);

    when(dataHandlerRegistry.lookup(DATA_HANDLER_ID_VALUE)).thenReturn(DATA_HANDLER_NAME_VALUE);
    when(subscriberRepository.save(any(Subscriber.class))).thenAnswer(returnsFirstArg());
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

    subscriberService
        .updateSubscription(WebhookData.of(DATA_HANDLER_ID_VALUE, EMAIL, UNSUBSCRIBED));

    assertSubscriptionStatusFromSavedSubscriber(UNSUBSCRIBED, DATA_HANDLER_ID_VALUE);
  }

  @Test
  public void givenUnknownEmail_whenUpdateSubscriber_thenNewSubscriberSaved() {
    given(subscriberRepository.findByEmailHash(EMAIL_HASH)).willReturn(Optional.empty());

    subscriberService.updateSubscription(WebhookData.of(DATA_HANDLER_ID_VALUE, EMAIL,
        SUBSCRIPTION_CREATED));

    assertSubscriptionStatusFromSavedSubscriber(SUBSCRIPTION_CREATED, DATA_HANDLER_ID_VALUE);
  }

  /// requestForget ///

  @Test
  public void givenKnownEmail_whenRequestForget_thenSubscriptionsUpdated() {
    given(subscriberRepository.findByEmailHash(EMAIL_HASH)).willReturn(Optional.of(subscriber));
    given(subscriptionRepository.findBySubscriberId(SUBSCRIBER_ID_VALUE))
        .willReturn(subscriber.getSubscriptions());

    subscriberService.requestForget(EMAIL);

    then(subscriberForgetRequestOutboundChannel).should().send(any(Message.class));

    assertSubscriptionStatusFromSavedSubscription(FORGET_PENDING, DATA_HANDLER_ID_VALUE);

    assertForgetRequestMessage(
        DATA_HANDLER_ID_VALUE, DATA_HANDLER_NAME_VALUE, SUBSCRIPTION_ID_VALUE, EMAIL
    );
  }

  @Test(expected = EntityNotFoundException.class)
  public void givenUnknownEmail_whenRequestForget_thenEntityNotFoundException() {
    given(subscriberRepository.findByEmailHash(EMAIL_HASH)).willReturn(Optional.empty());

    subscriberService.requestForget(EMAIL);
  }

  /// recordForgetResponse ///

  @Test(expected = EntityNotFoundException.class)
  public void givenUnknownSubscriptionId_whenRecordForgetResponse_thenEntityNotFoundException() {
    given(subscriptionRepository.findById(SUBSCRIBER_ID_VALUE)).willReturn(Optional.empty());

    subscriberService.recordForgetResponse(new ForgetResponse(SUBSCRIBER_ID_VALUE, true));
  }

  @Test
  public void givenKnownSubscriptionId_withACK_whenRecordForgetResponse_thenSubscriptionsUpdated() {
    Subscription subscription = subscriber.getSubscriptions().get(0);
    given(subscriptionRepository.findById(SUBSCRIBER_ID_VALUE))
        .willReturn(Optional.of(subscription));

    subscriberService.recordForgetResponse(new ForgetResponse(SUBSCRIBER_ID_VALUE, true));

    assertSubscriptionStatusFromSavedSubscription(FORGOTTEN, DATA_HANDLER_ID_VALUE);
  }

  @Test
  public void givenKnownSubscriptionId_withNAK_whenRecordForgetResponse_thenSubscriptionsUpdated() {
    Subscription subscription = subscriber.getSubscriptions().get(0);
    given(subscriptionRepository.findById(SUBSCRIBER_ID_VALUE))
        .willReturn(Optional.of(subscription));

    subscriberService.recordForgetResponse(new ForgetResponse(SUBSCRIBER_ID_VALUE, false));

    assertSubscriptionStatusFromSavedSubscription(FORGET_FAILED, DATA_HANDLER_ID_VALUE);
  }

  private void assertForgetRequestMessage(
      UUID expectedDataHandlerId, String expectedDataHandlerName, UUID expectedSubscriptionId,
      String expectedEmail) {

    ArgumentCaptor<Message<ForgetRequest>> messageArgumentCaptor =
        ArgumentCaptor.forClass(Message.class);

    then(subscriberForgetRequestOutboundChannel).should().send(messageArgumentCaptor.capture());

    Message<ForgetRequest> forgetRequestMessage = messageArgumentCaptor.getValue();
    assertEquals(expectedDataHandlerId, forgetRequestMessage.getHeaders().get(DATA_HANDLER_ID));
    assertEquals(expectedDataHandlerName, forgetRequestMessage.getHeaders().get(
        DATA_HANDLER_NAME));
    assertEquals(expectedSubscriptionId, forgetRequestMessage.getPayload().getSubscriptionId());
    assertEquals(expectedEmail, forgetRequestMessage.getPayload().getSubscriberEmail());
  }

  private void assertSubscriptionStatusFromSavedSubscriber(
      SubscriptionStatus expectedStatus, UUID dataHandlerId) {

    ArgumentCaptor<Subscriber> subscriberArgumentCaptor = ArgumentCaptor.forClass(Subscriber.class);
    then(subscriberRepository).should().save(subscriberArgumentCaptor.capture());

    Subscription subscription = subscriber.getSubscriptions()
        .stream()
        .filter(it -> dataHandlerId.equals(it.getDataHandlerId()))
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
