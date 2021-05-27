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

import com.adobe.util.FeignUtil;
import com.adobe.ims.AccessToken;
import com.adobe.ims.ImsService;
import com.adobe.ims.JwtTokenBuilder;

public class ImsServiceImpl implements ImsService {

  private ImsApi imsApi;
  private JwtTokenBuilder jwtTokenBuilder;

  private ImsServiceImpl(final JwtTokenBuilder jwtTokenBuilder) {
    this.jwtTokenBuilder = jwtTokenBuilder;
    this.imsApi = FeignUtil.getBuilderWithFormEncoder()
        .target(ImsApi.class, jwtTokenBuilder.getImsUrl());
  }

  public static ImsService build(final JwtTokenBuilder jwtTokenBuilder) {
    return new ImsServiceImpl(jwtTokenBuilder);
  }

  @Override
  public AccessToken getJwtExchangeAccessToken() {
    return imsApi.getAccessToken(jwtTokenBuilder.getApiKey(),
        jwtTokenBuilder.getClientSecret(), jwtTokenBuilder.getJwtToken());
  }

}