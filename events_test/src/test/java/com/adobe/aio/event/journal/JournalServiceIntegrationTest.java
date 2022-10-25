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
import static com.adobe.aio.event.publish.PublishServiceTester.DATA_EVENT_ID_NODE;

import com.adobe.aio.event.journal.model.Event;
import com.adobe.aio.event.management.ProviderServiceIntegrationTest;
import com.adobe.aio.event.management.ProviderServiceTester;
import com.adobe.aio.event.management.RegistrationServiceTester;
import com.adobe.aio.event.management.model.Provider;
import com.adobe.aio.event.management.model.Registration;
import com.adobe.aio.event.publish.PublishServiceTester;
import com.adobe.aio.util.WorkspaceUtil;
import java.util.function.BiPredicate;
import org.junit.Assert;
import org.junit.Test;

public class JournalServiceIntegrationTest extends JournalServiceTester {

  private ProviderServiceTester providerServiceTester;
  private RegistrationServiceTester registrationServiceTester;
  private PublishServiceTester publishServiceTester;

  public JournalServiceIntegrationTest() {
    super();
    providerServiceTester = new ProviderServiceTester();
    registrationServiceTester = new RegistrationServiceTester();
    publishServiceTester = new PublishServiceTester();
  }

  @Test(expected = IllegalArgumentException.class)
  public void missingWorkspace() {
    JournalService journalService = JournalService.builder()
        .workspace(null)
        .url("https://adobe.com")
        .build();
  }

  @Test(expected = IllegalArgumentException.class)
  public void missingUrl() {
    JournalService journalService = JournalService.builder()
        .workspace(WorkspaceUtil.getSystemWorkspaceBuilder().build())
        .build();
  }

  @Test
  public void testJournalPolling()
      throws InterruptedException {
    Provider provider = providerServiceTester.createProvider(TEST_EVENT_PROVIDER_LABEL,
        ProviderServiceIntegrationTest.TEST_EVENT_CODE);
    String providerId = provider.getId();
    Registration registration = registrationServiceTester.createJournalRegistration(
        TEST_REGISTRATION_NAME, providerId, TEST_EVENT_CODE);
    String registrationId = registration.getRegistrationId();

    String cloudEventId = publishServiceTester.publishCloudEvent(providerId,TEST_EVENT_CODE);

    boolean wasCloudEventPolled = pollJournalForEvent(
        registration.getJournalUrl(), cloudEventId, isEventIdTheCloudEventId);

    String rawEventId = publishServiceTester.publishRawEvent(providerId, TEST_EVENT_CODE);

    boolean wasRawEventPolled = pollJournalForEvent(registration.getJournalUrl(), rawEventId,
        isEventIdInTheCloudEventData);

    // we want to clean up the provider and registration even if the journal polling failed.
    registrationServiceTester.deleteRegistration(registrationId);
    providerServiceTester.deleteProvider(provider.getId());

    Assert.assertTrue("The published CloudEvent was not retrieved in the Journal",
        wasCloudEventPolled);
    Assert.assertTrue("The published Raw Event was not retrieved in the Journal",
        wasRawEventPolled);
  }

}
