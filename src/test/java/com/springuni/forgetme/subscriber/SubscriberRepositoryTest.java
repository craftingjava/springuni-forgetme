package com.springuni.forgetme.subscriber;

import com.springuni.forgetme.core.orm.BaseRepositoryTest;
import java.util.UUID;

public class SubscriberRepositoryTest extends BaseRepositoryTest<Subscriber, SubscriberRepository> {

  private static final UUID ID = UUID.randomUUID();
  private static final UUID NON_EXISTENT_ID = UUID.randomUUID();

  @Override
  protected Subscriber createEntity() throws Exception {
    return new Subscriber("github@laszlocsontos.com");
  }

  @Override
  protected UUID getId() {
    return ID;
  }

  @Override
  protected UUID getNonExistentId() {
    return NON_EXISTENT_ID;
  }

}
