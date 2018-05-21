package com.springuni.forgetme.subscriber;

import static com.springuni.forgetme.Mocks.DATA_HANDLER_NAME;
import static com.springuni.forgetme.Mocks.EMAIL;
import static com.springuni.forgetme.Mocks.EMAIL_HASH;
import static com.springuni.forgetme.Mocks.createDataHandler;
import static com.springuni.forgetme.Mocks.createSubscriber;
import static com.springuni.forgetme.Mocks.createWebhookData;
import static com.springuni.forgetme.subscriber.SubscriberStatus.UNSUBSCRIBED;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.springuni.forgetme.core.model.EntityNotFoundException;
import com.springuni.forgetme.core.model.WebhookData;
import com.springuni.forgetme.datahandler.DataHandler;
import com.springuni.forgetme.datahandler.DataHandlerRepository;
import java.util.Optional;
import org.junit.After;
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
  private DataHandlerRepository dataHandlerRepository;

  @Mock
  private SubscriberRepository subscriberRepository;

  @InjectMocks
  private SubscriberServiceImpl subscriberService;

  private Subscriber subscriber;
  private DataHandler dataHandler;

  @Before
  public void setUp() {
    subscriber = createSubscriber();
    dataHandler = createDataHandler();
  }

  @After
  public void tearDown() throws Exception {
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
    given(dataHandlerRepository.findByName(DATA_HANDLER_NAME)).willReturn(Optional.of(dataHandler));
    given(subscriberRepository.findByEmailHash(EMAIL_HASH)).willReturn(Optional.of(subscriber));

    subscriberService.updateSubscription(new WebhookData(DATA_HANDLER_NAME, EMAIL, UNSUBSCRIBED));

    ArgumentCaptor<Subscriber> subscriberArgumentCaptor = ArgumentCaptor.forClass(Subscriber.class);
    then(subscriberRepository).should().save(subscriberArgumentCaptor.capture());

    Subscription subscription = subscriber.getSubscriptions()
        .stream()
        .filter(it -> dataHandler.equals(it.getDataHandler()))
        .findFirst()
        .get();

    assertEquals(UNSUBSCRIBED, subscription.getStatus());
  }

  /*
  @Test
  public void givenUnknownEmail_whenUpdateSubscriber_thenNewSubscriberSaved() {
    given(subscriberRepository.findByEmailHash(EMAIL_HASH)).willReturn(Optional.empty());

    subscriberService.updateSubscription(subscriber);

    ArgumentCaptor<Subscriber> subscriberArgumentCaptor = ArgumentCaptor.forClass(Subscriber.class);
    then(subscriberRepository).should().save(subscriberArgumentCaptor.capture());
    assertSame(subscriber, subscriberArgumentCaptor.getValue());
  }
  */

}
