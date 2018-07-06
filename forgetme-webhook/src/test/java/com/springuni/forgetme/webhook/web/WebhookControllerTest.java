package com.springuni.forgetme.webhook.web;

import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.springuni.forgetme.core.model.EntityNotFoundException;
import com.springuni.forgetme.core.web.RestErrorHandler;
import com.springuni.forgetme.webhook.service.WebhookService;
import com.springuni.forgetme.webhook.web.WebhookControllerTest.TestConfig;
import java.util.UUID;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.FileCopyUtils;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = TestConfig.class)
@WebMvcTest(controllers = WebhookController.class, secure = false)
@TestPropertySource(properties = {
    "WEBHOOK_ACCESS_KEY = test",
    "WEBHOOK_SECRET_KEY = secret"
})
public class WebhookControllerTest {

  private static final String DATA_HANDLER_NAME = "mailerlite";

  private static final String TEST_ACCESS_KEY = "test";
  private static final String TEST_SECRET_KEY = "secret";

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private WebhookService webhookService;

  private byte[] jsonData;

  @Before
  public void setUp() throws Exception {
    jsonData = FileCopyUtils.copyToByteArray(
        getClass().getClassLoader().getResourceAsStream("test_webhook_data.json")
    );
  }

  @Test
  public void givenUnregisteredDataHandler_whenSubmitData_thenNotFound() throws Exception {
    willThrow(EntityNotFoundException.class)
        .given(webhookService).submitData(anyString(), anyMap());

    mockMvc
        .perform(
            post("/webhook/{dataHandlerName}", DATA_HANDLER_NAME)
                .with(httpBasic(TEST_ACCESS_KEY, TEST_SECRET_KEY))
                .content(jsonData).contentType(APPLICATION_JSON)
        ).andExpect(status().isNotFound()).andDo(print());
  }

  @Test
  public void givenUnauthenticatedRequest_whenSubmitData_thenUnauthorized() throws Exception {
    willThrow(EntityNotFoundException.class)
        .given(webhookService).submitData(anyString(), anyMap());

    mockMvc
        .perform(
            post("/webhook/{dataHandlerName}", DATA_HANDLER_NAME)
                .content(jsonData).contentType(APPLICATION_JSON)
        ).andExpect(status().isUnauthorized()).andDo(print());
  }

  @Test
  public void givenNonJsonData_whenSubmitData_thenBadRequest() throws Exception {
    mockMvc
        .perform(
            post("/webhook/{dataHandlerName}", DATA_HANDLER_NAME)
                .with(httpBasic(TEST_ACCESS_KEY, TEST_SECRET_KEY))
                .content("bad").contentType(APPLICATION_JSON)
        ).andExpect(status().isBadRequest()).andDo(print());
  }

  @Test
  public void givenRegisteredDataHandlerWithJsonData_whenSubmitData_thenBadAccepted()
      throws Exception {
    mockMvc
        .perform(
            post(
                "/webhook/{dataHandlerName}", DATA_HANDLER_NAME)
                .with(httpBasic(TEST_ACCESS_KEY, TEST_SECRET_KEY))
                .content(jsonData).contentType(APPLICATION_JSON)
        ).andExpect(status().isAccepted()).andDo(print());
  }

  @TestConfiguration
  @Import(WebhookSecurityConfig.class)
  public static class TestConfig {

    @Bean
    RestErrorHandler restErrorHandler() {
      return new RestErrorHandler();
    }

  }

}
