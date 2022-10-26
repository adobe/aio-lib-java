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


import static com.adobe.aio.event.management.ProviderServiceIntegrationTest.TEST_EVENT_CODE;
import static com.adobe.aio.event.management.ProviderServiceIntegrationTest.TEST_EVENT_PROVIDER_LABEL;
import static com.adobe.aio.event.management.RegistrationServiceIntegrationTest.TEST_REGISTRATION_NAME;

import com.adobe.aio.event.management.ProviderServiceTester;
import com.adobe.aio.event.management.RegistrationServiceTester;
import com.adobe.aio.event.management.model.Provider;
import com.adobe.aio.event.management.model.Registration;
import org.junit.Test;

public class PublishServiceIntegrationTest extends PublishServiceTester {

  private final ProviderServiceTester providerServiceTester;
  private final RegistrationServiceTester registrationServiceTester;

  public PublishServiceIntegrationTest() {
    super();
    providerServiceTester = new ProviderServiceTester();
    registrationServiceTester = new RegistrationServiceTester();
  }

  @Test(expected = IllegalArgumentException.class)
  public void missingWorkspace() {
    PublishService publishService = PublishService.builder()
        .workspace(null)
        .build();
  }

  @Test
  public void publishEventTest() {
    Provider provider = providerServiceTester.createOrUpdateProvider(TEST_EVENT_PROVIDER_LABEL, TEST_EVENT_CODE);
    String providerId = provider.getId();
    Registration registration = registrationServiceTester.createJournalRegistration(
        TEST_REGISTRATION_NAME, providerId, TEST_EVENT_CODE);
    String registrationId = registration.getRegistrationId();

    String cloudEventId = publishCloudEvent(providerId, TEST_EVENT_CODE);
    String rawEventId = publishRawEvent(providerId, TEST_EVENT_CODE);

    // we want to clean up the provider and registration even if the journal polling failed.
    registrationServiceTester.deleteRegistration(registration.getRegistrationId());
    providerServiceTester.deleteProvider(provider.getId());
  }

}
