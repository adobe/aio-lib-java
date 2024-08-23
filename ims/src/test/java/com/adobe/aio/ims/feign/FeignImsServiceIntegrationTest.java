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

import com.adobe.aio.auth.Context;
import com.adobe.aio.ims.ImsService;
import com.adobe.aio.ims.model.AccessToken;
import com.adobe.aio.util.WorkspaceUtil;
import com.adobe.aio.workspace.Workspace;
import feign.FeignException;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import static com.adobe.aio.util.WorkspaceUtil.DEFAULT_TEST_PROPERTIES;
import static com.adobe.aio.workspace.Workspace.API_KEY;
import static org.junit.jupiter.api.Assertions.*;

public class FeignImsServiceIntegrationTest {

  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  @Test
  public void getAccessToken() {
    Workspace workspace = WorkspaceUtil.getSystemWorkspaceBuilder().build();
    ImsService imsService = ImsService.builder().workspace(workspace).build();
    AccessToken accessToken = imsService.getAccessToken();
    assertNotNull(accessToken);
    assertNotNull(accessToken.getAccessToken());
    assertTrue(accessToken.getExpiresIn() > 0);
    logger.info("retrieved an access Token");
    if (workspace.isAuthJWT()) {
      assertTrue(imsService.validateAccessToken(accessToken.getAccessToken()));
      logger.info("JWT Exchange access token validated");
    }
  }

  @Test
  public void getAccessTokenWithBadApiKey() {
    Map<String,String> workspaceConfig = WorkspaceUtil.getSystemWorkspaceConfig(DEFAULT_TEST_PROPERTIES);
    workspaceConfig.put(API_KEY, "bad_api_key");
    Workspace workspace = WorkspaceUtil.getWorkspaceBuilder(workspaceConfig).build();
    ImsService imsService = ImsService.builder().workspace(workspace).build();
    assertThrows(FeignException.BadRequest.class, imsService::getAccessToken);
  }

  @Test
  public void getAccessTokenWithBadSecret() {
    Map<String,String> workspaceConfig = WorkspaceUtil.getSystemWorkspaceConfig(DEFAULT_TEST_PROPERTIES);
    workspaceConfig.put(Context.CLIENT_SECRET, "bad_secret");
    Workspace workspace = WorkspaceUtil.getWorkspaceBuilder(workspaceConfig).build();
    ImsService imsService = ImsService.builder().workspace(workspace).build();
    assertThrows(FeignException.BadRequest.class, imsService::getAccessToken);
  }

  @Test
  public void buildImsServiceWithMissingAuth() {
    Workspace workspace = WorkspaceUtil.getSystemWorkspaceBuilder().authContext(null).build();
    assertThrows(IllegalStateException.class, () -> ImsService.builder().workspace(workspace).build());
  }

}
