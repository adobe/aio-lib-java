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
import com.adobe.aio.ims.util.PrivateKeyBuilder;
import feign.RequestInterceptor;
import java.security.PrivateKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FeignImsServiceTestDrive {

  private static final Logger logger = LoggerFactory.getLogger(FeignImsServiceTestDrive.class);

  public static void runTheReadmeFile() {

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
        .imsService(imsService)
        .apiKey(workspace.getApiKey())
        .build();

    // or you could also do
    RequestInterceptor authInterceptor2 = JWTAuthInterceptor.builder()
        .workspace(workspace)
        .build();
  }

  /**
   * use your own property file filePath or classpath. WARNING: don't push back to github as it
   * contains many secrets. We do provide a sample/template workspace.properties file in the
   * ./src/test/resources folder
   */
  private static final String DEFAULT_TEST_PROPERTIES = "workspace.secret.properties";

  public static void main(String[] args) {
    try {
      PrivateKey privateKey = new PrivateKeyBuilder().configPath(DEFAULT_TEST_PROPERTIES).build();

      Workspace workspace = Workspace.builder()
          .propertiesPath(DEFAULT_TEST_PROPERTIES)
          .privateKey(privateKey)
          .build();
      ImsService imsService = ImsService.builder().workspace(workspace).build();

      AccessToken accessToken = imsService.getJwtExchangeAccessToken();
      logger.info("accessToken: {}", accessToken.getAccessToken());
      logger.info(imsService.validateAccessToken(accessToken.getAccessToken()).toString());

      System.exit(0);
    } catch (Exception e) {
      logger.error(e.getMessage(), e);
      System.exit(-1);
    }
  }

}
