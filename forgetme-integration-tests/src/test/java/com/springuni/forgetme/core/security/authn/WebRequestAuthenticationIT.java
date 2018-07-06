package com.springuni.forgetme.core.security.authn;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.springuni.forgetme.core.security.authn.WebRequestAuthenticationIT.TestConfig;
import com.springuni.forgetme.core.security.authn.WebRequestAuthenticationIT.TestController;
import com.springuni.forgetme.subscriber.web.ApiSecurityConfig;
import com.springuni.forgetme.ui.config.UISecurityConfig;
import com.springuni.forgetme.webhook.web.WebhookSecurityConfig;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = TestController.class)
@ContextConfiguration(classes = TestConfig.class)
@TestPropertySource(properties = {
    "API_ACCESS_KEY = test",
    "API_SECRET_KEY = secret",
    "WEBHOOK_ACCESS_KEY = test",
    "WEBHOOK_SECRET_KEY = secret"
})
public class WebRequestAuthenticationIT {

  private static final String TEST_ACCESS_KEY = "test";
  private static final String TEST_SECRET_KEY = "secret";

  private static final String ROOT_PATH = "/";
  private static final String TEST_API_PATH = "/api/links/2";
  private static final String TEST_WEBHOOK_PATH = "/webhook/test";
  private static final String TEST_PAGE_PATH = "/pages/subscriber";

  @Autowired
  protected MockMvc mockMvc;

  @MockBean
  protected ClientRegistrationRepository clientRegistrationRepository;

  @Before
  public void setUp() throws Exception {
  }

  @Test
  public void givenApiAccessed_whenAuthenticated_thenOk() throws Exception {
    mockMvc.perform(get(TEST_API_PATH).with(httpBasic(TEST_ACCESS_KEY, TEST_SECRET_KEY)))
        .andExpect(status().isOk())
        .andDo(print());
  }

  @Test
  public void givenApiAccessed_whenUnauthenticated_thenUnauthorized() throws Exception {
    mockMvc.perform(get(TEST_API_PATH))
        .andExpect(status().isUnauthorized())
        .andDo(print());
  }

  @Test
  public void givenWebhookAccessed_whenAuthenticated_thenOk() throws Exception {
    mockMvc.perform(get(TEST_WEBHOOK_PATH).with(httpBasic(TEST_ACCESS_KEY, TEST_SECRET_KEY)))
        .andExpect(status().isOk())
        .andDo(print());
  }

  @Test
  public void givenWebhookAccessed_whenUnauthenticated_thenUnauthorized() throws Exception {
    mockMvc.perform(get(TEST_WEBHOOK_PATH))
        .andExpect(status().isUnauthorized())
        .andDo(print());
  }

  @Test
  @WithMockUser
  public void givenDashboardAccessed_whenAuthenticated_thenOk() throws Exception {
    mockMvc.perform(get(TEST_PAGE_PATH))
        .andExpect(status().isOk())
        .andDo(print());
  }

  @Test
  public void givenDashboardAccessed_whenUnauthenticated_thenRedirected() throws Exception {
    mockMvc.perform(get(TEST_PAGE_PATH))
        .andExpect(status().isFound())
        .andDo(print());
  }

  @Test
  @WithMockUser
  public void givenRootAccessed_whenAuthenticated_thenOk() throws Exception {
    mockMvc.perform(get(ROOT_PATH))
        .andExpect(status().isOk())
        .andDo(print());
  }

  @Test
  public void givenRootAccessed_whenUnauthenticated_thenOk() throws Exception {
    mockMvc.perform(get(ROOT_PATH))
        .andExpect(status().isOk())
        .andDo(print());
  }

  @RestController
  public static class TestController {

    @GetMapping(ROOT_PATH)
    public HttpEntity getRoot() {
      return ResponseEntity.ok().build();
    }

    @GetMapping(TEST_API_PATH)
    public HttpEntity getApi() {
      return ResponseEntity.ok().build();
    }

    @GetMapping(TEST_WEBHOOK_PATH)
    public HttpEntity getWebhook() {
      return ResponseEntity.ok().build();
    }

    @GetMapping(TEST_PAGE_PATH)
    public String dashboard() {
      return "pages/dashboard";
    }

  }

  @TestConfiguration
  @Import({ApiSecurityConfig.class, WebhookSecurityConfig.class, UISecurityConfig.class})
  public static class TestConfig {

  }

}
