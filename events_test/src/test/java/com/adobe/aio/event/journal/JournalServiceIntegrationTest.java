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
package com.adobe.aio.event.journal;

import static com.adobe.aio.event.management.ProviderServiceIntegrationTest.TEST_EVENT_CODE;
import static com.adobe.aio.event.management.ProviderServiceIntegrationTest.TEST_EVENT_PROVIDER_LABEL;
import static com.adobe.aio.event.management.RegistrationServiceIntegrationTest.TEST_REGISTRATION_NAME;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.adobe.aio.event.journal.model.JournalEntry;
import com.adobe.aio.event.management.ProviderServiceIntegrationTest;
import com.adobe.aio.event.management.ProviderServiceTester;
import com.adobe.aio.event.management.RegistrationServiceTester;
import com.adobe.aio.event.management.model.Registration;
import com.adobe.aio.event.publish.PublishServiceTester;
import com.adobe.aio.util.WorkspaceUtil;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

public class JournalServiceIntegrationTest extends JournalServiceTester {

  private final ProviderServiceTester providerServiceTester;
  private final RegistrationServiceTester registrationServiceTester;
  private final PublishServiceTester publishServiceTester;

  public JournalServiceIntegrationTest() {
    super();
    providerServiceTester = new ProviderServiceTester();
    registrationServiceTester = new RegistrationServiceTester();
    publishServiceTester = new PublishServiceTester();

  }

  @Test
  public void missingWorkspace() {
    assertThrows(IllegalArgumentException.class, () ->
     JournalService.builder()
        .workspace(null)
        .url("https://adobe.com")
        .build()
    );
  }

  @Test
  public void missingUrl() {
    assertThrows(IllegalArgumentException.class, () ->
    JournalService.builder()
        .workspace(WorkspaceUtil.getSystemWorkspaceBuilder().build())
        .build()
    );
  }

  @Test
  public void testJournalPolling()
      throws InterruptedException {
    String providerId = null;
    String registrationId = null;
    try {
      providerId = providerServiceTester.createOrUpdateProvider(TEST_EVENT_PROVIDER_LABEL,
          ProviderServiceIntegrationTest.TEST_EVENT_CODE).getId();
      Registration registration = registrationServiceTester.createOrUpdateJournalRegistration(
          TEST_REGISTRATION_NAME, providerId, TEST_EVENT_CODE);
      registrationId = registration.getRegistrationId();

      JournalEntry fromEntry = getLatestEntry(registration.getJournalUrl().getHref());
      logger.info("polled latest Journal Entry before publishing a new test event...");

      String cloudEventId = publishServiceTester.publishCloudEvent(false, providerId, TEST_EVENT_CODE);
      boolean wasCloudEventPolled = pollJournalForEvent(
          registration.getJournalUrl().getHref(), fromEntry, cloudEventId, isEventIdTheCloudEventId);

      String rawEventId = publishServiceTester.publishRawEvent(providerId, TEST_EVENT_CODE);
      boolean wasRawEventPolled = pollJournalForEvent(registration.getJournalUrl().getHref(), fromEntry, rawEventId,
          isEventIdInTheCloudEventData);

      assertTrue(wasCloudEventPolled, "The published CloudEvent was not retrieved in the Journal");
      assertTrue(wasRawEventPolled, "The published Raw Event was not retrieved in the Journal");
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
