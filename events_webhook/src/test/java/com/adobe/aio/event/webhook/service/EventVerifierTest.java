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
  private static final String VALID_SIGNATURE_1 = "i7qHaTn6x7vLxEHE74hJaFfnsV8yaz/IPucUliqF2JO/60SI1d2YeEb+hU1KJIBI3Cfyz/Ou9UvVfhQLC+Pa6wV8efKAV+yl7Qca7SqyoP9FrA7esZnzCCJKaf+M4IS2QMndU9xbqPL8tSk/W0hau6YZsw8Ubkqtt5lwbQ58Z+zUetpb+XcoIhBc6NsDmgUP32dsoJ6R/Tu5Ukqi8BBQ7kucDl6bmH00sDeOz8dXCVZvSsuvd7ACbgOIMRhZkQF878ffJ+O+PF0WJXO20xOHztnW86GA/l1ZtVDQhwSYGmBQg45LnuRy9GcfT+sMwlYQEyH/e26bZL76Amxw46nYdQ==";
  private static final String VALID_SIGNATURE_2 = "fqwUxrESvWBzt/i7BcGWfovS7tAQB8AikemXOJZYNyTaZKbU/7eL2TfEKbOTdjy+m2wuVhLB0YU+FNyfo8+CFwkG6dJ+uUDM5pWrIBgxeDX9ns/cXPGvzNFLJT5g8t9S2GMV+t9DebbFcbkCSMCp5KomMyKGa/w6/LY4kZmpcMd4mDvdWf4sol4rzetjO0wlrrJqshnEsNiP+8qtKg9umii794smNE2F9e8GZvOwxqvIzHaJ03aI3NwhgW4Of+f9s2dHeJJP2AlgH1kH+FbYM9u+/t6aFAS1BttRGW5zWwQsV349L3Efx5oWWqZ0wMs781AZuSHXAjK7TiJACoiIDg==";
  private static final String VALID_SIGNATURE_1_FOR_BATCH_PAYLOAD = "tchPD9CPmn7BLCx+xOa2QVKLMGN4ZOyhO1EcnkZYRrXkiowspXIkUoGlQjY4u7PXv4nDz/nKMH4mGZW7wlHfHAXfSEmapB31ZZtGlnZWjRrQ7JYb6mzTbPVoL0ExcGJV7qhAhINSZ7oxQFa0cljqVlHnmUs/TgANQNlWfu17vevdIFZ0P2XM7tXo9hoKuq2aFs04QqZZNDPrfIXAizVZU3npz250UtkioNsK9oMJ6WlvjZmDj+t5oZ3eofl+4ebGMaq+q/G4rtQVLdCnLHAVR5PED2nLEPDpSsU7dEyvkCL8571liR+0ShTdDZrVlXwvK5JschC4bHAA78mFfFbhxw==";
  private static final String VALID_SIGNATURE_2_FOR_BATCH_PAYLOAD = "OaKFMWlfXh7u95MYyptGouMMCprJRNcGCFi9SIN2RZBNcm24iQEJQQBEnApxIAh7XMnOHKfl2SxQdkWX75VPpW2DbDMeVZIlNjtGlINZrwByOWCL9SEw/8yRuVCP4JOTg/r0CIi1j3JE7SFN6pbJapKYQ6fB55jSttHpa6+hzVd1PxSjmxl0bUNF6hLWksd2HatIUV1Ozr3YgPldfBimbsnBtK03RF16G4Jlm+wMjhg5yCKb86nWnLas8FU0pvQ7QcJE2AeMs1vp6lf5zJbrPqD1RYVOopwVGBp2TNRjzaIJ5GLwm921W2sRD/kDJwRH++8u6Msq4Kv1Pg2k8rwiZw==";
  private static final String INVALID_SIGNATURE = "abc22OGm8/6H6bJXSi+/4VztsPN+fPZtHgHrrASuTw7LTUZVpbAZNaXVTzQsFd47PvaI8aQxbl874GFmH0QfAVQaRT93x5O/kQdM1ymG03303QaFY/mjm/Iot3VEwq5xOtM8f5a2mKUce9bgEv28iN7z9H/MbBOSmukPSJh/vMLkFAmMZQwdP4SRK3ckxQg6wWTbeMRxjw8/FLckznCGPZri4c0O7WPr8wnrWcvArlhBpIPJPeifJOyDj/woFQzoeemdrVoBFOieE/j3RoMWzcQeLENaSrqk00MPL2svNQcTLMkmWuICOjYSbnlv/EPFCQS8bQsnVHxGFD1yDeFa7Q==";
  private static final String PUB_KEY1_PATH = "/junit/pub-key-1.pem";
  private static final String PUB_KEY2_PATH = "/junit/pub-key-2.pem";
  private static final String ANOTHER_PUB_KEY_PATH = "/junit/another-pub-key.pem";
  private static final String VALID_PAYLOAD = "{\"eventid\":\"eventId1\",\"event\":{\"hello\":\"world\"},\"recipientclientid\":\"client_id1\"}";
  private static final String VALID_BATCH_PAYLOAD = "[{\"eventid\":\"eventId1\",\"event\":{\"hello\":\"world\"},\"" + RECIPIENT_CLIENT_ID + "\":\"" + API_KEY + "\"}]";
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

    String publicK = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAwwFivc/m/4JRjhSvzap/\n"
        + "Q99qm9aqefy5kW7X61XCfzfYHxOWyLx+9IQqs+Z5ygUaKIIDcwgwBCmL2vOWTv2d\n"
        + "NRAIaPWlN+kB3daf8hQTVrt3tAS9Rd6m9pIhFziQad+OnFnSq9+YjtnBP4JXsy0C\n"
        + "FwVXf2667N4j8IG9HBpJDR8olXl6EApu3P6LFKBiwbU7aep6JkafL0Tc8IeBKTPk\n"
        + "u7BxBJWbNxlBcwetvh+sNiqlsfcHF0o6N5xDecbajH0RXWHEO3NpyU4H2YlNb8IV\n"
        + "uOosiEQ+DT8eALEOTWcADH5UXCPlTXR6+cgoWp4Gbgt29c5cvWZd9q/0rRtAr27S\n"
        + "ewIDAQAB";

    publicK = publicK.replaceAll(System.lineSeparator(), "");
    return stringToKey(publicK);
  }

  private static PublicKey getPubKey2() throws Exception {
    String publicK = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAshVvy/d7VXOQc04MBY+l\n"
        + "hc8ihYCprpuCMUv4cnCrOouBDR1FBP0gajAN0eFlmVOgMYeE6j6l59oGqTbAE2Ls\n"
        + "hG9iKEqKh1qGhrOqh7VQc2z1fT2BhlL1nH/1uV4Io3EEc3cSwK/pSwl6Rdag3Twm\n"
        + "g22/R2e57Y8bFhZoOOBnn9EIUH+MChZZR5tVXnsyphg35sL0WSneh0UEopuluvck\n"
        + "jZ1OV4YxIh6Ce1NMHzF2ewiiDmMC6hT+Xf64naexWpnrq9vRqJy0yzJrLeGekdV+\n"
        + "3N8H52SB5+eumY3EKanaEPOkRqfaa4XhwqNC6x6qt0YpC9pgtQmvddqGMhHgyWhr\n"
        + "hwIDAQAB";
    publicK = publicK.replaceAll(System.lineSeparator(), "");
    return stringToKey(publicK);
  }

  private static PublicKey getPublicKey1ForBatchPayload() throws Exception {
    String publicK = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAwwFivc/m/4JRjhSvzap/\n"
        + "Q99qm9aqefy5kW7X61XCfzfYHxOWyLx+9IQqs+Z5ygUaKIIDcwgwBCmL2vOWTv2d\n"
        + "NRAIaPWlN+kB3daf8hQTVrt3tAS9Rd6m9pIhFziQad+OnFnSq9+YjtnBP4JXsy0C\n"
        + "FwVXf2667N4j8IG9HBpJDR8olXl6EApu3P6LFKBiwbU7aep6JkafL0Tc8IeBKTPk\n"
        + "u7BxBJWbNxlBcwetvh+sNiqlsfcHF0o6N5xDecbajH0RXWHEO3NpyU4H2YlNb8IV\n"
        + "uOosiEQ+DT8eALEOTWcADH5UXCPlTXR6+cgoWp4Gbgt29c5cvWZd9q/0rRtAr27S\n"
        + "ewIDAQAB";

    publicK = publicK.replaceAll(System.lineSeparator(), "");
    return stringToKey(publicK);
  }

  private static PublicKey getPublicKey2ForBatchPayload() throws Exception {
    String publicK = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAshVvy/d7VXOQc04MBY+l\n"
        + "hc8ihYCprpuCMUv4cnCrOouBDR1FBP0gajAN0eFlmVOgMYeE6j6l59oGqTbAE2Ls\n"
        + "hG9iKEqKh1qGhrOqh7VQc2z1fT2BhlL1nH/1uV4Io3EEc3cSwK/pSwl6Rdag3Twm\n"
        + "g22/R2e57Y8bFhZoOOBnn9EIUH+MChZZR5tVXnsyphg35sL0WSneh0UEopuluvck\n"
        + "jZ1OV4YxIh6Ce1NMHzF2ewiiDmMC6hT+Xf64naexWpnrq9vRqJy0yzJrLeGekdV+\n"
        + "3N8H52SB5+eumY3EKanaEPOkRqfaa4XhwqNC6x6qt0YpC9pgtQmvddqGMhHgyWhr\n"
        + "hwIDAQAB";
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
