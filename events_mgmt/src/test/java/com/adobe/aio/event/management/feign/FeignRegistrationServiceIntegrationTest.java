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
package com.adobe.aio.event.management.feign;

import static com.adobe.aio.event.management.model.EventMetadataModelTest.TEST_EVENT_CODE;
import static com.adobe.aio.event.management.model.RegistrationInputModelTest.TEST_REGISTRATION_DESC;
import static com.adobe.aio.event.management.model.RegistrationInputModelTest.TEST_REGISTRATION_NAME;

import com.adobe.aio.event.management.ProviderService;
import com.adobe.aio.event.management.RegistrationService;
import com.adobe.aio.event.management.model.DeliveryType;
import com.adobe.aio.event.management.model.EventsOfInterestTest;
import com.adobe.aio.event.management.model.Provider;
import com.adobe.aio.event.management.model.Registration;
import com.adobe.aio.event.management.model.Registration.IntegrationStatus;
import com.adobe.aio.event.management.model.Registration.Status;
import com.adobe.aio.event.management.model.RegistrationInputModelTest;
import com.adobe.aio.util.WorkspaceUtil;
import com.adobe.aio.workspace.Workspace;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FeignRegistrationServiceIntegrationTest {

  private final Logger logger = LoggerFactory.getLogger(this.getClass());
  private ProviderService providerService;
  private RegistrationService registrationService;

  @Before
  public void setUp() {
    Workspace workspace = WorkspaceUtil.getSystemWorkspaceBuilder().build();
    providerService = ProviderService.builder()
        .workspace(workspace)
        .url(WorkspaceUtil.getSystemProperty(WorkspaceUtil.API_URL))
        .build();
    registrationService = RegistrationService.builder()
        .workspace(workspace)
        .url(WorkspaceUtil.getSystemProperty(WorkspaceUtil.API_URL))
        .build();
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
  public void createGetDeleteJournalRegistration() throws MalformedURLException {
    Provider provider = FeignProviderServiceIntegrationTest.createTestProvider(providerService);
    String providerId = provider.getId();

    Optional<Registration> registration = registrationService.createRegistration(
        RegistrationInputModelTest.getRegistrationInputModelBuilder()
            .addEventsOfInterests(
                EventsOfInterestTest.getTestEventsOfInterestBuilder(providerId).build()));
    Assert.assertTrue(registration.isPresent());
    logger.info("Created AIO Event Registration: {}", registration.get());
    String registrationId = registration.get().getRegistrationId();
    Assert.assertNotNull(registrationId);
    String createdId = registration.get().getRegistrationId();
    Assert.assertEquals(TEST_REGISTRATION_DESC, registration.get().getDescription());
    Assert.assertEquals(TEST_REGISTRATION_NAME, registration.get().getName());
    Assert.assertEquals(DeliveryType.JOURNAL, registration.get().getDeliveryType());
    Assert.assertEquals(1, registration.get().getEventsOfInterests().size());
    Assert.assertEquals(TEST_EVENT_CODE,
        registration.get().getEventsOfInterests().iterator().next().getEventCode());
    Assert.assertEquals(providerId,
        registration.get().getEventsOfInterests().iterator().next().getProviderId());
    Assert.assertEquals(Status.VERIFIED, registration.get().getStatus());
    Assert.assertEquals(IntegrationStatus.ENABLED, registration.get().getIntegrationStatus());
    Assert.assertNull(registration.get().getWebhookUrl());
    assertUrl(registration.get().getJournalUrl());
    assertUrl(registration.get().getTraceUrl());
    Assert.assertNotNull(registration.get().getCreatedDate());
    Assert.assertNotNull(registration.get().getUpdatedDate());
    Assert.assertEquals(registration.get().getUpdatedDate(), registration.get().getCreatedDate());

    Optional<Registration> found = registrationService.findById(registrationId);
    logger.info("Found AIO Event Registration: {}", registration.get());
    Assert.assertTrue(found.isPresent());
    Assert.assertEquals(registrationId, found.get().getRegistrationId());
    Assert.assertEquals(registration.get().getClientId(), found.get().getClientId());
    Assert.assertEquals(registration.get().getDescription(), found.get().getDescription());
    Assert.assertEquals(registration.get().getName(), found.get().getName());
    Assert.assertEquals(registration.get().getDeliveryType(), found.get().getDeliveryType());
    Assert.assertEquals(registration.get().getEventsOfInterests(),
        found.get().getEventsOfInterests());
    Assert.assertEquals(registration.get().getStatus(), found.get().getStatus());
    Assert.assertEquals(registration.get().getIntegrationStatus(),
        found.get().getIntegrationStatus());
    Assert.assertEquals(registration.get().getWebhookUrl(), found.get().getWebhookUrl());
    Assert.assertEquals(registration.get().getJournalUrl(), found.get().getJournalUrl());
    Assert.assertEquals(registration.get().getTraceUrl(), found.get().getTraceUrl());

    registrationService.delete(registrationId);
    Assert.assertTrue(registrationService.findById(registrationId).isEmpty());

    providerService.deleteProvider(providerId);
    logger.info("Deleted AIO Events Provider: {}", providerId);
  }

  private static void assertUrl(String stringUrl) throws MalformedURLException {
    Assert.assertNotNull(stringUrl);
    URL url = new URL(stringUrl);
    Assert.assertEquals("https", url.getProtocol());
  }
}
