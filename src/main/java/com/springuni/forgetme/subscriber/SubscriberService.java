package com.springuni.forgetme.subscriber;

public interface SubscriberService {

  Subscriber getSubscriber(String email);

  void updateSubscriber(Subscriber subscriber);

}
