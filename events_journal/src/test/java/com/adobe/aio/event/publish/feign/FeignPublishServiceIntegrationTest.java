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

import static com.adobe.aio.event.management.feign.FeignProviderServiceIntegrationTest.TEST_EVENT_CODE;
import static com.adobe.aio.event.publish.model.CloudEvent.SPEC_VERSION;

import com.adobe.aio.event.journal.JournalService;
import com.adobe.aio.event.journal.model.Event;
import com.adobe.aio.event.journal.model.JournalEntry;
import com.adobe.aio.event.management.ProviderService;
import com.adobe.aio.event.management.RegistrationService;
import com.adobe.aio.event.management.feign.FeignProviderServiceIntegrationTest;
import com.adobe.aio.event.management.feign.FeignRegistrationServiceIntegrationTest;
import com.adobe.aio.event.management.model.Provider;
import com.adobe.aio.event.management.model.Registration;
import com.adobe.aio.event.publish.PublishService;
import com.adobe.aio.event.publish.model.CloudEvent;
import com.adobe.aio.util.WorkspaceUtil;
import com.adobe.aio.workspace.Workspace;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.UUID;
import java.util.function.BiPredicate;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FeignPublishServiceIntegrationTest {

  private static final Logger logger = LoggerFactory.getLogger(
      FeignPublishServiceIntegrationTest.class);

  public static final String RAW_EVENT_ID = "rawEventId";

  private Workspace workspace;
  private ProviderService providerService;
  private RegistrationService registrationService;
  private PublishService publishService;

  public static String publishCloudEvent(PublishService publishService, String providerId,
      String eventCode) {
    try {
    String eventId = UUID.randomUUID().toString();
    String eventData = getRawDataEvent(eventId);
    CloudEvent cloudEvent = publishService.publishCloudEvent(
          providerId, eventCode, eventId, eventData);
      logger.info("Published CloudEvent: {}", cloudEvent);
      Assert.assertEquals(eventId, cloudEvent.getId());
      Assert.assertEquals(CloudEvent.SOURCE_URN_PREFIX + providerId, cloudEvent.getSource());
      Assert.assertEquals(TEST_EVENT_CODE, cloudEvent.getType());
      Assert.assertEquals(eventId, cloudEvent.getData().get(RAW_EVENT_ID).asText());
      Assert.assertEquals(SPEC_VERSION, cloudEvent.getSpecVersion());
      Assert.assertEquals("application/json", cloudEvent.getDataContentType());
      return eventId;
    } catch (JsonProcessingException e) {
     Assert.fail("publishService.publishCloudEvent failed with "+e.getMessage());
     return null;
    }
  }

  public static String publishRawEvent(PublishService publishService, String providerId,
      String eventCode) {
    String eventId = UUID.randomUUID().toString();
    String rawEventPayload = getRawDataEvent(eventId);
    publishService.publishRawEvent(providerId, eventCode, rawEventPayload);
    logger.info("Published Raw Event: {}", rawEventPayload);
    return eventId;
  }

  private static String getRawDataEvent(String eventId) {
    return "{\"" + RAW_EVENT_ID + "\" : \"" + eventId + "\"}";
  }

  @Before
  public void setUp() {
    workspace = WorkspaceUtil.getSystemWorkspaceBuilder().build();
    registrationService = RegistrationService.builder()
        .workspace(workspace)
        .url(WorkspaceUtil.getSystemProperty(WorkspaceUtil.API_URL))
        .build();
    providerService = ProviderService.builder()
        .workspace(workspace)
        .url(WorkspaceUtil.getSystemProperty(WorkspaceUtil.API_URL))
        .build();
    publishService = PublishService.builder()
        .workspace(workspace)
        .url(WorkspaceUtil.getSystemProperty(WorkspaceUtil.PUBLISH_URL))
        .build();

  }

  @Test(expected = IllegalArgumentException.class)
  public void missingWorkspace() {
    PublishService publishService = PublishService.builder()
        .workspace(null)
        .build();
  }

  @Test
  public void publishEventTest() {
    Provider provider = FeignProviderServiceIntegrationTest.createTestProvider(providerService);
    Registration registration = FeignRegistrationServiceIntegrationTest.createRegistration(
        registrationService, provider.getId());

    String cloudEventId = publishCloudEvent(publishService, provider.getId(), TEST_EVENT_CODE);
    String rawEventId = publishRawEvent(publishService, provider.getId(), TEST_EVENT_CODE);

    // we want to clean up the provider and registration even if the journal polling failed.
    FeignRegistrationServiceIntegrationTest.deleteRegistration(registrationService,
        registration.getRegistrationId());
    FeignProviderServiceIntegrationTest.deleteProvider(providerService, provider.getId());
  }

}
