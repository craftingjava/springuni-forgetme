package com.springuni.forgetme.subscriber;

import static com.springuni.forgetme.Mocks.EMAIL;
import static com.springuni.forgetme.Mocks.EMAIL_HASH;
import static com.springuni.forgetme.Mocks.createSubscriber;
import static com.springuni.forgetme.subscriber.SubscriberStatus.UNSUBSCRIBED;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.springuni.forgetme.core.model.EntityNotFoundException;
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
  private SubscriberRepository subscriberRepository;

  @InjectMocks
  private SubscriberServiceImpl subscriberService;

  private Subscriber subscriber;

  @Before
  public void setUp() {
    subscriber = createSubscriber();
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

  // TODO: Fix this once message types have been introduced
  /*
  @Test
  public void givenKnownEmail_whenUpdateSubscriber_thenSubscriberUpdated() {
    given(subscriberRepository.findByEmailHash(EMAIL_HASH)).willReturn(Optional.of(subscriber));
    subscriber.setStatus(UNSUBSCRIBED);

    subscriberService.updateSubscriber(subscriber);

    ArgumentCaptor<Subscriber> subscriberArgumentCaptor = ArgumentCaptor.forClass(Subscriber.class);
    then(subscriberRepository).should().save(subscriberArgumentCaptor.capture());
    assertEquals(UNSUBSCRIBED, subscriberArgumentCaptor.getValue().getStatus());
  }
  */

  @Test
  public void givenUnknownEmail_whenUpdateSubscriber_thenNewSubscriberSaved() {
    given(subscriberRepository.findByEmailHash(EMAIL_HASH)).willReturn(Optional.empty());

    subscriberService.updateSubscriber(subscriber);

    ArgumentCaptor<Subscriber> subscriberArgumentCaptor = ArgumentCaptor.forClass(Subscriber.class);
    then(subscriberRepository).should().save(subscriberArgumentCaptor.capture());
    assertSame(subscriber, subscriberArgumentCaptor.getValue());
  }

}
