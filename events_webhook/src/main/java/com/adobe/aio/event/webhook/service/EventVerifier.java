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

import static com.adobe.aio.cache.CaffeinCacheUtils.buildCacheWithExpiryAfterWrite;
import static com.adobe.aio.retrofit.RetrofitUtils.getRetrofitWithJacksonConverterFactory;
import static java.nio.charset.StandardCharsets.UTF_8;

import com.adobe.aio.exception.AIOException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.benmanes.caffeine.cache.Cache;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Map;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.validator.routines.UrlValidator;
import org.h2.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import retrofit2.Call;
import retrofit2.Response;


public class EventVerifier {

  private static Logger logger = LoggerFactory.getLogger(EventVerifier.class);

  private static final String ADOBE_IOEVENTS_SECURITY_DOMAIN = "https://static.adobeioevents.com";
  private static final Long CACHE_EXPIRY_IN_MINUTES = 1440L; // expiry of 24 hrs
  private static final Long CACHE_MAX_ENTRY_COUNT = 100L;
  public static final String ADOBE_IOEVENTS_DIGI_SIGN_1 = "x-adobe-digital-signature-1";
  public static final String ADOBE_IOEVENTS_DIGI_SIGN_2 = "x-adobe-digital-signature-2";
  public static final String ADOBE_IOEVENTS_PUB_KEY_1_PATH = "x-adobe-public-key1-path";
  public static final String ADOBE_IOEVENTS_PUB_KEY_2_PATH = "x-adobe-public-key2-path";

  private final Cache<String, String> pubKeyCache;
  private PubKeyService pubKeyService;

  public EventVerifier() {
    this.pubKeyCache = buildCacheWithExpiryAfterWrite("publicKeyCache",
        CACHE_EXPIRY_IN_MINUTES, CACHE_MAX_ENTRY_COUNT);
    this.pubKeyService = getRetrofitWithJacksonConverterFactory(ADOBE_IOEVENTS_SECURITY_DOMAIN, 60)
        .create(PubKeyService.class);
  }

  /**
   * Authenticate the event by checking the target recipient
   * and verifying the signatures
   * @param message - the event payload
   * @param clientId - recipient client id in the payload
   * @param headers - webhook request headers
   * @return boolean - TRUE if valid event else FALSE
   * @throws Exception
   */
  public boolean authenticateEvent(String message, String clientId,
      Map<String, String> headers) throws Exception {
    if(!isValidTargetRecipient(message, clientId)) {
      logger.error("target recipient {} is not valid for message {}", clientId, message);
      return false;
    }
    if (!verifyEventSignatures(message, headers)) {
      logger.error("signatures are not valid for message {}", message);
      return false;
    }
    return true;
  }

  private boolean verifyEventSignatures(String message,
      Map<String, String> headers) {
    String[] digitalSignatures = {headers.get(ADOBE_IOEVENTS_DIGI_SIGN_1),
        headers.get(ADOBE_IOEVENTS_DIGI_SIGN_2)};
    String[] pubKeyPaths = {headers.get(ADOBE_IOEVENTS_PUB_KEY_1_PATH),
        headers.get(ADOBE_IOEVENTS_PUB_KEY_2_PATH)};
    String publicKey1Url = ADOBE_IOEVENTS_SECURITY_DOMAIN + headers.get(ADOBE_IOEVENTS_PUB_KEY_1_PATH);
    String publicKey2Url = ADOBE_IOEVENTS_SECURITY_DOMAIN + headers.get(ADOBE_IOEVENTS_PUB_KEY_2_PATH);

    try {
      if (isValidUrl(publicKey1Url) && isValidUrl(publicKey2Url)) {
        return verifySignature(message, pubKeyPaths, digitalSignatures);
      }
    } catch (Exception e) {
      throw new AIOException("Error verifying signature for public keys " + publicKey1Url +
          " & " + publicKey2Url + ". Reason -> " + e.getMessage());
    }
    return false;
  }

