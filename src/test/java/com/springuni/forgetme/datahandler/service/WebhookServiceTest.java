package com.springuni.forgetme.datahandler.service;

import static java.util.Collections.EMPTY_MAP;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.springuni.forgetme.core.model.EntityNotFoundException;
import com.springuni.forgetme.datahandler.model.DataHandler;
import com.springuni.forgetme.datahandler.service.DataHandlerRepository;
import com.springuni.forgetme.datahandler.service.WebhookService;
import com.springuni.forgetme.datahandler.service.WebhookServiceImpl;
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

  private static final UUID DATA_HANDLER_ID = UUID.randomUUID();
  private static final UUID DATA_HANDLER_KEY = UUID.randomUUID();

  @Mock
  private DataHandlerRepository dataHandlerRepository;
  @Mock
  private MessageChannel webhookOutboundChannel;

  private WebhookService webhookService;

  @Before
  public void setUp() throws Exception {
    webhookService = new WebhookServiceImpl(dataHandlerRepository, webhookOutboundChannel);
  }

  @Test
  public void givenNonExistentDataHandler_whenSubmitData_thenEntityNotFoundException() {
    given(dataHandlerRepository.findById(DATA_HANDLER_ID)).willReturn(Optional.empty());

    try {
      webhookService.submitData(DATA_HANDLER_ID, DATA_HANDLER_KEY, EMPTY_MAP);
      Assert.fail(EntityNotFoundException.class + " expected.");
    } catch (EntityNotFoundException e) {
      // This is what we expected
    }

    then(webhookOutboundChannel).shouldHaveZeroInteractions();
  }

  @Test
  public void givenInvalidDataHandlerKey_whenSubmitData_thenEntityNotFoundException() {
    DataHandler dataHandler = new DataHandler("test", DATA_HANDLER_KEY);
    given(dataHandlerRepository.findById(DATA_HANDLER_ID)).willReturn(Optional.of(dataHandler));

    try {
      webhookService.submitData(DATA_HANDLER_ID, UUID.randomUUID(), EMPTY_MAP);
      Assert.fail(EntityNotFoundException.class + " expected.");
    } catch (EntityNotFoundException e) {
      // This is what we expected
    }

    then(webhookOutboundChannel).shouldHaveZeroInteractions();
  }

  @Test
  public void givenValidDataHandlerKey_whenSubmitData_thenSent() {
    DataHandler dataHandler = new DataHandler("test", DATA_HANDLER_KEY);
    given(dataHandlerRepository.findById(DATA_HANDLER_ID)).willReturn(Optional.of(dataHandler));

    webhookService.submitData(DATA_HANDLER_ID, DATA_HANDLER_KEY, EMPTY_MAP);

    then(webhookOutboundChannel).should().send(any(Message.class));
  }

}
