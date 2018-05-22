package com.springuni.forgetme.datahandler.service;

import com.springuni.forgetme.core.orm.BaseRepositoryTest;
import com.springuni.forgetme.datahandler.model.DataHandler;
import com.springuni.forgetme.datahandler.service.DataHandlerRepository;

public class DataHandlerRepositoryTest
    extends BaseRepositoryTest<DataHandler, DataHandlerRepository> {

  @Override
  protected DataHandler createEntity() throws Exception {
    return new DataHandler("test");
  }

}
