package com.springuni.forgetme.datahandler.service;

import com.springuni.forgetme.core.orm.BaseRepositoryTest;
import com.springuni.forgetme.datahandler.model.DataHandler;

public class DataHandlerRepositoryTest
    extends BaseRepositoryTest<DataHandler, DataHandlerRepository> {

  @Override
  protected DataHandler createEntity() throws Exception {
    return new DataHandler("test");
  }

}
