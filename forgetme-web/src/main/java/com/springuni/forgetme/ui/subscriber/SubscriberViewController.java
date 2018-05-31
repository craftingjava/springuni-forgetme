package com.springuni.forgetme.ui.subscriber;

import com.springuni.forgetme.core.model.EntityNotFoundException;
import com.springuni.forgetme.subscriber.service.SubscriberService;
import java.security.Principal;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/pages/subscriber")
@RequiredArgsConstructor
public class SubscriberViewController {

  static final String MODEL_NAME = "subscriber";
  static final String VIEW_NAME = "pages/subscriber";

  private final SubscriberService subscriberService;

  @GetMapping
  public ModelAndView loadSubscriber(Authentication authentication) {
    ModelAndView modelAndView = new ModelAndView(VIEW_NAME);

    addSubscriberToModel(getEmail(authentication), modelAndView);

    return modelAndView;
  }

  @PostMapping
  public ModelAndView requestForget(Authentication authentication) {
    ModelAndView modelAndView = new ModelAndView(VIEW_NAME);

    String email = getEmail(authentication);

    try {
      subscriberService.requestForget(email);
    } catch (EntityNotFoundException e) {
      return modelAndView;
    }

    addSubscriberToModel(getEmail(authentication), modelAndView);

    return modelAndView;
  }

  private void addSubscriberToModel(String email, ModelAndView modelAndView) {
    subscriberService.findSubscriber(email)
        .ifPresent(it -> modelAndView.addObject(MODEL_NAME, it));
  }

  private String getEmail(Authentication authentication) {
    return Optional.ofNullable(authentication)
        .map(Principal::getName)
        .orElse("");
  }

}
