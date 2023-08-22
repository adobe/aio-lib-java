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
package com.adobe.aio.event.publish.feign;

import com.adobe.aio.event.publish.PublishService;
import com.adobe.aio.event.publish.model.CloudEvent;
import com.adobe.aio.util.JacksonUtil;
import com.adobe.aio.util.WorkspaceUtil;
import com.adobe.aio.workspace.Workspace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FeignPublishServiceTestDrive {

  public static final String AIO_PROVIDER_ID = "aio_provider_id";
  public static final String AIO_EVENT_CODE = "aio_event_code";
  private static final Logger logger = LoggerFactory.getLogger(FeignPublishServiceTestDrive.class);
  private static final String AIO_PUBLISH_URL = "aio_publish_url";

  public static void main(String[] args) {
    try {
      Workspace workspace = WorkspaceUtil.getSystemWorkspaceBuilder().build();
      String providerId = WorkspaceUtil.getSystemProperty(AIO_PROVIDER_ID);
      String eventCode = WorkspaceUtil.getSystemProperty(AIO_EVENT_CODE);

      String eventDataPayload = "your event payload";
      //String eventDataPayload = "   { \"key\" : \"value\" } ";

      PublishService publishService = PublishService.builder()
          .workspace(workspace)
          .url(WorkspaceUtil.getSystemProperty(
              AIO_PUBLISH_URL)) // you can omit this if you target prod
          .build(); //
      CloudEvent cloudEvent = publishService.publishCloudEvent(providerId, eventCode,
          eventDataPayload);
      logger.info("published Cloud Event{}",
          JacksonUtil.DEFAULT_OBJECT_MAPPER.writeValueAsString(cloudEvent));

      // Adobe I/O Events Publishing API also allows the publication of simple/raw event json payload
      publishService.publishRawEvent(providerId, eventCode, eventDataPayload);
      logger.info("published Raw Event{}", eventDataPayload);

      System.exit(0);
    } catch (Exception e) {
      logger.error(e.getMessage(), e);
      System.exit(-1);
    }
  }


}
