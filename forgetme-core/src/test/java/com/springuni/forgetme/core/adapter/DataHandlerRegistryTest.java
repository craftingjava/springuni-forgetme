package com.springuni.forgetme.core.adapter;

import static com.springuni.forgetme.core.adapter.DataHandlerRegistration.DATA_HANDLER_REGISTRATION_PREFIX;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.env.MockEnvironment;

public class DataHandlerRegistryTest {

  private static final String DATA_HANDLER_NAME = "test";

  private DataHandlerRegistryImpl dataHandlerRegistry;

  @Before
  public void setUp() {
    MockEnvironment environment = new MockEnvironment()
        .withProperty(
            DATA_HANDLER_REGISTRATION_PREFIX + "." + DATA_HANDLER_NAME + ".name",
            DATA_HANDLER_NAME
        );

    dataHandlerRegistry = new DataHandlerRegistryImpl(environment);
    dataHandlerRegistry.afterPropertiesSet();
  }

  @Test(expected = IllegalArgumentException.class)
  public void givenNonExistentDataHandler_whenActivate_thenError() {
    DataHandlerRegistration dataHandlerRegistration = new DataHandlerRegistration("bad");
    dataHandlerRegistry.activate(dataHandlerRegistration);
  }

  @Test
  public void givenExistentDataHandler_whenActivate_thenActivated() {
    DataHandlerRegistration dataHandlerRegistration =
        new DataHandlerRegistration(DATA_HANDLER_NAME);

    dataHandlerRegistry.activate(dataHandlerRegistration);

    assertTrue(dataHandlerRegistry.lookup(DATA_HANDLER_NAME, true).isPresent());
  }

  @Test
  public void givenNonExistentDataHandler_whenLookup_thenEmpty() {
    assertFalse(dataHandlerRegistry.lookup("bad").isPresent());
  }

  @Test
  public void givenExistentDataHandler_whenLookup_thenReturned() {
    assertTrue(dataHandlerRegistry.lookup(DATA_HANDLER_NAME).isPresent());
  }

  @Test
  public void givenInactiveDataHandler_whenLookup_thenEmpty() {
    assertFalse(dataHandlerRegistry.lookup(DATA_HANDLER_NAME, true).isPresent());
  }

  @Test
  public void givenInactiveDataHandler_whenLookupInactive_thenEmpty() {
    assertTrue(dataHandlerRegistry.lookup(DATA_HANDLER_NAME, false).isPresent());
  }

}
