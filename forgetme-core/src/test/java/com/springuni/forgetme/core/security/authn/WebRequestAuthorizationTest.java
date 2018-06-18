package com.springuni.forgetme.core.security.authn;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.springuni.forgetme.core.security.authn.WebRequestAuthorizationTest.TestController;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@WebMvcTest(controllers = TestController.class)
public class WebRequestAuthorizationTest extends AbstractWebSecurityTest {

  private static final String ROOT_PATH = "/";
  private static final String TEST_API_PATH = "/api/links/2";
  private static final String TEST_PAGE_PATH = "/pages/subscriber";

  @Before
  public void setUp() throws Exception {
  }

  @Test
  public void givenRootAccessed_whenUnauthenticated_thenOk() throws Exception {
    mockMvc.perform(get(ROOT_PATH))
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
  public void givenDashboardAccessed_whenUnauthenticated_thenRedirected() throws Exception {
    mockMvc.perform(get("/pages/dashboard"))
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
  @WithMockUser
  public void givenApiAccessed_whenAuthenticated_thenOk() throws Exception {
    mockMvc.perform(get(TEST_API_PATH))
        .andExpect(status().isOk())
        .andDo(print());
  }

  @Test
  @WithMockUser
  public void givenDashboardAccessed_whenAuthenticated_thenOk() throws Exception {
    mockMvc.perform(get(TEST_PAGE_PATH))
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

    @GetMapping(TEST_PAGE_PATH)
    public String dashboard() {
      return "pages/dashboard";
    }

  }

}
