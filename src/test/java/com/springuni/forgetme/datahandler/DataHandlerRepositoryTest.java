package com.springuni.forgetme.datahandler;

import com.springuni.forgetme.core.orm.BaseRepositoryTest;

public class DataHandlerRepositoryTest
    extends BaseRepositoryTest<DataHandler, DataHandlerRepository> {

  @Override
  protected DataHandler createEntity() throws Exception {
    return new DataHandler("test");
  }

}
