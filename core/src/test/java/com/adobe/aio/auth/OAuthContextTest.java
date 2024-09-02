package com.adobe.aio.auth;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static com.adobe.aio.auth.OAuthContext.*;

public class OAuthContextTest {

  private static final String TEST_VALUE = "_changeMe";

  @Test
  void missingClientSecret() {
    OAuthContext actual = OAuthContext.builder().build();
    Exception ex = assertThrows(IllegalStateException.class, actual::validate);
    assertEquals("Your `OAuthContext` is missing a clientSecret.", ex.getMessage());
  }

  @Test
  void missingScope() {
    OAuthContext actual = OAuthContext.builder().clientSecret(CLIENT_SECRET + TEST_VALUE).build();
    Exception ex = assertThrows(IllegalStateException.class, actual::validate);
    assertEquals("Your `OAuthContext` is missing a scope.", ex.getMessage());

    // Blank scope
    actual = OAuthContext.builder()
        .clientSecret(CLIENT_SECRET + TEST_VALUE)
        .addScope(" ")
        .build();
    ex = assertThrows(IllegalStateException.class, actual::validate);
    assertEquals("Your `OAuthContext` is missing a scope.", ex.getMessage());

  }

}
