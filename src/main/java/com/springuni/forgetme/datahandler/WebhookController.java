package com.springuni.forgetme.datahandler;

import java.util.Map;
import java.util.UUID;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public class WebhookController {

  private final WebhookService webhookService;

  public WebhookController(WebhookService webhookService) {
    this.webhookService = webhookService;
  }

  @PostMapping(path = "/api/datahandlers/{id}/webhook/{key}")
  public HttpEntity webhook(
      @PathVariable UUID id, @PathVariable UUID key, @RequestBody Map<String, Object> data) {

    webhookService.submitData(id, key, data);
    return ResponseEntity.accepted().build();
  }

}
