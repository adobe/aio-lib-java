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

import static java.nio.charset.StandardCharsets.UTF_8;

import com.adobe.aio.event.webhook.feign.FeignPubKeyService;
import com.adobe.aio.exception.AIOException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.security.GeneralSecurityException;
import java.security.Signature;
import java.util.Base64;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EventVerifier {

  private static Logger logger = LoggerFactory.getLogger(EventVerifier.class);

  public static final String ADOBE_IOEVENTS_SECURITY_DOMAIN = "https://static.adobeioevents.com";
  public static final String ADOBE_IOEVENTS_DIGI_SIGN_1 = "x-adobe-digital-signature-1";
  public static final String ADOBE_IOEVENTS_DIGI_SIGN_2 = "x-adobe-digital-signature-2";
  public static final String ADOBE_IOEVENTS_PUB_KEY_1_PATH = "x-adobe-public-key1-path";
  public static final String ADOBE_IOEVENTS_PUB_KEY_2_PATH = "x-adobe-public-key2-path";

  private final FeignPubKeyService pubKeyService;

  /**
   * Used for TESTING ONLY
   * for creating instance with a test stub url
   * @param url
   */
  protected EventVerifier(String url) {
    this.pubKeyService = new FeignPubKeyService(url);
  }

  public EventVerifier() {
    this(ADOBE_IOEVENTS_SECURITY_DOMAIN);
  }

  /**
   * @param eventPayload    the event payload to verify
   * @param apiKey          the event payload `recipient_client_id` must be matching it
   * @param requestHeaders  webhook request requestHeaders sent along the event payload: containing
   *                        the path to the two public keys and the associated signatures of the
   *                        eventPayload. Indeed, Adobe I/O Events sends two signatures, either of
   *                        which is valid at any point of time (even when the signatures are rotated).
   *                        So, the signed payload is considered valid if any one of the signatures is valid.
   *                        Refer our public doc for more details -
   *                        https://developer.adobe.com/events/docs/guides/#security-considerations
   * @return the security verification result
   */
  public boolean verify(String eventPayload, String apiKey, Map<String, String> requestHeaders) {
    if (!isValidTargetRecipient(eventPayload, apiKey)) {
      logger.error(
          "Your apiKey {} is not matching the recipient_client_id of the event payload {}", apiKey,
          eventPayload);
      return false;
    }
    if(!requestHeaders.isEmpty()) {
      if (!verifyEventSignatures(eventPayload, requestHeaders)) {
        logger.error("signatures are not valid for message {}", eventPayload);
        return false;
      }
      return true;
    } else {
      logger.error("you request is missing the required headers for signatures and public key paths");
      return false;
    }
  }

  private boolean verifyEventSignatures(String message, Map<String, String> headers) {
    return verifySignature(message, headers.get(ADOBE_IOEVENTS_PUB_KEY_1_PATH),
        headers.get(ADOBE_IOEVENTS_DIGI_SIGN_1)) ||
        verifySignature(message, headers.get(ADOBE_IOEVENTS_PUB_KEY_2_PATH),
            headers.get(ADOBE_IOEVENTS_DIGI_SIGN_1));
  }

  private boolean verifySignature(String message, String publicKeyPath, String signature) {
    byte[] data = message.getBytes(UTF_8);
    try {
      // signature generated at I/O Events side is Base64 encoded, so it must be decoded
      byte[] sign = Base64.getDecoder().decode(signature);
      Signature sig = Signature.getInstance("SHA256withRSA");
      sig.initVerify(pubKeyService.getAioEventsPublicKey(publicKeyPath));
      sig.update(data);
      return sig.verify(sign);
    } catch (GeneralSecurityException e) {
      throw new AIOException("Error verifying signature for public key " + publicKeyPath
          + ". Reason -> " + e.getMessage(), e);
    }
  }

  private boolean isValidTargetRecipient(String message, String clientId) {
    try {
      ObjectMapper mapper = new ObjectMapper();
      JsonNode jsonPayload = mapper.readTree(message);
      JsonNode recipientClientIdNode = jsonPayload.get("recipient_client_id");
      return (recipientClientIdNode != null && recipientClientIdNode.textValue() != null
          && recipientClientIdNode.textValue().equals(clientId));
    } catch (JsonProcessingException e) {
      throw new AIOException("error parsing the event payload during target recipient check..");
    }
  }

}
