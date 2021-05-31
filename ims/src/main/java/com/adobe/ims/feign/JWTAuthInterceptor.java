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
package com.adobe.ims.feign;

import static com.adobe.util.Constants.API_KEY_HEADER;
import static com.adobe.util.Constants.AUTHORIZATION_HEADER;
import static com.adobe.util.Constants.BEARER_PREFIX;

import com.adobe.ims.ImsService;
import com.adobe.ims.JwtTokenBuilder;
import com.adobe.ims.model.AccessToken;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.apache.commons.lang3.StringUtils;

public class JWTAuthInterceptor implements RequestInterceptor {

  private volatile Long expirationTimeMillis;
  private volatile AccessToken accessToken;

  private final ImsService imsService;
  private final String apiKey;

  public JWTAuthInterceptor(JwtTokenBuilder jwtTokenBuilder){
   this(ImsServiceImpl.build(jwtTokenBuilder), jwtTokenBuilder.getApiKey());
  }

  public JWTAuthInterceptor(final ImsService imsService, final String apiKey) {
    this.imsService = imsService;
    this.apiKey = apiKey;
  }

  @Override
  public void apply(RequestTemplate requestTemplate) {
    // If the request already have an authorization
    if (requestTemplate.headers().containsKey(AUTHORIZATION_HEADER)) {
      return;
    }
    // If first time or of expired, get the token
    else if (expirationTimeMillis == null || System.currentTimeMillis() >= expirationTimeMillis) {
      updateAccessToken();
    }
    if (getAccessToken() != null) {
      requestTemplate.header(AUTHORIZATION_HEADER, BEARER_PREFIX + getAccessToken());
    }
    if (requestTemplate.headers().containsKey(API_KEY_HEADER)) {
      return;
    } else if (!StringUtils.isEmpty(apiKey)) {
      requestTemplate.header(API_KEY_HEADER, apiKey);
    }

  }

  private synchronized void updateAccessToken() {
    this.accessToken = imsService.getJwtExchangeAccessToken();
    this.expirationTimeMillis = System.currentTimeMillis() + accessToken.getExpiresIn();
    // throw RetryableException and implement Feign Retry ?
  }

  private synchronized String getAccessToken() {
    return this.accessToken.getAccessToken();
  }

}
