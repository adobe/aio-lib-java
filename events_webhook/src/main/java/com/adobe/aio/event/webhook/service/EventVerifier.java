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
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.security.Signature;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EventVerifier {

  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  public static final String RECIPIENT_CLIENT_ID = "recipient_client_id";

  public static final String ADOBE_IOEVENTS_SECURITY_DOMAIN = "https://static.adobeioevents.com";
  public static final String ADOBE_IOEVENTS_DIGI_SIGN_1 = "x-adobe-digital-signature-1";
  public static final String ADOBE_IOEVENTS_DIGI_SIGN_2 = "x-adobe-digital-signature-2";
  public static final String ADOBE_IOEVENTS_PUB_KEY_1_PATH = "x-adobe-public-key1-path";
  public static final String ADOBE_IOEVENTS_PUB_KEY_2_PATH = "x-adobe-public-key2-path";

  private final FeignPubKeyService pubKeyService;

  /**
   * Used for TESTING ONLY for creating instance with a test stub url
   *
   * @param url
   */
  EventVerifier(String url) {
    this.pubKeyService = new FeignPubKeyService(url);
  }

  public EventVerifier() {
    this(ADOBE_IOEVENTS_SECURITY_DOMAIN);
  }

  /**
   * @param eventPayload   the event payload to verify
   * @param apiKey         the event payload `recipient_client_id` must be matching it
   * @param requestHeaders webhook request requestHeaders sent along the event payload: containing
   *                       the path to the two public keys and the associated signatures of the
   *                       eventPayload. Indeed, Adobe I/O Events sends two signatures, either of
   *                       which is valid at any point of time (even when the signatures are
   *                       rotated). So, the signed payload is considered valid if any one of the
   *                       signatures is valid. Refer our public doc for more details -
   *                       https://developer.adobe.com/events/docs/guides/#security-considerations
   * @return the security verification result
   */
  public boolean verify(String eventPayload, String apiKey, Map<String, String> requestHeaders) {
    if (!isValidTargetRecipient(eventPayload, apiKey)) {
      logger.error(
          "Your apiKey `{}` is not matching the `{}` of the event payload `{}`", apiKey,
          RECIPIENT_CLIENT_ID, eventPayload);
      return false;
    }
    if (requestHeaders.isEmpty() ||
        StringUtils.isEmpty(requestHeaders.get(ADOBE_IOEVENTS_DIGI_SIGN_1)) ||
        StringUtils.isEmpty(requestHeaders.get(ADOBE_IOEVENTS_DIGI_SIGN_2)) ||
        StringUtils.isEmpty(requestHeaders.get(ADOBE_IOEVENTS_PUB_KEY_1_PATH)) ||
        StringUtils.isEmpty(requestHeaders.get(ADOBE_IOEVENTS_PUB_KEY_2_PATH))) {
      logger.error("At least one of the required Adobe IO Events headers is missing");
      return false;
    } else {
      return verifyEventSignatures(eventPayload, requestHeaders);
    }
  }

  private boolean verifyEventSignatures(String eventPayload, Map<String, String> headers) {
    return verifySignature(eventPayload, headers.get(ADOBE_IOEVENTS_PUB_KEY_1_PATH),
          headers.get(ADOBE_IOEVENTS_DIGI_SIGN_1)) ||
          verifySignature(eventPayload, headers.get(ADOBE_IOEVENTS_PUB_KEY_2_PATH),
              headers.get(ADOBE_IOEVENTS_DIGI_SIGN_2));
    }


  private boolean verifySignature(String eventPayload, String publicKeyPath, String signature) {
    try {
      // signature generated at I/O Events side is Base64 encoded, so it must be decoded
      byte[] sign = Base64.getDecoder().decode(signature);
      Signature sig = Signature.getInstance("SHA256withRSA");
      sig.initVerify(pubKeyService.getAioEventsPublicKey(publicKeyPath));
      sig.update(eventPayload.getBytes(UTF_8));
      return sig.verify(sign);
    } catch (Exception e) {
      logger.error(
          "Invalid signature for public key " + publicKeyPath + ". Reason -> " + e.getMessage(), e);
      return false;
    }
  }

  private boolean isValidTargetRecipient(String eventPayload, String clientId) {
    try {
      ObjectMapper mapper = new ObjectMapper();
      JsonNode jsonPayload = mapper.readTree(eventPayload);

      if (jsonPayload.isArray()) {
        List recipientClientIds = new ArrayList<String>();
        for (JsonNode event : jsonPayload) {
          recipientClientIds.add(event.get(RECIPIENT_CLIENT_ID).textValue());
        }
        return recipientClientIds.stream().allMatch(id -> id.equals(clientId));
      } else {
        JsonNode recipientClientIdNode = jsonPayload.get(RECIPIENT_CLIENT_ID);
        return (recipientClientIdNode != null && recipientClientIdNode.textValue() != null
            && recipientClientIdNode.textValue().equals(clientId));
      }
    } catch (JsonProcessingException e) {
      logger.error("Invalid event Payload: {}", e.getMessage());
      return false;
    }
  }

}
