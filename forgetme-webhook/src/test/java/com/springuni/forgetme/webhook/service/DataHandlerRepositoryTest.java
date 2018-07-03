package com.springuni.forgetme.webhook.service;

import com.springuni.forgetme.core.orm.BaseRepositoryTest;
import com.springuni.forgetme.webhook.model.DataHandler;
import com.springuni.forgetme.webhook.service.DataHandlerRepository;

public class DataHandlerRepositoryTest
    extends BaseRepositoryTest<DataHandler, DataHandlerRepository> {

  @Override
  protected DataHandler createEntity() throws Exception {
    return new DataHandler("test");
  }

}
