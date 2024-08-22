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

import com.adobe.aio.auth.OAuthContext;
import com.adobe.aio.ims.ImsService;
import com.adobe.aio.ims.model.AccessToken;
import com.adobe.aio.util.WorkspaceUtil;
import com.adobe.aio.workspace.Workspace;
import feign.FeignException;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.adobe.aio.util.WorkspaceUtil.DEFAULT_TEST_OAUTH_PROPERTIES;
import static org.junit.jupiter.api.Assertions.*;

public class FeignImsOAuthServiceIntegrationTest {

  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  @Test
  public void getOAuthAccessToken() {
    Workspace workspace = WorkspaceUtil.getSystemWorkspaceBuilder(DEFAULT_TEST_OAUTH_PROPERTIES).build();
    ImsService imsService = ImsService.builder().workspace(workspace).build();
    AccessToken accessToken = imsService.getOAuthAccessToken();
    logger.info("OAuth token received");
    assertNotNull(accessToken);
    assertNotNull(accessToken.getAccessToken());
    assertTrue(accessToken.getExpiresIn()>0);
    logger.info("OAuth access token validated");
  }

  @Test
  public void getOAuthAccessTokenWithBadApiKey() {
    Workspace workspace = WorkspaceUtil.getSystemWorkspaceBuilder(DEFAULT_TEST_OAUTH_PROPERTIES).apiKey("bad_api_key").build();
    ImsService imsService = ImsService.builder().workspace(workspace).build();
    assertThrows(FeignException.BadRequest.class, imsService::getOAuthAccessToken);
  }

  @Test
  public void getOAuthAccessTokenWithBadSecret() {
    OAuthContext context = OAuthContext.builder().propertiesPath(DEFAULT_TEST_OAUTH_PROPERTIES).clientSecret("bad_secret").build();
    Workspace workspace = WorkspaceUtil.getSystemWorkspaceBuilder(DEFAULT_TEST_OAUTH_PROPERTIES).authContext(context).build();
    ImsService imsService = ImsService.builder().workspace(workspace).build();
    assertThrows(FeignException.BadRequest.class, imsService::getOAuthAccessToken);
  }

}
