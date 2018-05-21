package com.springuni.forgetme.core.model;

import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.messaging.support.GenericMessage;

public class ApplicationMessage<T> extends GenericMessage<T> {

  public static final String DATA_HANDLER_NAME = "data_handler_name";
  public static final String TYPE = "type";

  public ApplicationMessage(T payload, MessageType messageType, String dataHandlerName) {
    super(payload, createHeaders(messageType, dataHandlerName));
  }

  public String getDataHandlerName() {
    return (String) getHeaders().get(DATA_HANDLER_NAME);
  }

  public MessageType getMessageType() {
    return (MessageType) getHeaders().get(TYPE);
  }

  private static Map<String, Object> createHeaders(
      MessageType messageType, String dataHandlerName) {

    Map<String, Object> headers = new LinkedHashMap<>();
    headers.put(TYPE, messageType);
    headers.put(DATA_HANDLER_NAME, dataHandlerName);
    return headers;
  }

}
