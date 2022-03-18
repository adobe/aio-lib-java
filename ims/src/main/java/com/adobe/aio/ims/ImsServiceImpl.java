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
package com.adobe.aio.ims;

import com.adobe.aio.workspace.Workspace;
import com.adobe.aio.ims.api.ImsApi;
import com.adobe.aio.ims.model.AccessToken;
import com.adobe.aio.util.FeignUtil;

class ImsServiceImpl implements ImsService {

  public static final String ACCESS_TOKEN = "access_token";
  private final ImsApi imsApi;
  private final Workspace workspace;

  ImsServiceImpl(final Workspace workspace) {
    this.workspace = workspace;
    this.imsApi = FeignUtil.getBuilderWithFormEncoder()
        .target(ImsApi.class, workspace.getImsUrl());
  }

  @Override
  public AccessToken getJwtExchangeAccessToken() {
    workspace.validateJwtCredentialConfig();
    return imsApi.getAccessToken(workspace.getApiKey(),
        workspace.getClientSecret(), new JwtTokenBuilder(workspace).build());
  }

  @Override
  public Boolean validateAccessToken(String accessToken) {
    return imsApi.validateToken(ACCESS_TOKEN, workspace.getApiKey(), accessToken).getValid();
  }

}