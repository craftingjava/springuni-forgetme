package com.springuni.forgetme.ui.subscriber;

import static java.util.stream.Collectors.toList;

import com.springuni.forgetme.core.model.DataHandlerRegistry;
import com.springuni.forgetme.core.model.EntityNotFoundException;
import com.springuni.forgetme.subscriber.model.Subscription;
import com.springuni.forgetme.subscriber.service.SubscriberService;
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
public class SubscriberViewController extends AbstractViewController {

  static final String SUBSCRIBER_MODEL_NAME = "subscriber";
  static final String SUBSCRIPTIONS_MODEL_NAME = "subscriptions";
  static final String VIEW_NAME = "pages/subscriber";

  private final DataHandlerRegistry dataHandlerRegistry;
  private final SubscriberService subscriberService;

  @Override
  @GetMapping
  public ModelAndView loadSubscriber(Authentication authentication) {
    return super.loadSubscriber(authentication);
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

    populateModel(getEmail(authentication), modelAndView);

    return modelAndView;
  }

  @Override
  protected String getViewName() {
    return VIEW_NAME;
  }

  @Override
  protected void populateModel(String email, ModelAndView modelAndView) {
    subscriberService.findSubscriber(email)
        .ifPresent(subscriber -> {
          modelAndView.addObject(
              SUBSCRIBER_MODEL_NAME,
              new SubscriberViewModel(
                  subscriber.getEmailHash(),
                  subscriber.getCreatedDate(),
                  subscriber.getLastModifiedDate()
              )
          );

          modelAndView.addObject(
              SUBSCRIPTIONS_MODEL_NAME,
              subscriber.getSubscriptions()
                  .stream()
                  .map(this::toSubscriptionViewModel)
                  .collect(toList())
          );
        });
  }

  private SubscriptionViewModel toSubscriptionViewModel(Subscription subscription) {
    String dataHandlerName = dataHandlerRegistry.lookup(subscription.getDataHandlerId());
    return new SubscriptionViewModel(
        dataHandlerName,
        subscription.getStatus(),
        subscription.getCreatedDate(),
        subscription.getLastModifiedDate()
    );
  }

}
