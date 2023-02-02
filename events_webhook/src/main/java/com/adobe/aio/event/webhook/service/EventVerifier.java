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

  /**
   * Your Event Verifier instance must be created with the Adobe IO serving CDN host as below
   */
  public EventVerifier() {
    this(ADOBE_IOEVENTS_SECURITY_DOMAIN);
  }

  /**
   * Authenticate the event by checking the target recipient
   * and verifying the signatures
   * @param message - the event payload
   * @param clientId - recipient client id in the payload
   * @param headers - webhook request headers
   * @return boolean - TRUE if valid event else FALSE
   */
  public boolean authenticateEvent(String message, String clientId, Map<String, String> headers) {
    if (!isValidTargetRecipient(message, clientId)) {
      logger.error("target recipient {} is not valid for message {}", clientId, message);
      return false;
    }
    if (!verifyEventSignatures(message, headers)) {
      logger.error("signatures are not valid for message {}", message);
      return false;
    }
    return true;
  }

  private boolean verifyEventSignatures(String message, Map<String, String> headers) {
    // Adobe I/O Events sends two signatures for a signed event payload to the webhook
    // either of which is valid at any point of time even when the signatures are rotated.
    // So, the signed payload is considered valid if any one of the signatures is valid
    // refer our public doc for more details - https://developer.adobe.com/events/docs/guides/#security-considerations
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
      return (recipientClientIdNode != null && recipientClientIdNode.textValue() !=null
          && recipientClientIdNode.textValue().equals(clientId));
    } catch (JsonProcessingException e) {
      throw new AIOException("error parsing the event payload during target recipient check..");
    }
  }

}