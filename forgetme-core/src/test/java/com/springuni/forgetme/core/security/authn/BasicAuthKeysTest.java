package com.springuni.forgetme.core.security.authn;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.springframework.security.crypto.codec.Hex;
import org.springframework.security.crypto.keygen.BytesKeyGenerator;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringValueResolver;

public class BasicAuthKeysTest {

  private static final String TEST_ACCESS_KEY = "test";
  private static final String TEST_SECRET_KEY = "secret";

  private static final BytesKeyGenerator TEST_KEY_GENERATOR = new BytesKeyGenerator() {

    @Override
    public int getKeyLength() {
      return generateKey().length;
    }

    @Override
    public byte[] generateKey() {
      return new byte[]{0, 1, 2, 3};
    }

  };

  private static final String TEST_GENERATED_KEY_HEX =
      String.valueOf(Hex.encode(TEST_KEY_GENERATOR.generateKey()));

  private static final String TEST_PASSWORD_PREFIX = "{noop}";

  private static final PasswordEncoder TEST_PASSWORD_ENCODER = new PasswordEncoder() {

    @Override
    public String encode(CharSequence rawPassword) {
      return TEST_PASSWORD_PREFIX + rawPassword;
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
      return false;
    }

  };

  private static final StringValueResolver TEST_VALUE_RESOLVER = (it -> it);

  private BasicAuthKeys basicAuthKeys;

  @Before
  public void setUp() {
    basicAuthKeys = new BasicAuthKeys(
        TEST_KEY_GENERATOR, TEST_PASSWORD_ENCODER, TEST_PASSWORD_PREFIX
    );
  }

  @Test
  public void givenEmptyKeys_whenResolveKeys_thenGenerated() {
    basicAuthKeys.resolveKeys(TEST_VALUE_RESOLVER);

    assertEquals(TEST_GENERATED_KEY_HEX, basicAuthKeys.getAccessKey());
    assertEquals(TEST_PASSWORD_PREFIX + TEST_GENERATED_KEY_HEX, basicAuthKeys.getSecretKey());
  }

  @Test
  public void givenCleartextKeys_whenResolveKeys_thenGenerated() {
    basicAuthKeys.setAccessKey(TEST_ACCESS_KEY);
    basicAuthKeys.setSecretKey(TEST_SECRET_KEY);

    basicAuthKeys.resolveKeys(TEST_VALUE_RESOLVER);

    assertEquals(TEST_ACCESS_KEY, basicAuthKeys.getAccessKey());
    assertEquals(TEST_PASSWORD_PREFIX + TEST_SECRET_KEY, basicAuthKeys.getSecretKey());
  }

  @Test
  public void givenEncryptedKeys_whenResolveKeys_thenGenerated() {
    basicAuthKeys.setAccessKey(TEST_ACCESS_KEY);
    basicAuthKeys.setSecretKey(TEST_PASSWORD_PREFIX + TEST_SECRET_KEY);

    basicAuthKeys.resolveKeys(TEST_VALUE_RESOLVER);

    assertEquals(TEST_ACCESS_KEY, basicAuthKeys.getAccessKey());
    assertEquals(TEST_PASSWORD_PREFIX + TEST_SECRET_KEY, basicAuthKeys.getSecretKey());
  }

}
