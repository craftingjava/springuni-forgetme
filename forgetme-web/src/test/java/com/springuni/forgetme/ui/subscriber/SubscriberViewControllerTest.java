package com.springuni.forgetme.ui.subscriber;

import static com.springuni.forgetme.subscriber.Mocks.EMAIL;
import static com.springuni.forgetme.subscriber.Mocks.createSubscription;
import static com.springuni.forgetme.ui.subscriber.SubscriberViewController.FORGETME_ENABLED_MODEL_NAME;
import static com.springuni.forgetme.ui.subscriber.SubscriberViewController.SUBSCRIBER_MODEL_NAME;
import static com.springuni.forgetme.ui.subscriber.SubscriberViewController.SUBSCRIPTIONS_MODEL_NAME;
import static com.springuni.forgetme.ui.subscriber.SubscriberViewController.VIEW_NAME;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import com.springuni.forgetme.core.model.DataHandlerRegistry;
import com.springuni.forgetme.core.model.EntityNotFoundException;
import com.springuni.forgetme.subscriber.model.Subscriber;
import com.springuni.forgetme.subscriber.model.Subscription;
import com.springuni.forgetme.subscriber.service.SubscriberService;
import java.util.Optional;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = SubscriberViewController.class)
public class SubscriberViewControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private DataHandlerRegistry dataHandlerRegistry;

  @MockBean
  private SubscriberService subscriberService;

  private Subscriber subscriber;

  @Before
  public void setUp() {
    Subscription subscription = createSubscription();
    subscriber = subscription.getSubscriber();
  }

  @Test
  @WithMockUser(EMAIL)
  public void givenExistingSubscriber_whenLoadSubscriber_thenModelContainsIt() throws Exception {
    given(subscriberService.findSubscriber(EMAIL)).willReturn(Optional.of(subscriber));

    mockMvc.perform(get("/pages/subscriber"))
        .andExpect(status().isOk())
        .andExpect(view().name(VIEW_NAME))
        .andExpect(model().attributeExists(SUBSCRIBER_MODEL_NAME))
        .andExpect(model().attributeExists(SUBSCRIPTIONS_MODEL_NAME))
        .andExpect(model().attribute(FORGETME_ENABLED_MODEL_NAME, true))
        .andDo(print());
  }

  @Test
  @WithMockUser(EMAIL)
  public void givenNonExistingSubscriber_whenLoadSubscriber_thenModelDoesNotContainIt()
      throws Exception {

    given(subscriberService.findSubscriber(EMAIL)).willReturn(Optional.empty());

    mockMvc.perform(get("/pages/subscriber"))
        .andExpect(status().isOk())
        .andExpect(view().name(VIEW_NAME))
        .andExpect(model().attributeDoesNotExist(SUBSCRIBER_MODEL_NAME))
        .andExpect(model().attributeDoesNotExist(SUBSCRIPTIONS_MODEL_NAME))
        .andExpect(model().attributeDoesNotExist(FORGETME_ENABLED_MODEL_NAME))
        .andDo(print());
  }

  @Test
  public void givenUnauthenticated_whenLoadSubscriber_thenUnauthorized()
      throws Exception {

    mockMvc.perform(get("/pages/subscriber"))
        .andExpect(status().isUnauthorized())
        .andDo(print());
  }

  @Test
  @WithMockUser(EMAIL)
  public void givenExistingSubscriber_whenRequestForget_thenModelContainsIt() throws Exception {
    given(subscriberService.findSubscriber(EMAIL)).willReturn(Optional.of(subscriber));

    InOrder inOrder = inOrder(subscriberService);

    mockMvc.perform(post("/pages/subscriber").with(csrf()))
        .andExpect(status().isOk())
        .andExpect(view().name(VIEW_NAME))
        .andExpect(model().attributeExists(SUBSCRIBER_MODEL_NAME))
        .andExpect(model().attributeExists(SUBSCRIPTIONS_MODEL_NAME))
        .andExpect(model().attribute(FORGETME_ENABLED_MODEL_NAME, true))
        .andDo(print());

    then(subscriberService).should(inOrder).requestForget(EMAIL);
    then(subscriberService).should(inOrder).findSubscriber(EMAIL);
  }

  @Test
  @WithMockUser(EMAIL)
  public void givenNonExistingSubscriber_whenRequestForget_thenModelDoesNotContainIt()
      throws Exception {

    willThrow(EntityNotFoundException.class).given(subscriberService).requestForget(EMAIL);

    mockMvc.perform(post("/pages/subscriber").with(csrf()))
        .andExpect(status().isOk())
        .andExpect(view().name(VIEW_NAME))
        .andExpect(model().attributeDoesNotExist(SUBSCRIBER_MODEL_NAME))
        .andExpect(model().attributeDoesNotExist(SUBSCRIPTIONS_MODEL_NAME))
        .andExpect(model().attributeDoesNotExist(FORGETME_ENABLED_MODEL_NAME))
        .andDo(print());

    then(subscriberService).should(never()).findSubscriber(EMAIL);
  }

  @Test
  public void givenUnauthenticated_whenRequestForget_thenUnauthorized()
      throws Exception {

    mockMvc.perform(post("/pages/subscriber").with(csrf()))
        .andExpect(status().isUnauthorized())
        .andDo(print());
  }

}
