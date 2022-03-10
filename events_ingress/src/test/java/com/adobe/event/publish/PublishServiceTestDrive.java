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
package com.adobe.event.publish;

import com.adobe.Workspace;
import com.adobe.event.publish.model.CloudEvent;
import com.adobe.ims.JWTAuthInterceptor;
import com.adobe.util.FileUtil;
import com.adobe.util.JacksonUtil;
import com.adobe.ims.util.PrivateKeyBuilder;
import feign.RequestInterceptor;
import java.security.PrivateKey;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PublishServiceTestDrive {

  private static final Logger logger = LoggerFactory.getLogger(PublishServiceTestDrive.class);

  // use your own property file filePath or classpath and don't push back to git
  private static final String DEFAULT_TEST_DRIVE_PROPERTIES = "workspace.secret.properties";
  private static final String AIO_PUBLISH_URL = "aio_publish_url";

  public static final String AIO_PROVIDER_ID = "aio_provider_id";
  public static final String AIO_EVENT_CODE = "aio_event_code";

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

      String eventDataPayload =  "your event payload";
      //String eventDataPayload = "   { \"key\" : \"value\" } ";

      PublishService publishService = PublishService.builder()
          .authInterceptor(authInterceptor) // [1]
          .url(prop.getProperty(AIO_PUBLISH_URL)) // you can omit this if you target prod
          .build(); //
      CloudEvent cloudEvent = publishService.publishCloudEvent(
          prop.getProperty(AIO_PROVIDER_ID),
          prop.getProperty(AIO_EVENT_CODE),
          eventDataPayload);
      logger.info("published Cloud Event{}", JacksonUtil.DEFAULT_OBJECT_MAPPER.writeValueAsString(cloudEvent));

      // Adobe I/O Events Publishing API also allows the publication of simple/raw event json payload
      publishService.publishRawEvent(prop.getProperty(AIO_PROVIDER_ID),
          prop.getProperty(AIO_EVENT_CODE),
          eventDataPayload);
      logger.info("published Raw Event{}",eventDataPayload);

      System.exit(0);
    } catch (Exception e) {
      logger.error(e.getMessage(), e);
      System.exit(-1);
    }
  }


}
