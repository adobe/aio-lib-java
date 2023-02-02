/*
 * Copyright 2017 Adobe. All rights reserved.
 * This file is licensed to you under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License. You may obtain a copy
 * of the License at http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under
 * the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR REPRESENTATIONS
 * OF ANY KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */

package com.adobe.aio.event.webhook.service;

import static com.adobe.aio.event.webhook.service.EventVerifier.ADOBE_IOEVENTS_DIGI_SIGN_1;
import static com.adobe.aio.event.webhook.service.EventVerifier.ADOBE_IOEVENTS_DIGI_SIGN_2;
import static com.adobe.aio.event.webhook.service.EventVerifier.ADOBE_IOEVENTS_PUB_KEY_1_PATH;
import static com.adobe.aio.event.webhook.service.EventVerifier.ADOBE_IOEVENTS_PUB_KEY_2_PATH;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.configureFor;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.tomakehurst.wiremock.WireMockServer;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class EventVerifierTest {

  private static final String TEST_CLIENT_ID = "client_id1";
  private static final String INVALID_TEST_CLIENT_ID = "invalid_client_id";
  private static final String TEST_DIGI_SIGN_1 = "IaHo9/8DYt2630pAtjIJeGtsHjB61zOSiAb3S4X1VdPooxikfk79H/t3rgaSbmQMOnjVPRpYVNsHn1fE+l80gjEqmljgNEHt+BtfEH8EsEigwbjQS9opTx/GFnexw3h/sWOt4MGWt3TFK484Dsijijcs1gLwcxTyVUeU2G2XXECpH4dvvEXWQP+1HDFu9nrN+MU/aOR17cNF5em/D/jKjgTcaPx7jK+W5M57F3qqsmdcPxM1ltQxx1/iAXWaOffOC/nXSda5fLFZL75RKBIoveDjL9zthVkBVY9qKXYyK6S/usc2bW3PpXuRTd5Xv2bFB2Mlzr0Gi6St/iiNYLEl3g==";
  private static final String TEST_DIGI_SIGN_2 = "Xx8uVpZlKIOqAdVBr/6aNrASk6u7i/Bb9kWZttIFOu0Y2JGozZGG7WF9Z6056RdeeBUXLJsV4r8a3ZeEUrOZi3hvhV+Hw7vmK1NIQJVIqdigF9mJ/2gSMGe7K4OPedh+fPNZmbOyNIc6FRmUtTdemNLJeCzM7Zf+niC7Tfsytsz4lW4ebv34TWHxzAA9pZRcJE4a1YYqEYAqn3cHTvCzB/AQ6VdIcP8MsuTGatCk9Vc6dTPOVEcyYkVXTMGgsmzW8RB6mq0m1aqTz3KvnhEYlkspqtxi+jBkTjcYVf1dPa4ofbosmD5rohIef/UwPX5n5ZHM7du86Gf+6S72ee8tbw==";
  private static final String TEST_INVALID_DIGI_SIGN_1 = "abc22OGm8/6H6bJXSi+/4VztsPN+fPZtHgHrrASuTw7LTUZVpbAZNaXVTzQsFd47PvaI8aQxbl874GFmH0QfAVQaRT93x5O/kQdM1ymG03303QaFY/mjm/Iot3VEwq5xOtM8f5a2mKUce9bgEv28iN7z9H/MbBOSmukPSJh/vMLkFAmMZQwdP4SRK3ckxQg6wWTbeMRxjw8/FLckznCGPZri4c0O7WPr8wnrWcvArlhBpIPJPeifJOyDj/woFQzoeemdrVoBFOieE/j3RoMWzcQeLENaSrqk00MPL2svNQcTLMkmWuICOjYSbnlv/EPFCQS8bQsnVHxGFD1yDeFa7Q==";
  private static final String TEST_PUB_KEY1_PATH = "/junit/pub-key-1.pem";
  private static final String TEST_PUB_KEY2_PATH = "/junit/pub-key-2.pem";
  private static final String TEST_INVALID_PUB_KEY_PATH = "/junit/invalid-key.pem";

  static final int ADOBEIO_CDN_PORT = 9000;

  private EventVerifier underTest;

  private WireMockServer wireMockServer;

  @BeforeEach
  public void setup() {
    wireMockServer = new WireMockServer(options().port(ADOBEIO_CDN_PORT));
    wireMockServer.start();
    setupEndpointStub();
    underTest = new EventVerifier("http://localhost:" + ADOBEIO_CDN_PORT);
  }

  @AfterEach
  void tearDown() {
    wireMockServer.stop();
  }

  private void setupEndpointStub() {
    configureFor("localhost", ADOBEIO_CDN_PORT);
    stubFor(get(urlEqualTo(TEST_PUB_KEY1_PATH))
        .willReturn(aResponse().withBody(getPubKey1())));
    stubFor(get(urlEqualTo(TEST_PUB_KEY2_PATH))
        .willReturn(aResponse().withBody(getPubKey2())));
    stubFor(get(urlEqualTo(TEST_INVALID_PUB_KEY_PATH))
        .willReturn(aResponse().withBody(getInvalidPubKey())));
  }
  @Test
  public void testVerifyValidSignature() {
    String message = getTestMessage();
    Map<String, String> headers = getTestHeadersWithValidSignature();
    boolean result = underTest.authenticateEvent(message, TEST_CLIENT_ID, headers);
    assertTrue(result);
  }

  @Test
  public void testVerifyInvalidSignature() {
    String message = getTestMessage();
    Map<String, String> headers = getTestHeadersWithInvalidSignature();
    boolean result = underTest.authenticateEvent(message, TEST_CLIENT_ID, headers);
    assertFalse(result);
  }

  @Test
  public void testVerifyInvalidPublicKey() {
    String message = getTestMessage();
    Map<String, String> headers = getTestHeadersWithInvalidPubKey();
    boolean result = underTest.authenticateEvent(message, TEST_CLIENT_ID, headers);
    assertFalse(result);
  }

  @Test
  public void testVerifyInvalidRecipientClient() {
    String message = getTestMessage();
    Map<String, String> headers = getTestHeadersWithInvalidPubKey();
    boolean result = underTest.authenticateEvent(message, INVALID_TEST_CLIENT_ID, headers);
    assertFalse(result);
  }

  // ============================ PRIVATE HELPER METHODS ================================
  private String getTestMessage() {
    return "{\"event_id\":\"eventId1\",\"event\":{\"hello\":\"world\"},\"recipient_client_id\":\"client_id1\"}";
  }

  private Map<String, String> getTestSignatureHeaders() {
    Map<String, String> testSignatureHeaders = new HashMap<>();
    testSignatureHeaders.put(ADOBE_IOEVENTS_DIGI_SIGN_1, TEST_DIGI_SIGN_1);
    testSignatureHeaders.put(ADOBE_IOEVENTS_DIGI_SIGN_2, TEST_DIGI_SIGN_2);
    testSignatureHeaders.put(ADOBE_IOEVENTS_PUB_KEY_1_PATH, TEST_PUB_KEY1_PATH);
    testSignatureHeaders.put(ADOBE_IOEVENTS_PUB_KEY_2_PATH, TEST_PUB_KEY2_PATH);
    return testSignatureHeaders;
  }

  private Map<String, String> getTestHeadersWithValidSignature() {
    return getTestSignatureHeaders();
  }

  private Map<String, String> getTestHeadersWithInvalidSignature() {
    Map<String, String> signHeaders = getTestSignatureHeaders();
    signHeaders.put(ADOBE_IOEVENTS_DIGI_SIGN_1, TEST_INVALID_DIGI_SIGN_1);
    signHeaders.put(ADOBE_IOEVENTS_DIGI_SIGN_2, TEST_INVALID_DIGI_SIGN_1);
    return signHeaders;
  }

  private Map<String, String> getTestHeadersWithInvalidPubKey() {
    Map<String, String> signHeaders = getTestSignatureHeaders();
    signHeaders.put(ADOBE_IOEVENTS_PUB_KEY_1_PATH, TEST_INVALID_PUB_KEY_PATH);
    signHeaders.put(ADOBE_IOEVENTS_PUB_KEY_2_PATH, TEST_INVALID_PUB_KEY_PATH);
    return signHeaders;
  }
  private String getPubKey1() {
    return "-----BEGIN PUBLIC KEY-----\n"
        + "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAzxbiCd7hyiKbksssNEup\n"
        + "SBhnNRHaPFHUVbi44k82GlFrLBF2MbviEWPxxJxRfqRkysHwMaE+3w74sR9oEunF\n"
        + "Uz3J2vGcXHT4UWfEuKxO/C8dSt7Hex5EoK2R4eld/P7j/p55jp8ODvTW/Yd9ej8q\n"
        + "Dk9dia8ZbkOOuVht2NJlZW4+4p8OCp4MLnSjprvPLAIHU5pD8sIcS+LFYYA3kAz2\n"
        + "pAo/La7W4PFd6f3fuOQrhlBKsL99W6ALyXUOsHHBk0YrcgoxVeDYWO0N3NZLYIZd\n"
        + "aWMxNONoH9kH2mhguidf8MCWwIuYyqO+J+IzsshXVWGyMyn3q7fVZCra9ISEZqWE\n"
        + "iwIDAQAB\n"
        + "-----END PUBLIC KEY-----";
  }

  private String getPubKey2() {
    return "-----BEGIN PUBLIC KEY-----\n"
        + "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAuyszY9i34MeIfmmaFSUz\n"
        + "R0Y4ORhTkwiUafGbRntE0u0wTAKhe9Mewnmrxclh5OrX9jEjWY6lBkxxLYHAa+w2\n"
        + "B4jDExTwiz/o1GKHYf0CGlVw0JqGQVfLlvEGFg5lQsVfOBdSdnxXBSH0FOw7ZQUb\n"
        + "60MD7YKSbk40PRHKzHEcxlHLiHreoqPAIDn3JZ9A7b6QjKOB4LTR6jb3rUtfxnzl\n"
        + "jku8atEfdo341WcHSHW2hf/Gx2mazhGg1of6wZVforXo3R1HVqIVMlOk6GMcz4HH\n"
        + "iLOuEOURFucux3jm4gF2DF1B627vCqaGDoduvyIjitXQS6KqSx3dzB2dGOBDPpsr\n"
        + "8wIDAQAB\n"
        + "-----END PUBLIC KEY-----";
  }

  private String getInvalidPubKey() {
    return "-----BEGIN PUBLIC KEY-----\n"
        + "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAqQypvXnGrmnfoWdcdYg1\n"
        + "+LfHdIVpwU5ycTclnYyWp4zpogug+AfG40j4alKfuUPbuCNQh8DFSRGTgZdHY6lI\n"
        + "mzHcpbcJzkV1ZrW1foFEsNnulO9a7Vv8LNo4UAbZwrha5ozEbOUIttI+B6DKLLYa\n"
        + "4+BjHj0WtxHHuRPibf46qrliMd2St3gdp1yUjGO2qHOHlKHm15K9uwN5SYKANMK2\n"
        + "mZ5+3/uF4Ms21BqRSGCUEwNKpSxXT2bFNlUw0/DbM6gJuE1CJdk5z/sbLA0S3b1z\n"
        + "PR1LpgOeG84lFG7c0gcIaeZX+c3dLdmNBfkOQwacFP3m0urlJkSxI8MomaeEOS2y\n"
        + "hQIDAQAB\n"
        + "-----END PUBLIC KEY-----";
  }
}
