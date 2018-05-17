package com.springuni.forgetme.datahandler;

import com.springuni.forgetme.core.orm.BaseRepository;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

@RepositoryRestResource(path = "dataHandlers", collectionResourceRel = "dataHandlers")
public interface DataHandlerRepository extends BaseRepository<DataHandler, UUID> {

  @Override
  @RestResource
  Optional<DataHandler> findById(UUID id);

  @Override
  @RestResource
  DataHandler save(DataHandler dataHandler);

}
