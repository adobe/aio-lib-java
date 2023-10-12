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

import java.util.UUID;
import org.apache.commons.lang3.StringUtils;

import com.adobe.aio.event.management.ProviderServiceTester;
import com.adobe.aio.event.management.RegistrationServiceTester;
import org.junit.jupiter.api.Test;

import static com.adobe.aio.event.management.ProviderServiceIntegrationTest.*;
import static com.adobe.aio.event.management.RegistrationServiceIntegrationTest.*;

import static com.adobe.aio.event.util.DataNodeUtil.getEventDataNode;
import static org.junit.jupiter.api.Assertions.*;

public class PublishServiceIntegrationTest extends PublishServiceTester {

  private final ProviderServiceTester providerServiceTester;
  private final RegistrationServiceTester registrationServiceTester;

  public PublishServiceIntegrationTest() {
    super();
    providerServiceTester = new ProviderServiceTester();
    registrationServiceTester = new RegistrationServiceTester();
  }

  @Test
  public void missingWorkspace() {
    assertThrows(IllegalArgumentException.class, () -> PublishService.builder().workspace(null).build());
  }

  @Test
  public void publishEventTest() {
    String providerId = null;
    String registrationId = null;
    try {
      providerId = providerServiceTester.createOrUpdateProvider(TEST_EVENT_PROVIDER_LABEL,
          TEST_EVENT_CODE).getId();
      registrationId = registrationServiceTester.createJournalRegistration(
          TEST_REGISTRATION_NAME, providerId, TEST_EVENT_CODE).getRegistrationId();

      String eventId = UUID.randomUUID().toString();
      assertNotNull(publishCloudEvent(providerId, TEST_EVENT_CODE, eventId, getEventDataNode(eventId)));
      eventId = UUID.randomUUID().toString();
      assertNotNull(publishRawEvent(providerId, TEST_EVENT_CODE, eventId, getEventDataNode(eventId)));
    } catch (Exception e) {
      logger.error(e.getMessage(), e);
      fail(e.getMessage());
    } finally {
      if (!StringUtils.isEmpty(registrationId)) {
        registrationServiceTester.deleteRegistration(registrationId);
      }
      if (!StringUtils.isEmpty(providerId)) {
        providerServiceTester.deleteProvider(providerId);
      }
    }
  }

}
