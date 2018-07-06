package com.springuni.forgetme.core.adapter;

import static com.springuni.forgetme.core.adapter.DataHandlerRegistration.DATA_HANDLER_REGISTRATION_PREFIX;
import static java.util.Collections.emptyMap;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataHandlerRegistryImpl implements DataHandlerRegistry, InitializingBean {

  static final Bindable<Map<String, DataHandlerRegistration>> DATA_HANDLER_REGISTRATION_BINDABLE =
      Bindable.mapOf(String.class, DataHandlerRegistration.class);

  private final Environment environment;

  private final Set<String> activeRegistrations = new ConcurrentSkipListSet<>();

  private Map<String, DataHandlerRegistration> registrations;

  @Override
  public void afterPropertiesSet() {
    registrations = Binder.get(environment)
        .bind(DATA_HANDLER_REGISTRATION_PREFIX, DATA_HANDLER_REGISTRATION_BINDABLE)
        .orElse(emptyMap());
  }

  @EventListener
  public void activate(@NonNull DataHandlerRegistration dataHandlerRegistration) {
    String name = dataHandlerRegistration.getName();
    Assert.hasText(name, "data handler's name cannot be empty");
    Assert.isTrue(
        registrations.containsKey(name),
        "data handler " + name + " was not found; this is most likely a bug"
    );

    boolean added = activeRegistrations.add(name);

    Assert.isTrue(
        added,
        "data handler " + name + " has already been activated; this is most likely a bug"
    );

    log.info("Data handler {} has been activated.", name);
  }

  @Override
  public Optional<DataHandlerRegistration> lookup(String name) {
    return lookup(name, false);
  }

  @Override
  public Optional<DataHandlerRegistration> lookup(String name, boolean active) {
    return Optional.ofNullable(registrations.get(name))
        .filter(it -> !active || activeRegistrations.contains(name));
  }

}
