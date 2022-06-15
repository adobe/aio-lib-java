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
package com.adobe.aio.event.management.feign;

import com.adobe.aio.event.management.ProviderService;
import com.adobe.aio.event.management.model.EventMetadata;
import com.adobe.aio.event.management.model.Provider;
import com.adobe.aio.event.management.model.ProviderInputModel;
import com.adobe.aio.ims.util.TestUtil;
import com.adobe.aio.workspace.Workspace;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FeignProviderServiceTestDrive {

  private static final Logger logger = LoggerFactory.getLogger(FeignProviderServiceTestDrive.class);

  public static void main(String[] args) {
    try {
      Workspace workspace = TestUtil.getTestWorkspaceBuilder().build();

      ProviderService providerService = ProviderService.builder()
          .workspace(workspace) // [1]
          .url(TestUtil.getTestProperty(TestUtil.API_URL)) // you can omit this if you target prod
          .build();
      Optional<Provider> provider = providerService.findProviderById("someProviderId"); // [2]
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
          .info("added EventMetadata :{}",
              providerService.createEventMetadata(providerId, eventMetadata1));

      Optional<Provider> aboutToBeDeleted = providerService.findProviderById(created.get().getId());
      logger.info("aboutToBeDeleted: {}", aboutToBeDeleted);

      providerService.deleteProvider(aboutToBeDeleted.get().getId());
      logger.info("deleted: {}", aboutToBeDeleted.get().getId());

      System.exit(0);
    } catch (Exception e) {
      logger.error(e.getMessage(), e);
      System.exit(-1);
    }
  }


}
