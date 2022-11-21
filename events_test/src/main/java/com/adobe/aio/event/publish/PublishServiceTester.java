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
package com.adobe.aio.event.publish;

import static com.adobe.aio.event.publish.model.CloudEvent.SPEC_VERSION;

import com.adobe.aio.event.publish.model.CloudEvent;
import com.adobe.aio.util.WorkspaceUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.UUID;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PublishServiceTester {

  protected final Logger logger = LoggerFactory.getLogger(this.getClass());

  public static final String DATA_EVENT_ID_NODE = "data_event_id";

  private final PublishService publishService;

  public PublishServiceTester(){
    publishService = PublishService.builder()
        .workspace(WorkspaceUtil.getSystemWorkspaceBuilder().build())
        .url(WorkspaceUtil.getSystemProperty(WorkspaceUtil.PUBLISH_URL))
        .build();
  }

  public String publishCloudEvent(String providerId, String eventCode) {
    try {
    String eventId = UUID.randomUUID().toString();
    CloudEvent cloudEvent = publishService.publishCloudEvent(
          providerId, eventCode, eventId, getEventDataNode(eventId));
      logger.info("Published CloudEvent: {}", cloudEvent);
      Assert.assertEquals(eventId, cloudEvent.getId());
      Assert.assertEquals(CloudEvent.SOURCE_URN_PREFIX + providerId, cloudEvent.getSource());
      Assert.assertEquals(eventCode, cloudEvent.getType());
      Assert.assertEquals(eventId, cloudEvent.getData().get(DATA_EVENT_ID_NODE).asText());
      Assert.assertEquals(SPEC_VERSION, cloudEvent.getSpecVersion());
      Assert.assertEquals("application/json", cloudEvent.getDataContentType());
      return eventId;
    } catch (JsonProcessingException e) {
     Assert.fail("publishService.publishCloudEvent failed with "+e.getMessage());
     return null;
    }
  }

  public String publishRawEvent(String providerId, String eventCode) {
    String eventId = UUID.randomUUID().toString();
    String rawEventPayload = getEventDataNode(eventId);
    publishService.publishRawEvent(providerId, eventCode, rawEventPayload);
    logger.info("Published Raw Event: {}", rawEventPayload);
    return eventId;
  }

  public static String getEventDataNode(String eventId) {
    return "{\"" + DATA_EVENT_ID_NODE + "\" : \"" + eventId + "\"}";
  }

}
