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

import com.adobe.aio.event.management.RegistrationService;
import com.adobe.aio.ims.util.TestUtil;
import com.adobe.aio.workspace.Workspace;
import com.adobe.aio.event.management.model.EventsOfInterest;
import com.adobe.aio.event.management.model.Registration;
import com.adobe.aio.event.management.model.RegistrationInputModel;
import com.adobe.aio.util.FileUtil;
import com.adobe.aio.ims.util.PrivateKeyBuilder;

import java.security.PrivateKey;
import java.util.Optional;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FeignRegistrationServiceTestDrive {

  private static final Logger logger = LoggerFactory.getLogger(FeignRegistrationServiceTestDrive.class);

  public static final String SOME_PROVIDER_ID = "some_aio_provider_id";
  public static final String SOME_EVENT_CODE = "some_aio_event_code";

  public static void main(String[] args) {
    try {

      Workspace workspace = TestUtil.getTestWorkspaceBuilder().build();

      RegistrationService registrationService = RegistrationService.builder()
          .workspace(workspace) // [1]
          .url(TestUtil.getTestProperty(TestUtil.API_URL)) // you can omit this if you target prod
          .build(); //
      Optional<Registration> registration =
          registrationService.findById("someRegistrationId"); // [2]
      logger.info("someRegistration: {}", registration);

      Optional<Registration> created = registrationService.createRegistration(
          RegistrationInputModel.builder()
              .description("your registration description")
              .name("your registration name")
              .addEventsOfInterests(EventsOfInterest.builder()
                  .setEventCode(SOME_EVENT_CODE)
                  .setProviderId(SOME_PROVIDER_ID).build())
      );
      String createdId = created.get().getRegistrationId();
      logger.info("created: {}", created.get());
      registrationService.delete(createdId);
      logger.info("deleted: {}", createdId);

      System.exit(0);
    } catch (Exception e) {
      logger.error(e.getMessage(), e);
      System.exit(-1);
    }
  }


}
