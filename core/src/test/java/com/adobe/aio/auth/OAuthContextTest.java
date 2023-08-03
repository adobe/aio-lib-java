package com.adobe.aio.auth;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static com.adobe.aio.auth.OAuthContext.*;

public class OAuthContextTest {

  private static final String TEST_PROPERTIES = "workspace.properties";
  private static final String TEST_VALUE = "_changeMe";

  private static OAuthContext expected;

  @BeforeAll
  public static void beforeClass() throws Exception {
    expected = OAuthContext.builder().propertiesPath(TEST_PROPERTIES).build();
  }

  @Test
  void properties() {
    OAuthContext actual = OAuthContext.builder()
        .clientSecret(CLIENT_SECRET + TEST_VALUE)
        .addScope(SCOPES + TEST_VALUE)
        .build();
    assertEquals(actual, expected);
    assertEquals(actual.hashCode(), expected.hashCode());
    assertEquals(actual.toString(), expected.toString());
    actual.validate();
  }

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