  private boolean verifySignature(String message, String[] publicKeyPaths, String[] signatures)
      throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, SignatureException {
    byte[] data = message.getBytes(UTF_8);

    for (int i = 0; i < signatures.length; i++) {
      // signature generated at I/O Events side is Base64 encoded, so it must be decoded
      byte[] sign = Base64.decodeBase64(signatures[i]);
      Signature sig = Signature.getInstance("SHA256withRSA");
      sig.initVerify(getPublic(fetchPemEncodedPublicKey(publicKeyPaths[i])));
      sig.update(data);
      boolean result = sig.verify(sign);
      if (result) {
        return true;
      }
    }
    return false;
  }

  private boolean isValidTargetRecipient(String message, String clientId) {
    ObjectMapper mapper = new ObjectMapper();
    try {
      JsonNode jsonPayload = mapper.readTree(message);
      JsonNode recipientClientIdNode = jsonPayload.get("recipient_client_id");
      if (recipientClientIdNode != null) {
        return recipientClientIdNode.textValue().equals(clientId);
      }
    } catch (JsonProcessingException e) {
      throw new AIOException("error parsing the event payload during target recipient check..");
    }
    return false;
  }

  private PublicKey getPublic(String pubKey)
      throws NoSuchAlgorithmException, InvalidKeySpecException {
    String publicKeyPEM = pubKey
        .replace("-----BEGIN PUBLIC KEY-----", "")
        .replaceAll(System.lineSeparator(), "")
        .replace("-----END PUBLIC KEY-----", "");

    byte[] encoded = Base64.decodeBase64(publicKeyPEM);

    KeyFactory keyFactory = KeyFactory.getInstance("RSA");
    X509EncodedKeySpec keySpec = new X509EncodedKeySpec(encoded);
    return keyFactory.generatePublic(keySpec);
  }

  private String fetchPemEncodedPublicKey(String publicKeyPath) {
    return fetchKeyFromCacheOrApi(publicKeyPath);
  }

  private String fetchKeyFromCacheOrApi(String pubKeyPath) {
    String pubKeyFileName = getPublicKeyFileName(pubKeyPath);
    String pubKey = getKeyFromCache(pubKeyFileName);
    if (StringUtils.isNullOrEmpty(pubKey)) {
      pubKey = fetchKeyFromApiAndPutInCache(pubKeyPath, pubKeyFileName);
    }
    return pubKey;
  }

  private String fetchKeyFromApiAndPutInCache(String pubKeyPath, String pubKeyFileName) {
    try {
      logger.warn("public key {} not present in cache, fetching directly from the cdn url {}",
          pubKeyFileName, ADOBE_IOEVENTS_SECURITY_DOMAIN + pubKeyPath);
      String pubKey = "";
      Call<String> pubKeyFetchCall = pubKeyService.getPubKeyFromCDN(pubKeyPath);
      Response<String> response = pubKeyFetchCall.execute();
      if (response.isSuccessful()) {
        pubKey = response.body();
        pubKeyCache.put(pubKeyFileName, pubKey);
      }
      return pubKey;
    } catch (Exception e) {
      throw new AIOException("error fetching public key from CDN url"
          + ADOBE_IOEVENTS_SECURITY_DOMAIN + pubKeyPath + " due to " + e.getMessage());
    }
  }

  private String getKeyFromCache(String pubKeyFileNameAsKey) {
    String pubKey = pubKeyCache.getIfPresent(pubKeyFileNameAsKey);
    if (pubKey != null) {
      logger.debug("fetched key successfully for pub key path {} from cache", pubKeyFileNameAsKey);
    }
    return pubKey;
  }

  /**
   * Parses the pub key file name from the relative path
   *
   * @param pubKeyPath - relative path in the format /prod/keys/pub-key-voy5XEbWmT.pem
   * @return public key file name
   */
  String getPublicKeyFileName(String pubKeyPath) {
    return pubKeyPath.substring(pubKeyPath.lastIndexOf('/') + 1);
  }

  private boolean isValidUrl(String url) {
    return new UrlValidator().isValid(url);
  }
}
