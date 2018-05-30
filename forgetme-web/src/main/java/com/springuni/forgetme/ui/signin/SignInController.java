package com.springuni.forgetme.ui.signin;

import static org.springframework.security.web.WebAttributes.AUTHENTICATION_EXCEPTION;

import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class SignInController {

  static final String LOGIN_VIEW = "pages/login";

  @GetMapping("/login/oauth2")
  public ModelAndView loginPage(HttpServletRequest request) {
    ModelAndView modelAndView = new ModelAndView(LOGIN_VIEW);

    HttpSession session = request.getSession(false);
    if (session != null) {
      AuthenticationException authenticationException =
          (AuthenticationException) session.getAttribute(AUTHENTICATION_EXCEPTION);

      Optional.ofNullable(authenticationException)
          .map(Exception::getMessage)
          .ifPresent(it -> modelAndView.addObject("errorMsg", it));

      session.invalidate();
    }

    return modelAndView;
  }

}
