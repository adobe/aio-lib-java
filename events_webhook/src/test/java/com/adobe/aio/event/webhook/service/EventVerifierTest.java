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

import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import com.adobe.aio.event.webhook.feign.FeignPubKeyService;
import com.adobe.aio.exception.AIOException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedConstruction;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.adobe.aio.event.webhook.service.EventVerifier.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EventVerifierTest {

  private static final String API_KEY = "client_id1";
  private static final String ANOTHER_API_KEY = "another_api_key";
  private static final String VALID_SIGNATURE_1 = "IaHo9/8DYt2630pAtjIJeGtsHjB61zOSiAb3S4X1VdPooxikfk79H/t3rgaSbmQMOnjVPRpYVNsHn1fE+l80gjEqmljgNEHt+BtfEH8EsEigwbjQS9opTx/GFnexw3h/sWOt4MGWt3TFK484Dsijijcs1gLwcxTyVUeU2G2XXECpH4dvvEXWQP+1HDFu9nrN+MU/aOR17cNF5em/D/jKjgTcaPx7jK+W5M57F3qqsmdcPxM1ltQxx1/iAXWaOffOC/nXSda5fLFZL75RKBIoveDjL9zthVkBVY9qKXYyK6S/usc2bW3PpXuRTd5Xv2bFB2Mlzr0Gi6St/iiNYLEl3g==";
  private static final String VALID_SIGNATURE_2 = "Xx8uVpZlKIOqAdVBr/6aNrASk6u7i/Bb9kWZttIFOu0Y2JGozZGG7WF9Z6056RdeeBUXLJsV4r8a3ZeEUrOZi3hvhV+Hw7vmK1NIQJVIqdigF9mJ/2gSMGe7K4OPedh+fPNZmbOyNIc6FRmUtTdemNLJeCzM7Zf+niC7Tfsytsz4lW4ebv34TWHxzAA9pZRcJE4a1YYqEYAqn3cHTvCzB/AQ6VdIcP8MsuTGatCk9Vc6dTPOVEcyYkVXTMGgsmzW8RB6mq0m1aqTz3KvnhEYlkspqtxi+jBkTjcYVf1dPa4ofbosmD5rohIef/UwPX5n5ZHM7du86Gf+6S72ee8tbw==";
  private static final String VALID_SIGNATURE_1_FOR_BATCH_PAYLOAD = "FHdNfD32TWCLbTUbnQykLWfyNfD+XpNroo0sqmVMhmdMGUqVQMkg7SVfIr8vt12EhGPzapqIbgFe2b6lU7isflzzJWQfpN+BB7brMzfrxQVDkAZZP7hvm9FLqPVAV24/jLojyfNJbLNufEN8rg0+u90bGxZjzAOAWdHdkA3xbJudJc+mzpW0GSxKN7QszrIC0P0A2bTmpvfVtyCbR3uM9JVwBPwR0yqq0EuFV2GeFhnJzDqH4qq8Q4kScOmgMK/9p/Ii2Km0KquYnBFC7GsPE2OVrZulmOZZpy1F2YNY2NEPehbtqtgVvbKCafzwi85LnOC/ZXbfOnN4HMoWwS6MSQ==";
  private static final String VALID_SIGNATURE_2_FOR_BATCH_PAYLOAD = "v6Id3pI0rVt8b3QTMaceL874PwHztTZAHrydaAap/5uHoMcJhXcXHX0lZtTIEzqar0za2NM93HRz1AJZvuCDlWMPNWd+W2bvodjsh0OAHYJ2woNfeIm6QrnWOYHSC0x0dFuC0gncKGvcNt977Ef7XThEoIbTemIbQt/7dlgTOu9X0hTA9pZHb+iX8AL71zvjP31gWUaAQE9Di1LiACKI1kadfGb0ZNOWXpov3byOl8EXNN7GwoGYtTdP0AlyswhOjRZDEFeCfOoPq+XH2ufqOa0t8inoOp80AczxO9WEqPosyyqhaNjMhSSiN/h41TqMXNsDA7FIRgO6GRg2cRDtog==";
  private static final String INVALID_SIGNATURE = "abc22OGm8/6H6bJXSi+/4VztsPN+fPZtHgHrrASuTw7LTUZVpbAZNaXVTzQsFd47PvaI8aQxbl874GFmH0QfAVQaRT93x5O/kQdM1ymG03303QaFY/mjm/Iot3VEwq5xOtM8f5a2mKUce9bgEv28iN7z9H/MbBOSmukPSJh/vMLkFAmMZQwdP4SRK3ckxQg6wWTbeMRxjw8/FLckznCGPZri4c0O7WPr8wnrWcvArlhBpIPJPeifJOyDj/woFQzoeemdrVoBFOieE/j3RoMWzcQeLENaSrqk00MPL2svNQcTLMkmWuICOjYSbnlv/EPFCQS8bQsnVHxGFD1yDeFa7Q==";
  private static final String PUB_KEY1_PATH = "/junit/pub-key-1.pem";
  private static final String PUB_KEY2_PATH = "/junit/pub-key-2.pem";
  private static final String ANOTHER_PUB_KEY_PATH = "/junit/another-pub-key.pem";
  private static final String VALID_PAYLOAD = "{\"event_id\":\"eventId1\",\"event\":{\"hello\":\"world\"},\"" + RECIPIENT_CLIENT_ID + "\":\"" + API_KEY + "\"}";
  private static final String VALID_BATCH_PAYLOAD = "[{\"event_id\":\"eventId1\",\"event\":{\"hello\":\"world\"},\"" + RECIPIENT_CLIENT_ID + "\":\"" + API_KEY + "\"}]";
  private static final PublicKey PUBLIC_KEY1;
  private static final PublicKey PUBLIC_KEY1_FOR_BATCH_PAYLOAD;
  private static final PublicKey PUBLIC_KEY2_FOR_BATCH_PAYLOAD;
  private static final PublicKey PUBLIC_KEY2;
  private static final PublicKey ANOTHER_PUBLIC_KEY;

  private static final Map<String, String> VALID_HEADERS = new HashMap<>();
  static {
    VALID_HEADERS.put(ADOBE_IOEVENTS_DIGI_SIGN_1, VALID_SIGNATURE_1);
    VALID_HEADERS.put(ADOBE_IOEVENTS_DIGI_SIGN_2, VALID_SIGNATURE_2);
    VALID_HEADERS.put(ADOBE_IOEVENTS_PUB_KEY_1_PATH, PUB_KEY1_PATH);
    VALID_HEADERS.put(ADOBE_IOEVENTS_PUB_KEY_2_PATH, PUB_KEY2_PATH);

    try {
      PUBLIC_KEY1 = getPubKey1();
      PUBLIC_KEY2 = getPubKey2();
      ANOTHER_PUBLIC_KEY = getAnotherPubKey();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private static final Map<String, String> VALID_HEADERS_FOR_BATCH_PAYLOAD = new HashMap<>();
  static {
    VALID_HEADERS_FOR_BATCH_PAYLOAD.put(ADOBE_IOEVENTS_DIGI_SIGN_1, VALID_SIGNATURE_1_FOR_BATCH_PAYLOAD);
    VALID_HEADERS_FOR_BATCH_PAYLOAD.put(ADOBE_IOEVENTS_DIGI_SIGN_2, VALID_SIGNATURE_2_FOR_BATCH_PAYLOAD);
    VALID_HEADERS_FOR_BATCH_PAYLOAD.put(ADOBE_IOEVENTS_PUB_KEY_1_PATH, PUB_KEY1_PATH);
    VALID_HEADERS_FOR_BATCH_PAYLOAD.put(ADOBE_IOEVENTS_PUB_KEY_2_PATH, PUB_KEY2_PATH);

    try {
      PUBLIC_KEY1_FOR_BATCH_PAYLOAD = getPublicKey1ForBatchPayload();
      PUBLIC_KEY2_FOR_BATCH_PAYLOAD = getPublicKey2ForBatchPayload();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Test
  public void validSignatures() {
    try (MockedConstruction<FeignPubKeyService> ignored = mockConstruction(FeignPubKeyService.class,
        (mock, mockContext) -> {
          when(mock.getAioEventsPublicKey(PUB_KEY1_PATH)).thenReturn(PUBLIC_KEY1);
        }
    )) {
      assertTrue(new EventVerifier().verify(VALID_PAYLOAD, API_KEY, VALID_HEADERS));
    }
  }

  @Test
  public void validSignaturesForBatchPayload() {
    try (MockedConstruction<FeignPubKeyService> ignored = mockConstruction(FeignPubKeyService.class,
        (mock, mockContext) -> {
          when(mock.getAioEventsPublicKey(PUB_KEY1_PATH)).thenReturn(PUBLIC_KEY1_FOR_BATCH_PAYLOAD);
          when(mock.getAioEventsPublicKey(PUB_KEY2_PATH)).thenReturn(PUBLIC_KEY2_FOR_BATCH_PAYLOAD);
        }
    )) {
      assertTrue(new EventVerifier().verify(VALID_BATCH_PAYLOAD, API_KEY,
          VALID_HEADERS_FOR_BATCH_PAYLOAD));
    }
  }

  @Test
  public void invalidValidSignature1() {
    Map<String, String> headers = new HashMap<>(VALID_HEADERS);
    headers.put(ADOBE_IOEVENTS_DIGI_SIGN_1, INVALID_SIGNATURE);

    try (MockedConstruction<FeignPubKeyService> ignored = mockConstruction(FeignPubKeyService.class,
        (mock, mockContext) -> {
          when(mock.getAioEventsPublicKey(PUB_KEY1_PATH)).thenReturn(PUBLIC_KEY1);
          when(mock.getAioEventsPublicKey(PUB_KEY2_PATH)).thenReturn(PUBLIC_KEY2);
        }
    )) {
      assertTrue(new EventVerifier().verify(VALID_PAYLOAD, API_KEY, headers));
    }
  }

  @Test
  public void invalidSignature2() {
    Map<String, String> headers = new HashMap<>(VALID_HEADERS);
    headers.put(ADOBE_IOEVENTS_DIGI_SIGN_2, INVALID_SIGNATURE);
    try (MockedConstruction<FeignPubKeyService> ignored = mockConstruction(FeignPubKeyService.class,
        (mock, mockContext) -> {
          when(mock.getAioEventsPublicKey(PUB_KEY1_PATH)).thenReturn(PUBLIC_KEY1);
        }
    )) {
      assertTrue(new EventVerifier().verify(VALID_PAYLOAD, API_KEY, headers));
    }
  }

  @Test
  public void invalidSignatures() {
    Map<String, String> headers = new HashMap<>(VALID_HEADERS);

    headers.put(ADOBE_IOEVENTS_DIGI_SIGN_1, INVALID_SIGNATURE);
    headers.put(ADOBE_IOEVENTS_DIGI_SIGN_2, INVALID_SIGNATURE);
    try (MockedConstruction<FeignPubKeyService> ignored = mockConstruction(FeignPubKeyService.class,
        (mock, mockContext) -> {
          when(mock.getAioEventsPublicKey(PUB_KEY1_PATH)).thenReturn(PUBLIC_KEY1);
          when(mock.getAioEventsPublicKey(PUB_KEY2_PATH)).thenReturn(PUBLIC_KEY2);
        }
    )) {
      assertFalse(new EventVerifier().verify(VALID_PAYLOAD, API_KEY, headers));
    }
  }

  @Test
  public void badlyFormattedSignature() {
    Map<String, String> headers = new HashMap<>(VALID_HEADERS);
    try (MockedConstruction<FeignPubKeyService> ignored = mockConstruction(FeignPubKeyService.class,
        (mock, mockContext) -> {
          when(mock.getAioEventsPublicKey(PUB_KEY1_PATH)).thenReturn(PUBLIC_KEY1);
          when(mock.getAioEventsPublicKey(PUB_KEY2_PATH)).thenReturn(PUBLIC_KEY2);
        }
    )) {
      EventVerifier underTest = new EventVerifier();
      // not base64 encoded
      headers.put(ADOBE_IOEVENTS_DIGI_SIGN_1, "some random String");
      headers.put(ADOBE_IOEVENTS_DIGI_SIGN_2, "some random String");
      assertFalse(underTest.verify(VALID_PAYLOAD, API_KEY, headers));

      // just base64 encoded
      headers.put(ADOBE_IOEVENTS_DIGI_SIGN_1, Base64.getEncoder().encodeToString("some random String".getBytes()));
      headers.put(ADOBE_IOEVENTS_DIGI_SIGN_2, Base64.getEncoder().encodeToString("some random String".getBytes()));
      assertFalse(underTest.verify(VALID_PAYLOAD, API_KEY, headers));
    }
  }

  @Test
  public void publicKeyNotFound() {
    Map<String, String> headers = new HashMap<>(VALID_HEADERS);
    try (MockedConstruction<FeignPubKeyService> ignored = mockConstruction(FeignPubKeyService.class,
        (mock, mockContext) -> {
          when(mock.getAioEventsPublicKey(PUB_KEY1_PATH)).thenThrow(new AIOException("Error"));
          when(mock.getAioEventsPublicKey(PUB_KEY2_PATH)).thenThrow(new AIOException("Error"));
        }
    )) {
      assertFalse(new EventVerifier().verify(VALID_PAYLOAD, API_KEY, headers));
    }
  }

  @Test
  public void onePublicKeyNotFound() {
    Map<String, String> headers = new HashMap<>(VALID_HEADERS);
    try (MockedConstruction<FeignPubKeyService> ignored = mockConstruction(FeignPubKeyService.class,
        (mock, mockContext) -> {
          when(mock.getAioEventsPublicKey(PUB_KEY1_PATH)).thenThrow(new AIOException("Error"));
          when(mock.getAioEventsPublicKey(PUB_KEY2_PATH)).thenReturn(PUBLIC_KEY2);
        }
    )) {
      EventVerifier underTest = new EventVerifier();
      assertTrue(underTest.verify(VALID_PAYLOAD, API_KEY, headers));
    }
  }

  @Test
  public void publicKeyMismatch() {
    Map<String, String> headers = new HashMap<>(VALID_HEADERS);
    headers.put(ADOBE_IOEVENTS_PUB_KEY_1_PATH, ANOTHER_PUB_KEY_PATH);
    headers.put(ADOBE_IOEVENTS_PUB_KEY_2_PATH, ANOTHER_PUB_KEY_PATH);
    try (MockedConstruction<FeignPubKeyService> ignored = mockConstruction(FeignPubKeyService.class,
        (mock, mockContext) -> {
          when(mock.getAioEventsPublicKey(ANOTHER_PUB_KEY_PATH)).thenReturn(ANOTHER_PUBLIC_KEY);
          when(mock.getAioEventsPublicKey(ANOTHER_PUB_KEY_PATH)).thenReturn(ANOTHER_PUBLIC_KEY);
        }
    )) {
      assertFalse(new EventVerifier().verify(VALID_PAYLOAD, API_KEY, headers));
    }
  }

  @Test
  public void onePublicKeyMismatch() {
    Map<String, String> headers = new HashMap<>(VALID_HEADERS);
    headers.put(ADOBE_IOEVENTS_PUB_KEY_1_PATH, ANOTHER_PUB_KEY_PATH);
    try (MockedConstruction<FeignPubKeyService> ignored = mockConstruction(FeignPubKeyService.class,
        (mock, mockContext) -> {
          when(mock.getAioEventsPublicKey(ANOTHER_PUB_KEY_PATH)).thenReturn(ANOTHER_PUBLIC_KEY);
          when(mock.getAioEventsPublicKey(PUB_KEY2_PATH)).thenReturn(PUBLIC_KEY2);
        }
    )) {
      assertTrue(new EventVerifier().verify(VALID_PAYLOAD, API_KEY, headers));
    }
  }

  @Test
  public void withApiKeyMismatch() {
    assertFalse(new EventVerifier().verify(VALID_PAYLOAD, ANOTHER_API_KEY, VALID_HEADERS));
  }

  @Test
  public void invalidEventPayloads() {
    Map<String, String> headers = VALID_HEADERS;
    EventVerifier underTest = new EventVerifier();
    assertFalse(underTest.verify("aSimpleString", API_KEY, headers));
    assertFalse(underTest.verify("{\"key\":\"value\"}", API_KEY, headers));
    assertFalse(underTest.verify("{\"key\":\"value\"", API_KEY, headers));
    assertFalse(underTest.verify("[\"{\"key\":\"value\"}, {\"key\":\"value\"}]", API_KEY, headers));
  }

  @Test
  public void testMissingOneHeader() {
    Map<String, String> headers = new HashMap<>();
    headers.put(ADOBE_IOEVENTS_DIGI_SIGN_1, VALID_SIGNATURE_1);
    headers.put(ADOBE_IOEVENTS_DIGI_SIGN_2, VALID_SIGNATURE_2);
    headers.put(ADOBE_IOEVENTS_PUB_KEY_1_PATH, PUB_KEY1_PATH);
    assertFalse(new EventVerifier().verify(VALID_PAYLOAD, API_KEY, headers));
  }

  @Test
  public void testEmptyHeaders() {
    assertFalse(new EventVerifier().verify(VALID_PAYLOAD, API_KEY, new HashMap<>()));
  }

  private static PublicKey stringToKey(String publicK) throws Exception {
    byte[] publicBytes = Base64.getDecoder().decode(publicK);
    X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicBytes);
    KeyFactory keyFactory = KeyFactory.getInstance("RSA");
    return keyFactory.generatePublic(keySpec);
  }

  private static PublicKey getPubKey1() throws Exception {

    String publicK = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAzxbiCd7hyiKbksssNEup\n"
        + "SBhnNRHaPFHUVbi44k82GlFrLBF2MbviEWPxxJxRfqRkysHwMaE+3w74sR9oEunF\n"
        + "Uz3J2vGcXHT4UWfEuKxO/C8dSt7Hex5EoK2R4eld/P7j/p55jp8ODvTW/Yd9ej8q\n"
        + "Dk9dia8ZbkOOuVht2NJlZW4+4p8OCp4MLnSjprvPLAIHU5pD8sIcS+LFYYA3kAz2\n"
        + "pAo/La7W4PFd6f3fuOQrhlBKsL99W6ALyXUOsHHBk0YrcgoxVeDYWO0N3NZLYIZd\n"
        + "aWMxNONoH9kH2mhguidf8MCWwIuYyqO+J+IzsshXVWGyMyn3q7fVZCra9ISEZqWE\n"
        + "iwIDAQAB";

    publicK = publicK.replaceAll(System.lineSeparator(), "");
    return stringToKey(publicK);
  }

  private static PublicKey getPubKey2() throws Exception {
    String publicK = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAuyszY9i34MeIfmmaFSUz\n"
        + "R0Y4ORhTkwiUafGbRntE0u0wTAKhe9Mewnmrxclh5OrX9jEjWY6lBkxxLYHAa+w2\n"
        + "B4jDExTwiz/o1GKHYf0CGlVw0JqGQVfLlvEGFg5lQsVfOBdSdnxXBSH0FOw7ZQUb\n"
        + "60MD7YKSbk40PRHKzHEcxlHLiHreoqPAIDn3JZ9A7b6QjKOB4LTR6jb3rUtfxnzl\n"
        + "jku8atEfdo341WcHSHW2hf/Gx2mazhGg1of6wZVforXo3R1HVqIVMlOk6GMcz4HH\n"
        + "iLOuEOURFucux3jm4gF2DF1B627vCqaGDoduvyIjitXQS6KqSx3dzB2dGOBDPpsr\n"
        + "8wIDAQAB";
    publicK = publicK.replaceAll(System.lineSeparator(), "");
    return stringToKey(publicK);
  }

  private static PublicKey getPublicKey1ForBatchPayload() throws Exception {
    String publicK = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA32C7cnLjR0a\n"
        + "7nAkPff9u2EaWMeSHepagN65lP5KT0g8crCGX2ZKnU3Q87GR/dJXXa8TO8Q/khVf64\n"
        + "faO+mLdhFrpXu1fCPIaWmvKMVhwUdE8gh/jQKKiM7VP6o4K4SuhHrSJut7GFEfWemz\n"
        + "l9MKP5ykhqKbx11Hse4GwpN26F/oUp5tAfycrPOkQjyUo/WOhLNZ/zJpsl9FQRpYGf\n"
        + "HPWhIRXHuPvb32O1A1F9kNNYISphxPq6+htotnFGuXS3j2G928g0pX7SU2CYGkVBMV\n"
        + "AMJqNqyVENaVON0sVA8u10aIPi9xIi9vR9rGpridIP+zMiwsv+H1zWoU5CX4eN5RN/\n"
        + "QIDAQAB";

    publicK = publicK.replaceAll(System.lineSeparator(), "");
    return stringToKey(publicK);
  }

  private static PublicKey getPublicKey2ForBatchPayload() throws Exception {
    String publicK = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAxw9Gi8RdJuD\n"
        + "ZhMVMexLYCPISnFTuaJ14q9TL2wVnc9pc79GO24mAOFvjx9x4GffOsCKwWPp62wrgj\n"
        + "w4MS4Xyvo4uv9Qyhp0pJGfMgT3BRtzbL/AgJEJAGvluRxZdDOXkZsViB/pQIu1AQJx\n"
        + "+2c0DIRAVxI5AcQc/8BvBsB7AltjH+Bf5hFMuNuARiVouyI83KOh3S6hN+zZu10Dkm\n"
        + "rLA1kZ6X2rKxrnz6bTG8ad+rkL3DbY/WvAah9ZJz8Xq87TS3/NDsveloVQ3+Wm+nF3\n"
        + "tyzdyTRDldwviRFX+EDhzAbTtk2hEZEIU3+9zxhSAscBUfV/2+P12vnowVoYNN7mqr\n"
        + "QIDAQAB";
    publicK = publicK.replaceAll(System.lineSeparator(), "");
    return stringToKey(publicK);
  }

  private static PublicKey getAnotherPubKey() throws Exception {
    String publicK = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAqQypvXnGrmnfoWdcdYg1\n"
        + "+LfHdIVpwU5ycTclnYyWp4zpogug+AfG40j4alKfuUPbuCNQh8DFSRGTgZdHY6lI\n"
        + "mzHcpbcJzkV1ZrW1foFEsNnulO9a7Vv8LNo4UAbZwrha5ozEbOUIttI+B6DKLLYa\n"
        + "4+BjHj0WtxHHuRPibf46qrliMd2St3gdp1yUjGO2qHOHlKHm15K9uwN5SYKANMK2\n"
        + "mZ5+3/uF4Ms21BqRSGCUEwNKpSxXT2bFNlUw0/DbM6gJuE1CJdk5z/sbLA0S3b1z\n"
        + "PR1LpgOeG84lFG7c0gcIaeZX+c3dLdmNBfkOQwacFP3m0urlJkSxI8MomaeEOS2y\n"
        + "hQIDAQAB";
    publicK = publicK.replaceAll(System.lineSeparator(), "");
    return stringToKey(publicK);
  }
}
