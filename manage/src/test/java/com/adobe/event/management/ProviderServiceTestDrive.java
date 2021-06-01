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
package com.adobe.event.management;

import com.adobe.Workspace;
import com.adobe.event.management.model.Provider;
import com.adobe.ims.ImsService;
import com.adobe.ims.JWTAuthInterceptor;
import com.adobe.ims.model.AccessToken;
import com.adobe.util.FileUtil;
import com.adobe.util.PrivateKeyBuilder;
import feign.RequestInterceptor;
import java.security.PrivateKey;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProviderServiceTestDrive {

  private static final Logger logger = LoggerFactory.getLogger(ProviderServiceTestDrive.class);

  // use your own property file filePath or classpath and don't push back to git
  private static final String DEFAULT_TEST_DRIVE_PROPERTIES = "workspace.secret.properties";
  private static final String API_URL = "api_url";
  private static final String CONSUMER_ORG_ID = "consumer_org_id";
  public static final String PROVIDER_ID = "provider_id";

  public static void runTheReadmeFile() {
    PrivateKey privateKey = new PrivateKeyBuilder().systemEnv().build(); // [1]
    Workspace workspace = Workspace.builder()
        .systemEnv()
        .privateKey(privateKey)
        .build(); // [2]
    ImsService imsService = ImsService.builder().workspace(workspace).build(); // [3]

    // [1] Build your PrivateKey looking up the key indicated by you System Environment variables
    // [2] build your `Workspace` (a Java POJO representation of your `Adobe Developer Console` Workspace)
    //     looking up other System Environment variables.
    //     Note that our fluent workspace and private Key builders offers many ways to have your workspace configured,
    //     we are showing here the most concise
    // [3] build the Ims Service wrapper and have it use this workspace context

     RequestInterceptor authInterceptor = JWTAuthInterceptor.builder()
        .workspace(workspace)
        .build();

    ProviderService providerService = ProviderService.builder()
        .authInterceptor(authInterceptor) // [1]
        .consumerOrgId(workspace.getConsumerOrgId()) // [2]
        .build(); //
    Optional<Provider> provider = providerService.findById("someProviderId"); //[3]

   /*

   [1] build your ProviderService by passing a OpenFeign Authentication Request Interceptor
       This [Open Feign RequestInterceptor](https://github.com/OpenFeign/feign#request-interceptors)
       duty is to add :
      * an `Authorization` header with a valid `Bearer` access token
      * and a `x-api-key` header associated with the above token

   [2] set the consumerOrgId workspace context expected by the ProviderService
   [3] have this service retrieve one of your event provider by passing a provider id.

    */

  }

  /**
   * use your own property file filePath or classpath.
   * WARNING: don't push back to github as it contains many secrets.
   * We do provide a sample/template workspace.properties file in the
   * ./src/test/resources folder
   */
  private static final String DEFAULT_TEST_PROPERTIES = "workspace.secret.properties";

  public static void main(String[] args) {
    try {

      Properties prop =
          FileUtil.readPropertiesFromClassPath(
              (args != null && args.length > 0) ? args[0] : DEFAULT_TEST_DRIVE_PROPERTIES);

      PrivateKey privateKey = new PrivateKeyBuilder().properties(prop).build();

      Workspace workspace = Workspace.builder()
          .properties(prop)
          .privateKey(privateKey)
          .build();

      RequestInterceptor authInterceptor = JWTAuthInterceptor.builder()
          .workspace(workspace)
          .build();

      ProviderService providerService = ProviderService.builder()
          .authInterceptor(authInterceptor)
          .consumerOrgId(workspace.getConsumerOrgId())
          .url(prop.getProperty(API_URL))
          .build();

      List<Provider> providers = providerService.getProviders();
      logger.info("providers: {}", providers);

      Optional<Provider> provider1 = providerService.findById(prop.getProperty(PROVIDER_ID));
      logger.info("provider1: {}", provider1);

      Optional<Provider> provider2 = providerService.findById("notfound");
      logger.info("provider2: {}", provider2);

      System.exit(0);
    } catch (Exception e) {
      logger.error(e.getMessage(), e);
      System.exit(-1);
    }
  }



}
