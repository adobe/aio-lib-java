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

import com.adobe.event.management.feign.ProviderServiceImpl;
import com.adobe.event.management.model.Provider;
import com.adobe.ims.JwtTokenBuilder;
import com.adobe.ims.feign.JWTAuthInterceptor;
import com.adobe.util.FileUtil;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProviderServiceTestDrive {

  private static final Logger logger = LoggerFactory.getLogger(ProviderServiceTestDrive.class);

  // use your own property file filePath or classpath and don't push back to git
  private static final String DEFAULT_TEST_DRIVE_PROPERTIES = "management.secret.properties";
  private static final String API_URL = "api_url";
  private static final String CONSUMER_ORG_ID = "consumer_org_id";
  public static final String PROVIDER_ID = "provider_id";

  public static void main(String[] args) {
    try {
      Properties prop =
          FileUtil.readPropertiesFromClassPath(
              (args != null && args.length > 0) ? args[0] : DEFAULT_TEST_DRIVE_PROPERTIES);

      JWTAuthInterceptor jwtAuthInterceptor = new JWTAuthInterceptor(JwtTokenBuilder.build(prop));

      ProviderService providerService = ProviderServiceImpl
          .build(jwtAuthInterceptor, prop.getProperty(API_URL));

      String consumerOrgId = prop.getProperty(CONSUMER_ORG_ID);
      List<Provider> providers = providerService.getProviders(consumerOrgId);
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
