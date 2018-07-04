package com.springuni.forgetme.ui.signin;

import static com.springuni.forgetme.ui.signin.SignInController.LOGIN_VIEW;
import static org.springframework.security.oauth2.core.OAuth2ErrorCodes.ACCESS_DENIED;
import static org.springframework.security.web.WebAttributes.AUTHENTICATION_EXCEPTION;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import com.springuni.forgetme.ui.signin.SignInControllerTest.TestConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = TestConfig.class)
@WebMvcTest(controllers = SignInController.class, secure = false)
public class SignInControllerTest {

  private static final AuthenticationException OAUTH2_AUTHENTICATION_EXCEPTION =
      new OAuth2AuthenticationException(new OAuth2Error(ACCESS_DENIED), "Access Denied");

  @Autowired
  private MockMvc mockMvc;

  @Test
  public void givenLoginPageAccessed_thenLoginViewRendered() throws Exception {
    mockMvc.perform(get("/login/oauth2"))
        .andExpect(status().isOk())
        .andExpect(view().name(LOGIN_VIEW))
        .andDo(print());
  }

  @Test
  public void givenLoginError_thenErrorMessageRendered() throws Exception {
    mockMvc.perform(get("/login/oauth2")
        .sessionAttr(AUTHENTICATION_EXCEPTION, OAUTH2_AUTHENTICATION_EXCEPTION))
        .andExpect(model().attribute("errorMsg", "Access Denied"))
        .andExpect(status().isOk())
        .andExpect(view().name(LOGIN_VIEW))
        .andDo(print());
  }

  @TestConfiguration
  static class TestConfig {

  }

}
