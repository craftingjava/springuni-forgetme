package com.springuni.forgetme.ui.subscriber;

import static java.util.stream.Collectors.toList;

import com.springuni.forgetme.core.model.DataHandlerRegistry;
import com.springuni.forgetme.subscriber.model.Subscriber;
import com.springuni.forgetme.subscriber.model.Subscription;
import com.springuni.forgetme.subscriber.service.SubscriberService;
import java.util.Collection;
import java.util.List;
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

  static final String MODEL_NAME = "subscriptions";
  static final String VIEW_NAME = "pages/history";

  private final DataHandlerRegistry dataHandlerRegistry;
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
    subscriberService.findSubscriber(email)
        .map(Subscriber::getSubscriptions)
        .map(subscriptions -> subscriptions.stream()
            .map(this::toHistoryEntries)
            .flatMap(Collection::stream)
            .collect(toList())
        )
        .ifPresent(it -> modelAndView.addObject(MODEL_NAME, it));
  }

  private List<SubscriptionViewModel> toHistoryEntries(Subscription subscription) {
    String dataHandlerName = dataHandlerRegistry.lookup(subscription.getDataHandlerId());
    return subscription.getSubscriptionChanges()
        .stream()
        .map(
            it -> new SubscriptionViewModel(
                dataHandlerName, it.getStatus(), it.getEventTimestamp(), null
            )
        )
        .collect(toList());
  }

}
