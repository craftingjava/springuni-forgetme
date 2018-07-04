package com.springuni.forgetme.ui.subscriber;

import java.security.Principal;
import java.util.Optional;
import org.springframework.security.core.Authentication;
import org.springframework.web.servlet.ModelAndView;

abstract class AbstractViewController {

  public ModelAndView loadSubscriber(Authentication authentication) {
    ModelAndView modelAndView = new ModelAndView(getViewName());

    populateModel(getEmail(authentication), modelAndView);

    return modelAndView;
  }

  protected abstract String getViewName();

  protected abstract void populateModel(String email, ModelAndView modelAndView);

  String getEmail(Authentication authentication) {
    return Optional.ofNullable(authentication)
        .map(Principal::getName)
        .orElse("");
  }

}
