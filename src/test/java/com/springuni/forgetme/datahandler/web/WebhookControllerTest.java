package com.springuni.forgetme.datahandler.web;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.springuni.forgetme.core.model.EntityNotFoundException;
import com.springuni.forgetme.datahandler.web.WebhookControllerTest.TestConfig;
import com.springuni.forgetme.datahandler.service.WebhookService;
import java.util.UUID;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.FileCopyUtils;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = TestConfig.class)
@WebMvcTest(controllers = WebhookController.class, secure = false)
public class WebhookControllerTest {

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

  @After
  public void tearDown() throws Exception {
  }

  @Test
  public void givenNonExistentDataHandler_whenSubmitData_thenNotFound() throws Exception {
    willThrow(EntityNotFoundException.class)
        .given(webhookService).submitData(any(UUID.class), any(UUID.class), anyMap());

    mockMvc
        .perform(
            post(
                "/webhook/{dataHandlerId}/{dataHandlerKey}",
                UUID.randomUUID(),
                UUID.randomUUID()
            ).content(jsonData).contentType(APPLICATION_JSON)
        ).andExpect(status().isNotFound()).andDo(print());
  }

  @Test
  public void givenNonJsonData_whenSubmitData_thenBadRequest() throws Exception {
    mockMvc
        .perform(
            post(
                "/webhook/{dataHandlerId}/{dataHandlerKey}",
                UUID.randomUUID(),
                UUID.randomUUID()
            ).content("bad").contentType(APPLICATION_JSON)
        ).andExpect(status().isBadRequest()).andDo(print());
  }

  @Test
  public void givenExistentDataHandlerWithJsonData_whenSubmitData_thenBadAccepted()
      throws Exception {
    mockMvc
        .perform(
            post(
                "/webhook/{dataHandlerId}/{dataHandlerKey}",
                UUID.randomUUID(),
                UUID.randomUUID()
            ).content(jsonData).contentType(APPLICATION_JSON)
        ).andExpect(status().isAccepted()).andDo(print());
  }

  @TestConfiguration
  public static class TestConfig {

  }

}
