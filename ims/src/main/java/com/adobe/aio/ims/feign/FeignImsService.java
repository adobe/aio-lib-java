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

import com.adobe.aio.auth.JwtContext;
import com.adobe.aio.ims.ImsService;
import com.adobe.aio.ims.JwtTokenBuilder;
import com.adobe.aio.workspace.Workspace;
import com.adobe.aio.ims.api.ImsApi;
import com.adobe.aio.ims.model.AccessToken;
import com.adobe.aio.util.feign.FeignUtil;

public class FeignImsService implements ImsService {

  public static final String ACCESS_TOKEN = "access_token";
  private final ImsApi imsApi;
  private final Workspace workspace;

  public FeignImsService(final Workspace workspace) {
    this.workspace = workspace;
    this.imsApi = FeignUtil.getBuilderWithFormEncoder().target(ImsApi.class, workspace.getImsUrl());
  }

  @Override
  public AccessToken getJwtExchangeAccessToken() {
    if (!(workspace.getAuthContext() instanceof JwtContext)) {
      throw new IllegalStateException("AuthContext in workspace not of type `JwtContext`.");
    }

    JwtContext context = (JwtContext) workspace.getAuthContext();
    context.validate();

    JwtTokenBuilder builder = new JwtTokenBuilder(workspace);
    String token = builder.build();
    return imsApi.getAccessToken(workspace.getApiKey(), context.getClientSecret(), token);
  }

  @Override
  public boolean validateAccessToken(String accessToken) {
    if (!(workspace.getAuthContext() instanceof JwtContext)) {
      throw new IllegalStateException("AuthContext in workspace not of type `JwtContext`.");
    }

    return imsApi.validateToken(ACCESS_TOKEN, workspace.getApiKey(), accessToken).getValid();
  }

}
