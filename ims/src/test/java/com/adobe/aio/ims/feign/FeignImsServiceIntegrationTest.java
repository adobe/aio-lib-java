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

import com.adobe.aio.exception.feign.IOUpstreamError;
import com.adobe.aio.ims.ImsService;
import com.adobe.aio.ims.model.AccessToken;
import com.adobe.aio.ims.util.TestUtil;
import com.adobe.aio.workspace.Workspace;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FeignImsServiceIntegrationTest {

  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  @Test
  public void getAndValidateJwtExchangeAccessToken() {
    Workspace workspace = TestUtil.getTestWorkspaceBuilder().build();
    ImsService imsService = ImsService.builder().workspace(workspace).build();
    AccessToken accessToken = imsService.getJwtExchangeAccessToken();
    logger.info("JWT Exchange token flow complete");
    Assert.assertNotNull(accessToken);
    Assert.assertNotNull(accessToken.getAccessToken());
    Assert.assertTrue(accessToken.getExpiresIn()>0);
    Assert.assertTrue(imsService.validateAccessToken(accessToken.getAccessToken()));
    logger.info("JWT Exchange access token validated");
  }

  @Test(expected = IOUpstreamError.class)
  public void getAndValidateJwtExchangeAccessTokenWithBadApiKey() {
    Workspace workspace = TestUtil.getTestWorkspaceBuilder().apiKey("bad_api_key").build();
    ImsService imsService = ImsService.builder().workspace(workspace).build();
    imsService.getJwtExchangeAccessToken();
  }

  @Test(expected = IOUpstreamError.class)
  public void getAndValidateJwtExchangeAccessTokenWithBadSecret() {
    Workspace workspace = TestUtil.getTestWorkspaceBuilder().clientSecret("bad_secret").build();
    ImsService imsService = ImsService.builder().workspace(workspace).build();
    imsService.getJwtExchangeAccessToken();
  }

  @Test(expected = IOUpstreamError.class)
  public void getAndValidateJwtExchangeAccessTokenWithBadTechAccount() {
    Workspace workspace = TestUtil.getTestWorkspaceBuilder().technicalAccountId("bad_tech_account_id@techacct.adobe.com").build();
    ImsService imsService = ImsService.builder().workspace(workspace).build();
    imsService.getJwtExchangeAccessToken();
  }

  @Test(expected = IllegalArgumentException.class)
  public void getAndValidateJwtExchangeAccessTokenWithMissingPrivateKey() {
    Workspace workspace = TestUtil.getTestWorkspaceBuilder().privateKey(null).build();
    ImsService imsService = ImsService.builder().workspace(workspace).build();
    imsService.getJwtExchangeAccessToken();
  }
}
