package com.springuni.forgetme.datahandler;

import static org.junit.Assert.*;

import com.springuni.forgetme.core.orm.BaseRepositoryTest;
import java.util.UUID;

public class DataHandlerRepositoryTest
    extends BaseRepositoryTest<DataHandler, DataHandlerRepository> {

  @Override
  protected DataHandler createEntity() throws Exception {
    return new DataHandler("mailerlite");
  }

}
