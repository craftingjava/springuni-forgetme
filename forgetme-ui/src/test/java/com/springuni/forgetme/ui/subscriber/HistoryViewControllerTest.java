package com.springuni.forgetme.ui.subscriber;

import static com.springuni.forgetme.subscriber.Mocks.DATA_HANDLER_NAME_VALUE;
import static com.springuni.forgetme.subscriber.Mocks.EMAIL;
import static com.springuni.forgetme.subscriber.Mocks.createSubscription;
import static com.springuni.forgetme.ui.subscriber.HistoryViewController.MODEL_NAME;
import static com.springuni.forgetme.ui.subscriber.HistoryViewController.VIEW_NAME;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import com.springuni.forgetme.core.adapter.DataHandlerRegistration;
import com.springuni.forgetme.core.adapter.DataHandlerRegistry;
import com.springuni.forgetme.subscriber.model.Subscriber;
import com.springuni.forgetme.subscriber.model.Subscription;
import com.springuni.forgetme.subscriber.service.SubscriberService;
import java.util.Optional;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = HistoryViewController.class)
public class HistoryViewControllerTest {

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

    when(dataHandlerRegistry.lookup(DATA_HANDLER_NAME_VALUE))
        .thenReturn(Optional.of(new DataHandlerRegistration(DATA_HANDLER_NAME_VALUE)));
  }

  @Test
  @WithMockUser(EMAIL)
  public void givenExistingSubscriber_whenLoadSubscriber_thenModelContainsIt() throws Exception {
    given(subscriberService.findSubscriber(EMAIL)).willReturn(Optional.of(subscriber));

    mockMvc.perform(get("/pages/history"))
        .andExpect(status().isOk())
        .andExpect(view().name(VIEW_NAME))
        .andExpect(model().attributeExists(MODEL_NAME))
        .andDo(print());
  }

  @Test
  @WithMockUser(EMAIL)
  public void givenNonExistingSubscriber_whenLoadSubscriber_thenModelDoesNotContainIt()
      throws Exception {

    given(subscriberService.findSubscriber(EMAIL)).willReturn(Optional.empty());

    mockMvc.perform(get("/pages/history"))
        .andExpect(status().isOk())
        .andExpect(view().name(VIEW_NAME))
        .andExpect(model().attributeDoesNotExist(MODEL_NAME))
        .andDo(print());
  }

  @Test
  public void givenUnauthenticated_whenLoadSubscriber_thenUnauthorized()
      throws Exception {

    mockMvc.perform(get("/pages/history"))
        .andExpect(status().isUnauthorized())
        .andDo(print());
  }

}
