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
import com.adobe.aio.util.WorkspaceUtil;
import com.adobe.aio.workspace.Workspace;
import com.adobe.aio.ims.model.AccessToken;
import com.adobe.aio.ims.util.PrivateKeyBuilder;
import feign.RequestInterceptor;
import java.security.PrivateKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FeignImsServiceTestDrive {

  private static final Logger logger = LoggerFactory.getLogger(FeignImsServiceTestDrive.class);

  public static void runTheReadmeFileUsingOAuth() {
    Workspace workspace = Workspace.builder()
            .systemEnv()
            .build(); // [1]
    ImsService imsService = ImsService.builder().workspace(workspace).build(); // [2]

    AccessToken accessToken = imsService.getOAuthAccessToken(); // [3]

    // [1] build your `Workspace` (a Java POJO representation of your `Adobe Developer Console` Workspace)
    //     looking up other System Environment variables.
    //     Note that our fluent workspace and private Key builders offers many ways to have your workspace configured,
    //     we are showing here the most concise
    // [2] build the Ims Service wrapper and have it use this workspace context
    // [3] use this service to retrieve an OAuth access token

    // here is one way you can build the related IMS Feign OAuth Interceptor
    RequestInterceptor authInterceptor = OAuthInterceptor.builder()
            .workspace(workspace)
            .build();
  }

  public static void runTheReadmeFileUsingJWT() {
    PrivateKey privateKey = new PrivateKeyBuilder().systemEnv().build(); // [1]
    Workspace workspace = Workspace.builder()
        .systemEnv()
        .privateKey(privateKey)
        .build(); // [2]
    ImsService imsService = ImsService.builder().workspace(workspace).build(); // [3]

    AccessToken accessToken = imsService.getJwtExchangeAccessToken(); // [4]

    // [1] Build your PrivateKey looking up the key indicated by you System Environment variables
    // [2] build your `Workspace` (a Java POJO representation of your `Adobe Developer Console` Workspace)
    //     looking up other System Environment variables.
    //     Note that our fluent workspace and private Key builders offers many ways to have your workspace configured,
    //     we are showing here the most concise
    // [3] build the Ims Service wrapper and have it use this workspace context
    // [4] use this service to retrieve an access token using a jwt exchange token flow

    // here is one way you can build the related IMS Feign JWT Auth Interceptor
    RequestInterceptor authInterceptor = JWTAuthInterceptor.builder()
        .workspace(workspace)
        .build();
  }



  public static void main(String[] args) {
    try {
      Workspace jwtWorkspace = WorkspaceUtil.getSystemWorkspaceBuilder(WorkspaceUtil.DEFAULT_TEST_PROPERTIES).build();

      AccessToken accessToken = ImsService.builder().workspace(jwtWorkspace).build().getJwtExchangeAccessToken();
      logger.info("JWT accessToken: {}", accessToken);

      Workspace oAuthWorkspace = WorkspaceUtil.getSystemWorkspaceBuilder(WorkspaceUtil.DEFAULT_TEST_OAUTH_PROPERTIES).build();
      accessToken = ImsService.builder().workspace(oAuthWorkspace).build().getOAuthAccessToken();
      logger.info("OAuth accessToken: {}", accessToken);

      System.exit(0);
    } catch (Exception e) {
      logger.error(e.getMessage(), e);
      System.exit(-1);
    }
  }

}
