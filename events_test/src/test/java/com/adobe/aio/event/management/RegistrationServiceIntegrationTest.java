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

import com.adobe.aio.event.management.model.Registration;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;

public class RegistrationServiceIntegrationTest extends RegistrationServiceTester {

  public static final String TEST_REGISTRATION_NAME = "com.adobe.aio.event.management.test.registration";

  private final ProviderServiceTester providerServiceTester;

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
    Assert.assertFalse(registrationService.findById(idNotToBeFound).isPresent());
  }

  @Test
  public void createGetDeleteJournalRegistration() {
    String providerId = null;
    String registrationId = null;
    try {
      providerId = providerServiceTester.createOrUpdateProvider(TEST_EVENT_PROVIDER_LABEL,
          TEST_EVENT_CODE).getId();
      Registration registration = createJournalRegistration(TEST_REGISTRATION_NAME, providerId,
          TEST_EVENT_CODE);
      registrationId = registration.getRegistrationId();
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
      Assert.assertEquals(registration.getWebhookStatus(), found.get().getWebhookStatus());
      Assert.assertEquals(registration.isEnabled(), found.get().isEnabled());
      Assert.assertEquals(registration.getWebhookUrl(), found.get().getWebhookUrl());
      Assert.assertEquals(registration.getJournalUrl().getHref(), found.get().getJournalUrl().getHref());
      Assert.assertEquals(registration.getTraceUrl().getHref(), found.get().getTraceUrl().getHref());
    } catch (Exception e) {
      logger.error(e.getMessage(), e);
      Assert.fail(e.getMessage());
    } finally {
      if (!StringUtils.isEmpty(registrationId)) {
        this.deleteRegistration(registrationId);
      }
      if (!StringUtils.isEmpty(providerId)) {
        providerServiceTester.deleteProvider(providerId);
      }
    }
  }
}
