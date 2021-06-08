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
import com.adobe.event.management.model.EventsOfInterest;
import com.adobe.event.management.model.Registration;
import com.adobe.event.management.model.RegistrationInputModel;
import com.adobe.ims.JWTAuthInterceptor;
import com.adobe.util.FileUtil;
import com.adobe.util.PrivateKeyBuilder;
import feign.RequestInterceptor;
import java.security.PrivateKey;
import java.util.Optional;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegistrationServiceTestDrive {

  private static final Logger logger = LoggerFactory.getLogger(RegistrationServiceTestDrive.class);

  // use your own property file filePath or classpath and don't push back to git
  private static final String DEFAULT_TEST_DRIVE_PROPERTIES = "workspace.secret.properties";
  private static final String API_URL = "api_url";

  public static final String PROVIDER_ID = "provider_id";
  public static final String EVENT_CODE = "event_code";

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

      RegistrationService registrationService = RegistrationService.builder()
          .authInterceptor(authInterceptor) // [1]
          .workspace(workspace) // [2]
          .url(prop.getProperty(API_URL)) // you can omit this if you target prod
          .build(); //
      Optional<Registration> registration =
          registrationService.findById("someRegistrationId"); // [3]
      logger.info("someRegistration: {}", registration);

      Optional<Registration> created = registrationService.createRegistration(
          RegistrationInputModel.builder()
              .description("aio-lib-java registration description")
              .name("aio-lib-java registration name")
              .addEventsOfInterests(EventsOfInterest.builder()
                  .setEventCode(prop.getProperty(EVENT_CODE))
                  .setProviderId(prop.getProperty(PROVIDER_ID)).build())
      );
      String createdId = created.get().getRegistrationId();
      logger.info("found created: {}", createdId);
      registrationService.delete(createdId);

      System.exit(0);
    } catch (Exception e) {
      logger.error(e.getMessage(), e);
      System.exit(-1);
    }
  }


}
