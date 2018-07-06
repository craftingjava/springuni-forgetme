package com.springuni.forgetme.core.security.authn;

import static lombok.AccessLevel.NONE;

import java.util.Optional;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.codec.Hex;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.keygen.BytesKeyGenerator;
import org.springframework.security.crypto.keygen.KeyGenerators;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringValueResolver;

@Slf4j
@Getter
@Setter
class BasicAuthKeys {

  private static final int DEFAULT_KEY_LENGTH = 16;

  private static final PasswordEncoder DEFAULT_PASSWORD_ENCODER =
      PasswordEncoderFactories.createDelegatingPasswordEncoder();

  private static final String DEFAULT_PASSWORD_PREFIX = "{bcrypt}";

  private static final BytesKeyGenerator DEFAULT_KEY_GENERATOR =
      KeyGenerators.secureRandom(DEFAULT_KEY_LENGTH);

  private final BytesKeyGenerator keyGenerator;
  private final PasswordEncoder passwordEncoder;
  private final String passwordPrefix;

  private String accessKey;
  private String secretKey;

  @Getter(NONE)
  @Setter(NONE)
  private boolean resolved = false;

  BasicAuthKeys() {
    this(DEFAULT_KEY_GENERATOR, DEFAULT_PASSWORD_ENCODER, DEFAULT_PASSWORD_PREFIX);
  }

  BasicAuthKeys(
      BytesKeyGenerator keyGenerator, PasswordEncoder passwordEncoder, String passwordPrefix) {

    this.keyGenerator = keyGenerator;
    this.passwordEncoder = passwordEncoder;
    this.passwordPrefix = passwordPrefix;
  }

  void resolveKeys(@NonNull StringValueResolver valueResolver) {
    if (resolved) {
      return;
    }

    accessKey = Optional.ofNullable(valueResolver.resolveStringValue(accessKey))
        .orElseGet(() -> generateKey("access"));

    String secretKey = Optional.ofNullable(valueResolver.resolveStringValue(this.secretKey))
        .orElseGet(() -> generateKey("secret"));

    this.secretKey = Optional.of(secretKey)
        .filter(it -> it.startsWith(passwordPrefix))
        .orElseGet(() -> passwordEncoder.encode(secretKey));

    resolved = true;
  }

  private String generateKey(String kind) {
    String key = String.valueOf(Hex.encode(keyGenerator.generateKey()));
    log.info("Generated {} key {}.", kind, key);
    return key;
  }

}
