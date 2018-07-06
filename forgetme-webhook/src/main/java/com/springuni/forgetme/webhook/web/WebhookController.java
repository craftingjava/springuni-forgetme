package com.springuni.forgetme.webhook.web;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import com.springuni.forgetme.webhook.service.WebhookService;
import java.util.Map;
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

  @PostMapping(
      path = "/webhook/{dataHandlerName}",
      consumes = APPLICATION_JSON_VALUE
  )
  public HttpEntity submitData(
      @PathVariable String dataHandlerName, @RequestBody Map<String, Object> jsonData) {

    webhookService.submitData(dataHandlerName, jsonData);

    return ResponseEntity.accepted().build();
  }

}
