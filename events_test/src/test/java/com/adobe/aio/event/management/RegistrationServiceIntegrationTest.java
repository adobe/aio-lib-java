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
package com.adobe.aio.event.management;


import static com.adobe.aio.event.management.ProviderServiceIntegrationTest.TEST_EVENT_CODE;
import static com.adobe.aio.event.management.ProviderServiceIntegrationTest.TEST_EVENT_PROVIDER_LABEL;

import com.adobe.aio.event.management.model.Provider;
import com.adobe.aio.event.management.model.Registration;
import java.util.Optional;
import org.junit.Assert;
import org.junit.Test;

public class RegistrationServiceIntegrationTest extends RegistrationServiceTester {

  public static final String TEST_REGISTRATION_NAME = "com.adobe.aio.event.management.test.registration";

  private ProviderServiceTester providerServiceTester;

  public RegistrationServiceIntegrationTest() {
    super();
    providerServiceTester = new ProviderServiceTester();
  }

  @Test(expected = IllegalArgumentException.class)
  public void missingWorkspace() {
    RegistrationService.builder().build();
  }

  @Test
  public void getNotFound() {
    String idNotToBeFound = "this_id_should_not_exist";
    Assert.assertTrue(registrationService.findById(idNotToBeFound).isEmpty());
  }

  @Test
  public void createGetDeleteJournalRegistration()  {
    Provider provider = providerServiceTester.createOrUpdateProvider(TEST_EVENT_PROVIDER_LABEL, TEST_EVENT_CODE);
    String providerId = provider.getId();

    Registration registration = createJournalRegistration(TEST_REGISTRATION_NAME, providerId, TEST_EVENT_CODE);
    String registrationId = registration.getRegistrationId();

    Optional<Registration> found = registrationService.findById(registrationId);
    Assert.assertTrue(found.isPresent());
    logger.info("Found AIO Event Registration: {}", found.get());
    Assert.assertEquals(registrationId, found.get().getRegistrationId());
    Assert.assertEquals(registration.getClientId(), found.get().getClientId());
    Assert.assertEquals(registration.getDescription(), found.get().getDescription());
    Assert.assertEquals(registration.getName(), found.get().getName());
    Assert.assertEquals(registration.getDeliveryType(), found.get().getDeliveryType());
    Assert.assertEquals(registration.getEventsOfInterests(),
        found.get().getEventsOfInterests());
    Assert.assertEquals(registration.getStatus(), found.get().getStatus());
    Assert.assertEquals(registration.getIntegrationStatus(),
        found.get().getIntegrationStatus());
    Assert.assertEquals(registration.getWebhookUrl(), found.get().getWebhookUrl());
    Assert.assertEquals(registration.getJournalUrl(), found.get().getJournalUrl());
    Assert.assertEquals(registration.getTraceUrl(), found.get().getTraceUrl());

    deleteRegistration(registrationId);

    providerServiceTester.deleteProvider(providerId);
  }
}
