package com.springuni.forgetme.core.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.UUID;
import org.junit.Test;

public class ForgetResponseTest {

  @Test
  public void shouldDeserializeFromJson() throws IOException {
    ForgetResponse forgetResponse = new ObjectMapper().readValue(
        "{\"subscriptionId\":\"8478d457-5186-40e9-95c2-7a490f309aab\","
            + "\"acknowledged\": true}", ForgetResponse.class);

    assertEquals(
        UUID.fromString("8478d457-5186-40e9-95c2-7a490f309aab"),
        forgetResponse.getSubscriptionId()
    );

    assertTrue(forgetResponse.isAcknowledged());
  }

}
