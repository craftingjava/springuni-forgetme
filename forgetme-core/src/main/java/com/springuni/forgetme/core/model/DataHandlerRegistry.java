package com.springuni.forgetme.core.model;

import java.util.UUID;

public interface DataHandlerRegistry {

  UUID register(String name);

  String lookup(UUID id);

}
