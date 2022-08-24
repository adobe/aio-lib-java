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

import java.util.HashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class EventVerifierTest {

  private static final String TEST_CLIENT_ID = "client_id1";
  private static final String INVALID_TEST_CLIENT_ID = "invalid_client_id";
  private static final String TEST_DIGI_SIGN_1 = "mvEj6rEiJK9QFtX5QNl1bc9AJW7sOFAHgUk25ytroi8yqrRAPtijCu19vS6o96eANnjHQzS76p+sA1Y4giQ+DnQOZz35AJwvnDmK2FVkbaiupinCjRK4pnwd0aY88HI6o8Fmj6zomAKcF46D8IHglirCjiUHT3gENFHnuro55rgnJ3eWrT4ldcruKHSKAopapAhqQBr1BhrHVqtoz8Zg+ZWGsSgY+tAje3m59mTjWPSC1/KfjgpjhADDKHz+I1eT/z5k0667hlLSgYvHGhmhh8aAcLKgM5tzcXWpYQz4xCZgSF/MqAIUkmnVVWHhs18rr1WSaJ4j2ZTO6vaj8XoHng==";
  private static final String TEST_DIGI_SIGN_2 = "GpMONiPMHY51vpHF3R9SSs9ogRn8i2or/bvV3R+PYXGgDzAxDhRdl9dIUp/qQ3vsxDGEv045IV4GQ2f4QbsFvWLJsBNyCqLs6KL8LsRoGfEC4Top6c1VVjrEEQ1MOoFcoq/6riXzg4h09lRTfARllVv+icgzAiuv/JW2HNg5yQ4bqenFELD6ipCStuaI/OGS0A9s0Hc6o3aoHz3r5d5DecwE6pUdpG8ODhKBM+34CvcvMDNdrj8STYWHsEUqGdR9klpaqaC1QRYFIO7WgbgdwsuGULz6Sjm+q5s5Wh++fz5E+gXkizFviD389gDIUylFTig/1h7WTLRDuSz69Q+C5w==";
  private static final String TEST_PUB_KEY1_PATH = "qe/keys/pub-key-voy5XEbWmT.pem";
  private static final String TEST_PUB_KEY2_PATH = "qe/keys/pub-key-maAv3Tg6ZH.pem";

  private EventVerifier underTest;

  @Before
  public void setup() {
    underTest = new EventVerifier();
  }

  @Test
  public void testVerifyValidSignature() {
    String message = getTestMessage();
    Map<String, String> headers = getTestHeadersWithValidSignature();
    boolean result = underTest.authenticateEvent(message, TEST_CLIENT_ID, headers);
    Assert.assertEquals(true, result);
  }

  @Test
  public void testVerifyInvalidSignature() {
    String message = getTestMessage();
    Map<String, String> headers = getTestHeadersWithInvalidSignature();
    boolean result = underTest.authenticateEvent(message, TEST_CLIENT_ID, headers);
    Assert.assertEquals(Boolean.FALSE, result);
  }

  @Test
  public void testVerifyInvalidPublicKey() {
    String message = getTestMessage();
    Map<String, String> headers = getTestHeadersWithInvalidPubKey();
    boolean result = underTest.authenticateEvent(message, TEST_CLIENT_ID, headers);
    Assert.assertEquals(Boolean.FALSE, result);
  }

  @Test
  public void testVerifyInvalidRecipientClient() {
    String message = getTestMessage();
    Map<String, String> headers = getTestHeadersWithInvalidPubKey();
    boolean result = underTest.authenticateEvent(message, INVALID_TEST_CLIENT_ID, headers);
    Assert.assertEquals(Boolean.FALSE, result);
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
    signHeaders.put(ADOBE_IOEVENTS_DIGI_SIGN_1, TEST_DIGI_SIGN_2);
    signHeaders.put(ADOBE_IOEVENTS_DIGI_SIGN_2, TEST_DIGI_SIGN_1);
    return signHeaders;
  }

  private Map<String, String> getTestHeadersWithInvalidPubKey() {
    Map<String, String> signHeaders = getTestSignatureHeaders();
    signHeaders.put(ADOBE_IOEVENTS_PUB_KEY_1_PATH, TEST_PUB_KEY2_PATH);
    signHeaders.put(ADOBE_IOEVENTS_PUB_KEY_2_PATH, TEST_PUB_KEY1_PATH);
    return signHeaders;
  }
}
