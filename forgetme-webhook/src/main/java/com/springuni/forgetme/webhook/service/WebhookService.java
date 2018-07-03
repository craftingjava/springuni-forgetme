package com.springuni.forgetme.webhook.service;

import java.util.Map;
import java.util.UUID;

public interface WebhookService {

  void submitData(UUID dataHandlerId, UUID key, Map<String, Object> data);

}
