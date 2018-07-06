package com.springuni.forgetme.webhook.service;

import java.util.Map;

public interface WebhookService {

  void submitData(String dataHandlerName, Map<String, Object> data);

}
