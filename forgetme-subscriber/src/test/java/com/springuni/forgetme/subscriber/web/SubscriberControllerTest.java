package com.springuni.forgetme.subscriber.web;

import static com.springuni.forgetme.subscriber.Mocks.EMAIL;
import static com.springuni.forgetme.subscriber.Mocks.SUBSCRIBER_ID_VALUE;
import static com.springuni.forgetme.subscriber.Mocks.createSubscriber;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.hateoas.MediaTypes.HAL_JSON_VALUE;
import static org.springframework.hateoas.config.EnableHypermediaSupport.HypermediaType.HAL;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.springuni.forgetme.core.model.EntityNotFoundException;
import com.springuni.forgetme.core.web.RestErrorHandler;
import com.springuni.forgetme.subscriber.model.Subscriber;
import com.springuni.forgetme.subscriber.service.SubscriberService;
import com.springuni.forgetme.subscriber.web.SubscriberControllerTest.TestConfig;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.hateoas.config.EnableHypermediaSupport;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = TestConfig.class)
@WebMvcTest(controllers = SubscriberController.class, secure = false)
public class SubscriberControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private SubscriberService subscriberService;

  private Subscriber subscriber;

  @Before
  public void setUp() {
    subscriber = createSubscriber();
    subscriber.setId(SUBSCRIBER_ID_VALUE);
  }

  /// GET /api/subscribers/{email} ///

  @Test
  public void givenKnownEmail_whenGetSubscriber_thenOk() throws Exception {
    given(subscriberService.getSubscriber(EMAIL)).willReturn(subscriber);

    mockMvc.perform(get("/api/subscribers/{email}", EMAIL))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(HAL_JSON_VALUE))
        .andDo(print());
  }

  @Test
  public void givenUnknownEmail_whenGetSubscriber_thenNotFound() throws Exception {
    given(subscriberService.getSubscriber(EMAIL)).willThrow(EntityNotFoundException.class);

    mockMvc.perform(get("/api/subscribers/{email}", EMAIL))
        .andExpect(status().isNotFound())
        .andExpect(content().contentTypeCompatibleWith(HAL_JSON_VALUE))
        .andDo(print());
  }

  /// POST /api/subscribers/{email}/forget ///

  @Test
  public void givenKnownEmail_whenRequestForget_thenAccepted() throws Exception {
    mockMvc.perform(post("/api/subscribers/{email}/forget", EMAIL))
        .andExpect(status().isAccepted())
        .andDo(print());

    then(subscriberService).should().requestForget(EMAIL);
  }

  @Test
  public void givenUnknownEmail_whenRequestForget_thenNotFound() throws Exception {
    willThrow(EntityNotFoundException.class).given(subscriberService).requestForget(EMAIL);

    mockMvc.perform(post("/api/subscribers/{email}/forget", EMAIL))
        .andExpect(status().isNotFound())
        .andDo(print());
  }

  @TestConfiguration
  @EnableHypermediaSupport(type = HAL)
  public static class TestConfig {

    @Bean
    SubscriberAssembler linkResourceAssembler() {
      return new SubscriberAssembler();
    }

    @Bean
    RestErrorHandler restErrorHandler() {
      return new RestErrorHandler();
    }

  }

}
