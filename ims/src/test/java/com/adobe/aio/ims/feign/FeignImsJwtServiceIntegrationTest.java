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
import com.adobe.aio.ims.model.AccessToken;
import com.adobe.aio.util.WorkspaceUtil;
import com.adobe.aio.workspace.Workspace;
import feign.FeignException;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.*;

public class FeignImsJwtServiceIntegrationTest {

  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  @Test
  public void getAndValidateJwtExchangeAccessToken() {
    Workspace workspace = WorkspaceUtil.getSystemWorkspaceBuilder().build();
    ImsService imsService = ImsService.builder().workspace(workspace).build();
    AccessToken accessToken = imsService.getJwtExchangeAccessToken();
    logger.info("JWT Exchange token flow complete");
    assertNotNull(accessToken);
    assertNotNull(accessToken.getAccessToken());
    assertTrue(accessToken.getExpiresIn()>0);
    assertTrue(imsService.validateAccessToken(accessToken.getAccessToken()));
    logger.info("JWT Exchange access token validated");
  }

  @Test
  public void getAndValidateJwtExchangeAccessTokenWithBadApiKey() {
    Workspace workspace = WorkspaceUtil.getSystemWorkspaceBuilder().apiKey("bad_api_key").build();
    ImsService imsService = ImsService.builder().workspace(workspace).build();
    assertThrows(FeignException.BadRequest.class, imsService::getJwtExchangeAccessToken);
  }

  @Test
  public void getAndValidateJwtExchangeAccessTokenWithBadSecret() {
    Workspace workspace = WorkspaceUtil.getSystemWorkspaceBuilder().clientSecret("bad_secret").build();
    ImsService imsService = ImsService.builder().workspace(workspace).build();
    assertThrows(FeignException.BadRequest.class, imsService::getJwtExchangeAccessToken);
  }

  @Test
  public void getAndValidateJwtExchangeAccessTokenWithBadTechAccount() {
    Workspace workspace = WorkspaceUtil.getSystemWorkspaceBuilder().technicalAccountId("bad_tech_account_id@techacct.adobe.com").build();
    ImsService imsService = ImsService.builder().workspace(workspace).build();
    assertThrows(FeignException.BadRequest.class, imsService::getJwtExchangeAccessToken);
  }

  @Test
  public void getAndValidateJwtExchangeAccessTokenWithMissingPrivateKey() {
    Workspace workspace = WorkspaceUtil.getSystemWorkspaceBuilder().privateKey(null).build();
    assertThrows(IllegalStateException.class, () -> ImsService.builder().workspace(workspace).build());
  }
}
