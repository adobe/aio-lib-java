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

import com.adobe.aio.ims.feign.AuthInterceptor;
import com.adobe.aio.util.WorkspaceUtil;
import com.adobe.aio.workspace.Workspace;
import feign.RequestInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ImsServiceTestDrive {

  private static final Logger logger = LoggerFactory.getLogger(ImsServiceTestDrive.class);

  public static void main(String[] args) {
    try {
      Workspace workspace = WorkspaceUtil.getSystemWorkspaceBuilder().build(); // [1]
      ImsService imsService = ImsService.builder().workspace(workspace).build(); // [2]
      logger.info("accessToken: {}", imsService.getAccessToken()); // [3]

      // [1] build your `Workspace` (a Java POJO representation of your `Adobe Developer Console` Workspace)
      //     looking up other System Environment variables.
      //     Note that our fluent workspace and private Key builders offers many ways to have your workspace configured,
      //     we are showing here the most concise
      // [2] build the Ims Service wrapper and have it use this workspace context
      // [3] use this service to retrieve an OAuth access token

      // here is one way you can build the related Adobe IMS Auth Interceptor
      RequestInterceptor authInterceptor = AuthInterceptor.builder()
              .workspace(workspace)
              .build();

      System.exit(0);
    } catch (Exception e) {
      logger.error(e.getMessage(), e);
      System.exit(-1);
    }
  }

}
