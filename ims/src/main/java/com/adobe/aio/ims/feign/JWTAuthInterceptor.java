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
package com.adobe.aio.ims.feign;

import com.adobe.aio.ims.ImsService;
import com.adobe.aio.workspace.Workspace;
import com.adobe.aio.ims.model.AccessToken;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.apache.commons.lang3.StringUtils;

import static com.adobe.aio.util.Constants.*;

public class JWTAuthInterceptor implements RequestInterceptor {

  private volatile Long expirationTimeMillis;
  private volatile AccessToken accessToken;

  private final ImsService imsService;
  private final String apiKey;
  private final String imsOrgId;

  private JWTAuthInterceptor(final ImsService imsService, final String apiKey, final String imsOrgId) {
    this.imsService = imsService;
    this.apiKey = apiKey;
    this.imsOrgId = imsOrgId;
  }

  @Override
  public void apply(RequestTemplate requestTemplate) {
    applyAuthorization(requestTemplate);
    applyApiKey(requestTemplate);
    applyImsOrg(requestTemplate);
  }

  public boolean isUp(){
    return imsService.validateAccessToken(this.getAccessToken());
  }

  private void applyAuthorization(RequestTemplate requestTemplate) {
    // If the request already have an authorization
    if (requestTemplate.headers().containsKey(AUTHORIZATION_HEADER)) {
      return;
    }
    // If first time or of expired, get the token
    if (getAccessToken() != null) {
      requestTemplate.header(AUTHORIZATION_HEADER, BEARER_PREFIX + getAccessToken());
    }
  }

  private void applyApiKey(RequestTemplate requestTemplate) {
    if (!requestTemplate.headers().containsKey(API_KEY_HEADER) && !StringUtils.isEmpty(apiKey)) {
      requestTemplate.header(API_KEY_HEADER, apiKey);
    }
  }
  
  private void applyImsOrg(RequestTemplate requestTemplate) {
    if (!requestTemplate.headers().containsKey(IMS_ORG_HEADER) && !StringUtils.isEmpty(imsOrgId)) {
      requestTemplate.header(IMS_ORG_HEADER, imsOrgId);
    }
  }
  
  private synchronized void updateAccessToken() {
    this.accessToken = imsService.getJwtExchangeAccessToken();
    this.expirationTimeMillis = System.currentTimeMillis() + accessToken.getExpiresIn();
    // throw RetryableException and implement Feign Retry ?
  }

  private synchronized String getAccessToken() {
    if (expirationTimeMillis == null || System.currentTimeMillis() >= expirationTimeMillis) {
      updateAccessToken();
    }
    return this.accessToken.getAccessToken();
  }

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {

    private ImsService imsService;
    private String apiKey;
    private String imsOrgId;

    private Builder() {
    }

    public Builder workspace(Workspace workspace) {
      this.imsService = ImsService.builder().workspace(workspace).build();
      this.apiKey = workspace.getApiKey();
      return this;
    }

    public Builder imsService(ImsService imsService) {
      this.imsService = imsService;
      return this;
    }

    public Builder apiKey(String apiKey) {
      this.apiKey = apiKey;
      return this;
    }
    
    public Builder imsOrg(String imsOrgId) {
      this.imsOrgId = imsOrgId;
      return this;
    }

    public JWTAuthInterceptor build() {
      return new JWTAuthInterceptor(imsService, apiKey, imsOrgId);
    }
  }

}
