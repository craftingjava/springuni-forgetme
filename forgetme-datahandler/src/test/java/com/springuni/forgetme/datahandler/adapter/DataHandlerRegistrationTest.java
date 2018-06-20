package com.springuni.forgetme.datahandler.adapter;

import static com.springuni.forgetme.datahandler.adapter.DataHandlerRegistration.DATA_HANDLER_REGISTRATION_BINDABLE;
import static com.springuni.forgetme.datahandler.adapter.DataHandlerRegistration.DATA_HANDLER_REGISTRATION_PREFIX;
import static com.springuni.forgetme.datahandler.adapter.DataHandlerRegistration.DataScope.NOTIFICATION;
import static com.springuni.forgetme.datahandler.adapter.DataHandlerRegistration.DataScope.PROFILE;
import static org.hamcrest.Matchers.hasItems;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import com.springuni.forgetme.datahandler.adapter.DataHandlerRegistrationTest.TestConfig;
import java.net.URI;
import java.util.Collections;
import java.util.Map;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@SpringBootTest(classes = TestConfig.class)
public class DataHandlerRegistrationTest {

  private static final String DATA_HANDLER_NAME = "mailerlite";

  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  @Autowired
  private Environment environment;

  DataHandlerRegistration dataHandlerRegistration;

  @Before
  public void setUp() {
    Map<String, DataHandlerRegistration> dataHandlerRegistrationMap = Binder.get(environment)
        .bind(DATA_HANDLER_REGISTRATION_PREFIX, DATA_HANDLER_REGISTRATION_BINDABLE)
        .orElse(Collections.emptyMap());

    dataHandlerRegistration =
        dataHandlerRegistrationMap.get(DATA_HANDLER_NAME);
  }

  @Test
  public void shouldContainRegistration() {
    assertNotNull(dataHandlerRegistration);
  }

  @Test
  public void registrationDataShouldMatch() {
    assertEquals(DATA_HANDLER_NAME, dataHandlerRegistration.getName());
    assertEquals("MailerLite", dataHandlerRegistration.getDisplayName());
    assertEquals("Email Marketing", dataHandlerRegistration.getDescription());
    assertEquals(URI.create("https://www.mailerlite.com/"), dataHandlerRegistration.getUrl().get());
    assertThat(dataHandlerRegistration.getDataScopes(), hasItems(NOTIFICATION, PROFILE));
  }

  @Test
  public void validationShouldPass() {
    dataHandlerRegistration.validate();
  }

  @Configuration
  static class TestConfig {

  }

}
