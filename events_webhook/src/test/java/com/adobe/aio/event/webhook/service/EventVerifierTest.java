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
import static com.adobe.aio.event.webhook.service.EventVerifier.RECIPIENT_CLIENT_ID;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.configureFor;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.notFound;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.github.tomakehurst.wiremock.WireMockServer;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class EventVerifierTest {

  private static final String API_KEY = "client_id1";
  private static final String ANOTHER_API_KEY = "another_api_key";
  private static final String VALID_SIGNATURE_1 = "IaHo9/8DYt2630pAtjIJeGtsHjB61zOSiAb3S4X1VdPooxikfk79H/t3rgaSbmQMOnjVPRpYVNsHn1fE+l80gjEqmljgNEHt+BtfEH8EsEigwbjQS9opTx/GFnexw3h/sWOt4MGWt3TFK484Dsijijcs1gLwcxTyVUeU2G2XXECpH4dvvEXWQP+1HDFu9nrN+MU/aOR17cNF5em/D/jKjgTcaPx7jK+W5M57F3qqsmdcPxM1ltQxx1/iAXWaOffOC/nXSda5fLFZL75RKBIoveDjL9zthVkBVY9qKXYyK6S/usc2bW3PpXuRTd5Xv2bFB2Mlzr0Gi6St/iiNYLEl3g==";
  private static final String VALID_SIGNATURE_2 = "Xx8uVpZlKIOqAdVBr/6aNrASk6u7i/Bb9kWZttIFOu0Y2JGozZGG7WF9Z6056RdeeBUXLJsV4r8a3ZeEUrOZi3hvhV+Hw7vmK1NIQJVIqdigF9mJ/2gSMGe7K4OPedh+fPNZmbOyNIc6FRmUtTdemNLJeCzM7Zf+niC7Tfsytsz4lW4ebv34TWHxzAA9pZRcJE4a1YYqEYAqn3cHTvCzB/AQ6VdIcP8MsuTGatCk9Vc6dTPOVEcyYkVXTMGgsmzW8RB6mq0m1aqTz3KvnhEYlkspqtxi+jBkTjcYVf1dPa4ofbosmD5rohIef/UwPX5n5ZHM7du86Gf+6S72ee8tbw==";
  private static final String INVALID_SIGNATURE = "abc22OGm8/6H6bJXSi+/4VztsPN+fPZtHgHrrASuTw7LTUZVpbAZNaXVTzQsFd47PvaI8aQxbl874GFmH0QfAVQaRT93x5O/kQdM1ymG03303QaFY/mjm/Iot3VEwq5xOtM8f5a2mKUce9bgEv28iN7z9H/MbBOSmukPSJh/vMLkFAmMZQwdP4SRK3ckxQg6wWTbeMRxjw8/FLckznCGPZri4c0O7WPr8wnrWcvArlhBpIPJPeifJOyDj/woFQzoeemdrVoBFOieE/j3RoMWzcQeLENaSrqk00MPL2svNQcTLMkmWuICOjYSbnlv/EPFCQS8bQsnVHxGFD1yDeFa7Q==";
  private static final String PUB_KEY1_PATH = "/junit/pub-key-1.pem";
  private static final String PUB_KEY2_PATH = "/junit/pub-key-2.pem";
  private static final String ANOTHER_PUB_KEY_PATH = "/junit/another-pub-key.pem";
  private static final String NOT_FOUND_PUB_KEY_PATH = "/junit/not-found-pub-key.pem";

  private EventVerifier underTest;

  private WireMockServer wireMockServer;

  @Before
  public void setup() {

    wireMockServer = new WireMockServer(wireMockConfig().dynamicPort());
    wireMockServer.start();
    int port = wireMockServer.port();
    setupEndpointStub(port);
    underTest = new EventVerifier("http://localhost:" + port);
  }

  @After
  public void tearDown() {
    wireMockServer.stop();
  }

  private void setupEndpointStub(int port) {
    configureFor("localhost", port);
    stubFor(get(urlEqualTo(PUB_KEY1_PATH))
        .willReturn(aResponse().withBody(getPubKey1())));
    stubFor(get(urlEqualTo(PUB_KEY2_PATH))
        .willReturn(aResponse().withBody(getPubKey2())));
    stubFor(get(urlEqualTo(ANOTHER_PUB_KEY_PATH))
        .willReturn(aResponse().withBody(getAnotherPubKey())));
    stubFor(get(urlEqualTo(NOT_FOUND_PUB_KEY_PATH))
        .willReturn(notFound()));

  }

  @Test
  public void testVerifyWithValidSignatures() {
    assertTrue(underTest.verify(getValidPayload(), API_KEY, getValidHeaders()));
  }

  @Test
  public void testVerifyValidSignature1() {
    Map<String, String> headers = getValidHeaders();
    headers.put(ADOBE_IOEVENTS_DIGI_SIGN_1, INVALID_SIGNATURE);
    assertTrue(underTest.verify(getValidPayload(), API_KEY, headers));
  }

  @Test
  public void testVerifyValidSignature2() {
    Map<String, String> headers = getValidHeaders();
    headers.put(ADOBE_IOEVENTS_DIGI_SIGN_2, INVALID_SIGNATURE);
    assertTrue(underTest.verify(getValidPayload(), API_KEY, headers));
  }

  @Test
  public void testVerifyInvalidSignature() {
    Map<String, String> headers = getValidHeaders();
    headers.put(ADOBE_IOEVENTS_DIGI_SIGN_1, INVALID_SIGNATURE);
    headers.put(ADOBE_IOEVENTS_DIGI_SIGN_2, INVALID_SIGNATURE);
    assertFalse(underTest.verify(getValidPayload(), API_KEY, headers));
  }

  @Test
  public void testVerifyBadlyFormattedSignature() {
    String testPayload = getValidPayload();
    Map<String, String> headers = getValidHeaders();
    // not base64 encoded
    headers.put(ADOBE_IOEVENTS_DIGI_SIGN_1, "some random String");
    headers.put(ADOBE_IOEVENTS_DIGI_SIGN_2, "some random String");
    assertFalse(underTest.verify(testPayload, API_KEY, headers));

    // just base64 encoded
    headers.put(ADOBE_IOEVENTS_DIGI_SIGN_1,
        Base64.getEncoder().encodeToString("some random String".getBytes()));
    headers.put(ADOBE_IOEVENTS_DIGI_SIGN_2,
        Base64.getEncoder().encodeToString("some random String".getBytes()));
    assertFalse(underTest.verify(testPayload, API_KEY, headers));
  }

  @Test
  public void testVerifyPublicKeyNotFound() {
    Map<String, String> headers = getValidHeaders();
    headers.put(ADOBE_IOEVENTS_PUB_KEY_1_PATH, NOT_FOUND_PUB_KEY_PATH);
    headers.put(ADOBE_IOEVENTS_PUB_KEY_2_PATH, NOT_FOUND_PUB_KEY_PATH);
    assertFalse(underTest.verify(getValidPayload(), API_KEY, headers));
  }

  @Test
  public void testVerifyOnePublicKeyNotFound() {
    Map<String, String> headers = getValidHeaders();
    headers.put(ADOBE_IOEVENTS_PUB_KEY_1_PATH, NOT_FOUND_PUB_KEY_PATH);
    assertTrue(underTest.verify(getValidPayload(), API_KEY, headers));
  }

  @Test
  public void testVerifyPublicKeyMismatch() {
    Map<String, String> headers = getValidHeaders();
    headers.put(ADOBE_IOEVENTS_PUB_KEY_1_PATH, ANOTHER_PUB_KEY_PATH);
    headers.put(ADOBE_IOEVENTS_PUB_KEY_2_PATH, ANOTHER_PUB_KEY_PATH);
    assertFalse(underTest.verify(getValidPayload(), API_KEY, headers));
  }

  @Test
  public void testVerifyOnePublicKeyMismatch() {
    Map<String, String> headers = getValidHeaders();
    headers.put(ADOBE_IOEVENTS_PUB_KEY_1_PATH, ANOTHER_PUB_KEY_PATH);
    assertTrue(underTest.verify(getValidPayload(), API_KEY, headers));
  }

  @Test
  public void testVerifyWithApiKeyMismatch() {
    assertFalse(underTest.verify(getValidPayload(), ANOTHER_API_KEY, getValidHeaders()));
  }

  @Test
  public void testVerifyInvalidEventPayloads() {
    Map<String, String> headers = getValidHeaders();
    assertFalse(underTest.verify("aSimpleString", API_KEY, headers));
    assertFalse(underTest.verify("{\"key\":\"value\"}", API_KEY, headers));
    assertFalse(underTest.verify("{\"key\":\"value\"", API_KEY, headers));
  }

  @Test
  public void testMissingOneHeader() {
    Map<String, String> headers = new HashMap<>();
    headers.put(ADOBE_IOEVENTS_DIGI_SIGN_1, VALID_SIGNATURE_1);
    headers.put(ADOBE_IOEVENTS_DIGI_SIGN_2, VALID_SIGNATURE_2);
    headers.put(ADOBE_IOEVENTS_PUB_KEY_1_PATH, PUB_KEY1_PATH);
    assertFalse(underTest.verify(getValidPayload(), API_KEY, headers));
  }

  @Test
  public void testEmptyHeaders() {
    assertFalse(underTest.verify(getValidPayload(), API_KEY, new HashMap<>()));
  }

  // ============================ PRIVATE HELPER METHODS ================================
  private String getValidPayload() {
    return "{\"event_id\":\"eventId1\",\"event\":{\"hello\":\"world\"},\""
        + RECIPIENT_CLIENT_ID + "\":\"" + API_KEY + "\"}";
  }

  private Map<String, String> getValidHeaders() {
    Map<String, String> headers = new HashMap<>();
    headers.put(ADOBE_IOEVENTS_DIGI_SIGN_1, VALID_SIGNATURE_1);
    headers.put(ADOBE_IOEVENTS_DIGI_SIGN_2, VALID_SIGNATURE_2);
    headers.put(ADOBE_IOEVENTS_PUB_KEY_1_PATH, PUB_KEY1_PATH);
    headers.put(ADOBE_IOEVENTS_PUB_KEY_2_PATH, PUB_KEY2_PATH);
    return headers;
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

  private String getAnotherPubKey() {
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
