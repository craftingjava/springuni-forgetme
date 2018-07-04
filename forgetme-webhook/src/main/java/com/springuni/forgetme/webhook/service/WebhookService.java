package com.springuni.forgetme.webhook.service;

import java.util.Map;
import java.util.UUID;

public interface WebhookService {

  void submitData(String dataHandlerName, UUID key, Map<String, Object> data);

}
