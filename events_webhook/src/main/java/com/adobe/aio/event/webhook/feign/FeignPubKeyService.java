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
package com.adobe.aio.event.webhook.feign;

import com.adobe.aio.event.webhook.api.PublicKeyCdnApi;
import com.adobe.aio.event.webhook.service.PubKeyService;
import com.adobe.aio.exception.AIOException;
import com.adobe.aio.util.feign.FeignUtil;

import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class FeignPubKeyService implements PubKeyService {

  private final PublicKeyCdnApi publicKeyCdnApi;

  public FeignPubKeyService(final String pubKeyCdnBaseUrl) {
    this.publicKeyCdnApi = FeignUtil.getBaseBuilder()
        .target(PublicKeyCdnApi.class, pubKeyCdnBaseUrl);
  }

  /**
   * it is recommended to cache this public key fetched from the adobe hosted CDN.
   * after downloading the public key set it in the cache with cache expiry of not more than 24h.
   * refer our public doc for the same - https://developer.adobe.com/events/docs/guides/#security-considerations
   *
   * @param pubKeyPath cdn path for fetching the public key
   * @return {@link PublicKey}
   */
  @Override
  public PublicKey getAioEventsPublicKey(String pubKeyPath) {
    try {
      String publicKeyPEM = publicKeyCdnApi.getPubKeyFromCDN(pubKeyPath);
      if (publicKeyPEM == null) {
        throw new IllegalStateException(String.format("No key found at specified path %s", pubKeyPath));
      }

      publicKeyPEM = publicKeyPEM.replace("-----BEGIN PUBLIC KEY-----", "")
          .replaceAll(System.lineSeparator(), "")
          .replace("-----END PUBLIC KEY-----", "");
      byte[] keyBytes = Base64.getDecoder().decode(publicKeyPEM);
      KeyFactory keyFactory = KeyFactory.getInstance("RSA");
      X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
      return keyFactory.generatePublic(keySpec);
    } catch (GeneralSecurityException e) {
      throw new AIOException("Error fetching public key from CDN path " + pubKeyPath
          + ". Reason -> " + e.getMessage(), e);
    }
  }

}
