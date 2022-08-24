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
import com.adobe.aio.util.feign.FeignUtil;

public class FeignPubKeyService implements PubKeyService {

  private final PublicKeyCdnApi publicKeyCdnApi;

  public FeignPubKeyService(final String pubKeyCdnBaseUrl) {
    this.publicKeyCdnApi = FeignUtil.getBaseBuilder()
        .target(PublicKeyCdnApi.class, pubKeyCdnBaseUrl);
  }

  @Override
  public String getPubKeyFromCDN(String pubKeyPath) {
    String pubKey = publicKeyCdnApi.getPubKeyFromCDN(pubKeyPath);
    return pubKey;
  }
}
