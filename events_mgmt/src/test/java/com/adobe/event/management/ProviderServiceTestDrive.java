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
import com.adobe.event.management.model.EventMetadata;
import com.adobe.event.management.model.Provider;
import com.adobe.event.management.model.ProviderInputModel;
import com.adobe.ims.JWTAuthInterceptor;
import com.adobe.util.FileUtil;
import com.adobe.ims.util.PrivateKeyBuilder;
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
  private static final String API_URL = "aio_api_url";

  /**
   * use your own property file filePath or classpath. WARNING: don't push back to github as it
   * contains many secrets. We do provide a sample/template workspace.properties file in the
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
          .authInterceptor(authInterceptor) // [1]
          .workspace(workspace) // [2]
          .url(prop.getProperty(API_URL)) // you can omit this if you target prod
          .build(); //
      Optional<Provider> provider = providerService.findProviderById("someProviderId"); //[3]
      logger.info("someProvider: {}", provider);

      List<Provider> providers = providerService.getProviders();
      logger.info("providers: {}", providers);

      Optional<Provider> provider2 = providerService.findProviderById("notfound");
      logger.info("provider2: {}", provider2);

      ProviderInputModel providerCreateModel = ProviderInputModel.builder()
          .label("aio-lib-java test drive provider label")
          .description("aio-lib-java test drive provider description")
          .docsUrl(
              "https://github.com/adobe/aio-lib-java/blob/main/events_mgmt/README.md#providerservice-test-drive")
          .build();
      Optional<Provider> created = providerService.createProvider(providerCreateModel);
      logger.info("created: {}", created);
      String providerId = created.get().getId();

      EventMetadata eventMetadata1 = EventMetadata.builder()
          .eventCode("com.adobe.aio-java-lib.test")
          .description("aio-java-lib Test Drive Event")
          .build();
      logger
          .info("added EventMetadata :{}", providerService.createEventMetadata(providerId, eventMetadata1));

      Optional<Provider> aboutToBeDeleted = providerService.findProviderById(created.get().getId());
      logger.info("aboutToBeDeleted: {}", aboutToBeDeleted);

      providerService.deleteProvider(aboutToBeDeleted.get().getId());
      logger.info("deleted: {}", aboutToBeDeleted.get().getId());
      logger.info("ProviderServiceTestDrive completed successfully.");
      System.exit(0);
    } catch (Exception e) {
      logger.error(e.getMessage(), e);
      System.exit(-1);
    }
  }


}
