package com.springuni.forgetme.ui.subscriber;

import com.springuni.forgetme.subscriber.service.SubscriberService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/pages/history")
@RequiredArgsConstructor
public class HistoryViewController extends AbstractViewController {

  static final String MODEL_NAME = "subscriber";
  static final String VIEW_NAME = "pages/history";

  private final SubscriberService subscriberService;

  @Override
  @GetMapping
  public ModelAndView loadSubscriber(Authentication authentication) {
    return super.loadSubscriber(authentication);
  }

  protected String getViewName() {
    return VIEW_NAME;
  }

  @Override
  protected void populateModel(String email, ModelAndView modelAndView) {

  }

}
