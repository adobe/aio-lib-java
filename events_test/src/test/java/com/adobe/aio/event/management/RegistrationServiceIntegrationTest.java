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

import java.util.Optional;

import java.util.function.Supplier;
import org.apache.commons.lang3.StringUtils;

import com.adobe.aio.event.management.model.Registration;
import org.junit.jupiter.api.Test;

import static com.adobe.aio.event.management.ProviderServiceIntegrationTest.*;

import static org.junit.jupiter.api.Assertions.*;

public class RegistrationServiceIntegrationTest extends RegistrationServiceTester {

  public static final String TEST_REGISTRATION_NAME = "com.adobe.aio.event.management.test.registration";

  private final ProviderServiceTester providerServiceTester;

  public RegistrationServiceIntegrationTest() {
    super();
    providerServiceTester = new ProviderServiceTester();
  }

  @Test
  public void missingWorkspace() {
    assertThrows(IllegalArgumentException.class, () -> RegistrationService.builder().build());
  }

  @Test
  public void getNotFound() {
    String idNotToBeFound = "this_id_should_not_exist";
    assertFalse(registrationService.findById(idNotToBeFound).isPresent());
  }

  @Test
  public void createGetDeleteJournalRegistration() {
    String providerId = null;
    String registrationId = null;
    try {
      providerId = providerServiceTester.createOrUpdateProvider(TEST_EVENT_PROVIDER_LABEL,
          TEST_EVENT_CODE).getId();
      Registration registration = createOrUpdateJournalRegistration(TEST_REGISTRATION_NAME, providerId,
          TEST_EVENT_CODE);
      registrationId = registration.getRegistrationId();
      String finalRegistrationId = registration.getRegistrationId();
      assertCreatedOrUpdatedRegistrationMatchesWithFoundRegistration(registration,
          () -> registrationService.findById(finalRegistrationId));

      // covering the update path
      registration = createOrUpdateJournalRegistration(TEST_REGISTRATION_NAME, providerId,
          TEST_EVENT_CODE);
      String finalUpdatedRegistrationId = registration.getRegistrationId();
      assertCreatedOrUpdatedRegistrationMatchesWithFoundRegistration(registration,
          () -> registrationService.findById(finalUpdatedRegistrationId));
    } catch (Exception e) {
      logger.error(e.getMessage(), e);
      fail(e.getMessage());
    } finally {
      if (!StringUtils.isEmpty(registrationId)) {
        this.deleteRegistration(registrationId);
      }
      if (!StringUtils.isEmpty(providerId)) {
        providerServiceTester.deleteProvider(providerId);
      }
    }
  }

  public void assertCreatedOrUpdatedRegistrationMatchesWithFoundRegistration(Registration registration,
      Supplier<Optional<Registration>> foundRegistrationSupplier) {
    Optional<Registration> foundRegistrationOptional = foundRegistrationSupplier.get();
    assertTrue(foundRegistrationOptional.isPresent());
    logger.info("Found AIO Event Registration: {}", foundRegistrationOptional.get());
    assertEquals(registration.getRegistrationId(), foundRegistrationOptional.get().getRegistrationId());
    assertEquals(registration.getClientId(), foundRegistrationOptional.get().getClientId());
    assertEquals(registration.getDescription(), foundRegistrationOptional.get().getDescription());
    assertEquals(registration.getName(), foundRegistrationOptional.get().getName());
    assertEquals(registration.getDeliveryType(), foundRegistrationOptional.get().getDeliveryType());
    assertEquals(registration.getEventsOfInterests(),
        foundRegistrationOptional.get().getEventsOfInterests());
    assertEquals(registration.getWebhookStatus(), foundRegistrationOptional.get().getWebhookStatus());
    assertEquals(registration.isEnabled(), foundRegistrationOptional.get().isEnabled());
    assertEquals(registration.getWebhookUrl(), foundRegistrationOptional.get().getWebhookUrl());
    assertEquals(registration.getJournalUrl().getHref(), foundRegistrationOptional.get().getJournalUrl().getHref());
    assertEquals(registration.getTraceUrl().getHref(), foundRegistrationOptional.get().getTraceUrl().getHref());
  }
}
