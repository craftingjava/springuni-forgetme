package com.springuni.forgetme.ui.subscriber;

import static com.springuni.forgetme.subscriber.Mocks.createSubscriber;
import static org.junit.Assert.*;

import com.springuni.forgetme.subscriber.model.Subscriber;
import com.springuni.forgetme.subscriber.service.SubscriberService;
import com.springuni.forgetme.ui.subscriber.HistoryViewControllerTest.TestConfig;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = TestConfig.class)
@WebMvcTest(controllers = SubscriberViewController.class)
public class HistoryViewControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private SubscriberService subscriberService;

  private Subscriber subscriber;

  @Before
  public void setUp() {
    subscriber = createSubscriber();
  }

  @TestConfiguration
  static class TestConfig {

  }

}
