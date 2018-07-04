package com.springuni.forgetme.webhook.service;

import com.springuni.forgetme.core.adapter.DataHandlerRegistry;
import com.springuni.forgetme.core.orm.BaseRepository;
import com.springuni.forgetme.webhook.model.DataHandler;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

@RepositoryRestResource(path = "dataHandlers", collectionResourceRel = "dataHandlers")
public interface DataHandlerRepository
    extends BaseRepository<DataHandler, UUID>, DataHandlerRegistry {

  @Override
  @RestResource
  Optional<DataHandler> findById(UUID id);

  Optional<DataHandler> findByName(String name);

  @Override
  @RestResource
  DataHandler save(DataHandler dataHandler);

}
