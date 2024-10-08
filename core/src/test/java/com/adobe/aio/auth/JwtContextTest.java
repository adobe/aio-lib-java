package com.adobe.aio.auth;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;

import static com.adobe.aio.auth.JwtContext.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class JwtContextTest {
  private static final String TEST_VALUE = "_changeMe";

  private static PrivateKey privateKey;

  @BeforeAll
  public static void beforeClass() throws Exception {
    KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
    kpg.initialize(2048);
    KeyPair kp = kpg.generateKeyPair();
    privateKey = kp.getPrivate();
  }

  @Test
  void missingClientSecret() {
    JwtContext actual = JwtContext.builder().build();
    Exception ex = assertThrows(IllegalStateException.class, actual::validate);
    assertEquals("Your `JwtContext` is missing a clientSecret", ex.getMessage());
  }

  @Test
  void missingTechnicalAccountId() {
    JwtContext actual = JwtContext.builder()
        .clientSecret(CLIENT_SECRET + TEST_VALUE)
        .build();
    Exception ex = assertThrows(IllegalStateException.class, actual::validate);
    assertEquals("Your `JwtContext` is missing a technicalAccountId", ex.getMessage());
  }

  @Test
  void missingMetascopes() {
    JwtContext actual = JwtContext.builder()
        .clientSecret(CLIENT_SECRET + TEST_VALUE)
        .technicalAccountId(TECHNICAL_ACCOUNT_ID + TEST_VALUE)
        .build();
    Exception ex = assertThrows(IllegalStateException.class, actual::validate);
    assertEquals("Your `JwtContext` is missing a metascope", ex.getMessage());
  }

  @Test
  void missingPrivateKey() {
    JwtContext actual = JwtContext.builder()
        .clientSecret(CLIENT_SECRET + TEST_VALUE)
        .technicalAccountId(TECHNICAL_ACCOUNT_ID + TEST_VALUE)
        .addMetascope(META_SCOPES + TEST_VALUE)
        .build();
    Exception ex = assertThrows(IllegalStateException.class, actual::validate);
    assertEquals("Your `JwtContext` is missing a privateKey", ex.getMessage());
  }
}
