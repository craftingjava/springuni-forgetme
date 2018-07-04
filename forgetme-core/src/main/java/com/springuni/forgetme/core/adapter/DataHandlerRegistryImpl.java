package com.springuni.forgetme.core.adapter;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Slf4j
@Component
public class DataHandlerRegistryImpl implements DataHandlerRegistry {

  private final ConcurrentMap<String, DataHandlerRegistration> registrations =
      new ConcurrentHashMap<>();

  @EventListener
  public void register(@NonNull DataHandlerRegistration dataHandlerRegistration) {
    String name = dataHandlerRegistration.getName();
    Assert.hasText(name, "data handler's name cannot be empty");

    DataHandlerRegistration oldDataHandlerRegistration =
        registrations.putIfAbsent(name, dataHandlerRegistration);

    Assert.isNull(
        oldDataHandlerRegistration,
        "data handler " + name + " has already been registered; this is most likely a "
            + "configuration error."
    );

    log.info("Data handler {} has been registered.", name);
  }

  @Override
  public Optional<DataHandlerRegistration> lookup(String name) {
    return Optional.ofNullable(registrations.get(name));
  }

}
