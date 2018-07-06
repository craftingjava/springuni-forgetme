package com.springuni.forgetme.datahandler.adapter;

import static com.springuni.forgetme.core.adapter.DataHandlerRegistration.DATA_HANDLER_REGISTRATION_PREFIX;
import static com.springuni.forgetme.core.adapter.DataHandlerRegistration.DataScope.NOTIFICATION;
import static com.springuni.forgetme.core.adapter.DataHandlerRegistration.DataScope.PROFILE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeTrue;

import com.springuni.forgetme.core.adapter.DataHandlerRegistration;
import com.springuni.forgetme.datahandler.adapter.DataHandlerRegistrationTest.TestConfig;
import java.net.URI;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.bind.BindResult;
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

  private BindResult<DataHandlerRegistration> dataHandlerRegistrationBindResult;

  @Before
  public void setUp() {
    dataHandlerRegistrationBindResult = Binder.get(environment)
        .bind(
            DATA_HANDLER_REGISTRATION_PREFIX + "." + DATA_HANDLER_NAME,
            DataHandlerRegistration.class
        );
  }

  @Test
  public void shouldContainRegistration() {
    assertTrue(dataHandlerRegistrationBindResult.isBound());
  }

  @Test
  public void registrationDataShouldMatch() {
    assumeTrue(dataHandlerRegistrationBindResult.isBound());

    DataHandlerRegistration dataHandlerRegistration = dataHandlerRegistrationBindResult.get();

    assertEquals(DATA_HANDLER_NAME, dataHandlerRegistration.getName());
    assertEquals("MailerLite", dataHandlerRegistration.getDisplayName());
    assertEquals("Email Marketing", dataHandlerRegistration.getDescription());
    assertEquals(URI.create("https://www.mailerlite.com/"), dataHandlerRegistration.getUrl().get());
    assertThat(dataHandlerRegistration.getDataScopes(), Matchers.hasItems(NOTIFICATION, PROFILE));
  }

  @Test
  public void validationShouldPass() {
    assumeTrue(dataHandlerRegistrationBindResult.isBound());
    DataHandlerRegistration dataHandlerRegistration = dataHandlerRegistrationBindResult.get();
    dataHandlerRegistration.validate();
  }

  @Configuration
  static class TestConfig {

  }

}
