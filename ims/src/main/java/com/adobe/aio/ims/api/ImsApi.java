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
package com.adobe.aio.ims.api;

import com.adobe.aio.ims.model.AccessToken;
import com.adobe.aio.ims.model.TokenValidation;
import feign.Body;
import feign.Headers;
import feign.Param;
import feign.RequestLine;

public interface ImsApi {

  @RequestLine("POST /ims/token/v3?client_id={client_id}")
  @Headers("Content-Type: application/x-www-form-urlencoded")
  @Body("client_secret={client_secret}&grant_type=client_credentials&scope={scopes}")
  AccessToken getOAuthAccessToken(
      @Param("client_id") String clientId,
      @Param("client_secret") String clientSecret,
      @Param("scopes") String scopes);

}
