package com.springuni.forgetme.webhook.service;

import static java.util.Collections.EMPTY_MAP;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.springuni.forgetme.core.adapter.DataHandlerRegistration;
import com.springuni.forgetme.core.adapter.DataHandlerRegistry;
import com.springuni.forgetme.core.model.EntityNotFoundException;
import java.util.Optional;
import java.util.UUID;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;

@RunWith(MockitoJUnitRunner.class)
public class WebhookServiceTest {

  private static final String DATA_HANDLER_NAME = "mailerlite";
  private static final UUID DATA_HANDLER_KEY = UUID.randomUUID();

  @Mock
  private DataHandlerRegistry dataHandlerRegistry;

  @Mock
  private MessageChannel webhookOutboundChannel;

  private WebhookService webhookService;

  @Before
  public void setUp() throws Exception {
    webhookService = new WebhookServiceImpl(dataHandlerRegistry, webhookOutboundChannel);
  }

  @Test
  public void givenNonExistentDataHandler_whenSubmitData_thenEntityNotFoundException() {
    given(dataHandlerRegistry.lookup(DATA_HANDLER_NAME)).willReturn(Optional.empty());

    try {
      webhookService.submitData(DATA_HANDLER_NAME, DATA_HANDLER_KEY, EMPTY_MAP);
      Assert.fail(EntityNotFoundException.class + " expected.");
    } catch (EntityNotFoundException e) {
      // This is what we expected
    }

    then(webhookOutboundChannel).shouldHaveZeroInteractions();
  }

  @Test
  public void givenInvalidDataHandlerKey_whenSubmitData_thenEntityNotFoundException() {
    given(dataHandlerRegistry.lookup(DATA_HANDLER_NAME)).willReturn(Optional.empty());

    try {
      webhookService.submitData(DATA_HANDLER_NAME, UUID.randomUUID(), EMPTY_MAP);
      Assert.fail(EntityNotFoundException.class + " expected.");
    } catch (EntityNotFoundException e) {
      // This is what we expected
    }

    then(webhookOutboundChannel).shouldHaveZeroInteractions();
  }

  @Test
  public void givenValidDataHandlerKey_whenSubmitData_thenSent() {
    DataHandlerRegistration dataHandlerRegistration =
        new DataHandlerRegistration(DATA_HANDLER_NAME);

    given(dataHandlerRegistry.lookup(DATA_HANDLER_NAME))
        .willReturn(Optional.of(dataHandlerRegistration));

    webhookService.submitData(DATA_HANDLER_NAME, DATA_HANDLER_KEY, EMPTY_MAP);

    then(webhookOutboundChannel).should().send(any(Message.class));
  }

}
