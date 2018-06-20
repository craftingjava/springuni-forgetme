package com.springuni.forgetme.datahandler.adapter;

import java.util.Collections;
import java.util.Map;
import org.springframework.boot.autoconfigure.condition.ConditionMessage;
import org.springframework.boot.autoconfigure.condition.ConditionOutcome;
import org.springframework.boot.autoconfigure.condition.SpringBootCondition;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotatedTypeMetadata;

public abstract class AbstractDataHandlerConfiguredCondition extends SpringBootCondition {

  private static final Bindable<Map<String, String>> BINDABLE_HANDLER_PROPERTIES =
      Bindable.mapOf(String.class, String.class);

  private final String dataHandlerName;

  public AbstractDataHandlerConfiguredCondition(String dataHandlerName) {
    this.dataHandlerName = dataHandlerName;
  }

  @Override
  public ConditionOutcome getMatchOutcome(
      ConditionContext context, AnnotatedTypeMetadata metadata) {

    ConditionMessage.Builder message = ConditionMessage
        .forCondition("Data handler configured:", dataHandlerName);

    Map<String, String> dataHandlerProperties = getDataHandlerProperties(context.getEnvironment());
    if (isDataHandlerConfigured(dataHandlerProperties)) {
      return ConditionOutcome.match(message.available(dataHandlerName));
    }

    return ConditionOutcome.noMatch(message.notAvailable(dataHandlerName));
  }

  protected abstract boolean isDataHandlerConfigured(Map<String, String> dataHandlerProperties);

  private Map<String, String> getDataHandlerProperties(Environment environment) {
    return Binder.get(environment)
        .bind("forgetme.data-handler.provider." + dataHandlerName, BINDABLE_HANDLER_PROPERTIES)
        .orElse(Collections.emptyMap());
  }

}
