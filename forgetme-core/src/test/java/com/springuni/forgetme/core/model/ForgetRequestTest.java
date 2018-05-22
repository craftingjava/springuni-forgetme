package com.springuni.forgetme.core.model;

import static org.junit.Assert.assertEquals;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.UUID;
import org.junit.Test;

public class ForgetRequestTest {

  @Test
  public void shouldDeserializeFromJson() throws IOException {
    ForgetRequest forgetRequest = new ObjectMapper().readValue(
        "{\"subscriptionId\":\"8478d457-5186-40e9-95c2-7a490f309aab\","
            + "\"subscriberEmail\": \"test@springuni.com\"}", ForgetRequest.class);

    assertEquals(
        UUID.fromString("8478d457-5186-40e9-95c2-7a490f309aab"),
        forgetRequest.getSubscriptionId()
    );

    assertEquals("test@springuni.com", forgetRequest.getSubscriberEmail());
  }

}
