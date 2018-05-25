package com.springuni.forgetme.subscriber.web;

import static org.springframework.hateoas.MediaTypes.HAL_JSON_VALUE;
import static org.springframework.http.ResponseEntity.accepted;
import static org.springframework.http.ResponseEntity.ok;

import com.springuni.forgetme.core.model.ApplicationException;
import com.springuni.forgetme.subscriber.model.Subscriber;
import com.springuni.forgetme.subscriber.service.SubscriberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/subscribers")
@RequiredArgsConstructor
public class SubscriberController {

  private final SubscriberAssembler subscriberAssembler;
  private final SubscriberService subscriberService;

  @GetMapping(path = "/{email}", produces = HAL_JSON_VALUE)
  HttpEntity<SubscriberResource> getSubscriber(@PathVariable String email)
      throws ApplicationException {

    Subscriber subscriber = subscriberService.getSubscriber(email);
    return ok(subscriberAssembler.toResource(subscriber));
  }

  @PostMapping(path = "/{email}/forget")
  HttpEntity<?> requestForget(@PathVariable String email) throws ApplicationException {
    subscriberService.requestForget(email);
    return accepted().build();
  }

}
