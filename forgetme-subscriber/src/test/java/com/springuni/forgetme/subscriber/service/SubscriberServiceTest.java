package com.springuni.forgetme.subscriber.service;

import static com.springuni.forgetme.Mocks.DATA_HANDLER_ID_VALUE;
import static com.springuni.forgetme.Mocks.EMAIL;
import static com.springuni.forgetme.Mocks.EMAIL_HASH;
import static com.springuni.forgetme.Mocks.createSubscriber;
import static com.springuni.forgetme.core.model.SubscriberStatus.SUBSCRIBED;
import static com.springuni.forgetme.core.model.SubscriberStatus.UNSUBSCRIBED;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.springuni.forgetme.core.model.EntityNotFoundException;
import com.springuni.forgetme.core.model.WebhookData;
import com.springuni.forgetme.subscriber.model.Subscriber;
import com.springuni.forgetme.core.model.SubscriberStatus;
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

@RunWith(MockitoJUnitRunner.class)
public class SubscriberServiceTest {

  @Mock
  private SubscriberRepository subscriberRepository;

  @InjectMocks
  private SubscriberServiceImpl subscriberService;

  private Subscriber subscriber;

  @Before
  public void setUp() {
    subscriber = createSubscriber();
    subscriber.updateSubscription(DATA_HANDLER_ID_VALUE, SUBSCRIBED);
  }

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

  @Test
  public void givenKnownEmail_whenUpdateSubscription_thenSubscriptionUpdated() {
    given(subscriberRepository.findByEmailHash(EMAIL_HASH)).willReturn(Optional.of(subscriber));

    subscriberService
        .updateSubscription(WebhookData.of(DATA_HANDLER_ID_VALUE, EMAIL, UNSUBSCRIBED));

    assertSubscriptionStatus(UNSUBSCRIBED, DATA_HANDLER_ID_VALUE);
  }

  @Test
  public void givenUnknownEmail_whenUpdateSubscriber_thenNewSubscriberSaved() {
    given(subscriberRepository.findByEmailHash(EMAIL_HASH)).willReturn(Optional.empty());

    subscriberService.updateSubscription(WebhookData.of(DATA_HANDLER_ID_VALUE, EMAIL, SUBSCRIBED));

    assertSubscriptionStatus(SUBSCRIBED, DATA_HANDLER_ID_VALUE);
  }

  private void assertSubscriptionStatus(SubscriberStatus expectedStatus, UUID dataHandlerId) {
    ArgumentCaptor<Subscriber> subscriberArgumentCaptor = ArgumentCaptor.forClass(Subscriber.class);
    then(subscriberRepository).should().save(subscriberArgumentCaptor.capture());

    Subscription subscription = subscriber.getSubscriptions()
        .stream()
        .filter(it -> dataHandlerId.equals(it.getDataHandlerId()))
        .findFirst()
        .get();

    assertEquals(expectedStatus, subscription.getStatus());
  }

}
