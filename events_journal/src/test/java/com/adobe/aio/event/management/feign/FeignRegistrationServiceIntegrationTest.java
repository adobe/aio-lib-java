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


import com.adobe.aio.event.management.ProviderService;
import com.adobe.aio.event.management.RegistrationService;
import com.adobe.aio.event.management.model.EventsOfInterestInputModel;
import com.adobe.aio.event.management.model.Provider;
import com.adobe.aio.event.management.model.Registration;
import com.adobe.aio.event.management.model.RegistrationCreateModel;
import com.adobe.aio.util.WorkspaceUtil;
import com.adobe.aio.workspace.Workspace;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FeignRegistrationServiceIntegrationTest {

  public static final String TEST_REGISTRATION_NAME = "com.adobe.aio.event.management.test.registration";
  public static final String TEST_REGISTRATION_DESC = TEST_REGISTRATION_NAME + " description";
  private final static Logger logger = LoggerFactory.getLogger(
      FeignRegistrationServiceIntegrationTest.class);
  private ProviderService providerService;
  private RegistrationService registrationService;

  public static EventsOfInterestInputModel.Builder getTestEventsOfInterestBuilder(String providerId) {
    return EventsOfInterestInputModel.builder()
        .eventCode(FeignProviderServiceIntegrationTest.TEST_EVENT_CODE)
        .providerId(providerId);
  }

  public static Registration createRegistration(RegistrationService registrationService,
      String providerId) {
    Optional<Registration> registration = registrationService.createRegistration(
      RegistrationCreateModel.builder()
        .name(TEST_REGISTRATION_NAME)
        .description(TEST_REGISTRATION_DESC)
        .deliveryType("journal")
        .addEventsOfInterests(getTestEventsOfInterestBuilder(providerId).build())
    );
    Assert.assertTrue(registration.isPresent());
    logger.info("Created AIO Event Registration: {}", registration.get());
    String registrationId = registration.get().getRegistrationId();
    Assert.assertNotNull(registrationId);
    Assert.assertEquals(TEST_REGISTRATION_DESC, registration.get().getDescription());
    Assert.assertEquals(TEST_REGISTRATION_NAME, registration.get().getName());
    Assert.assertEquals("journal", registration.get().getDeliveryType());
    Assert.assertEquals(1, registration.get().getEventsOfInterests().size());
    Assert.assertEquals(FeignProviderServiceIntegrationTest.TEST_EVENT_CODE,
        registration.get().getEventsOfInterests().iterator().next().getEventCode());
    Assert.assertEquals(providerId,
        registration.get().getEventsOfInterests().iterator().next().getProviderId());
    Assert.assertEquals("verified", registration.get().getWebhookStatus());
    Assert.assertEquals(true, registration.get().isEnabled());
    Assert.assertNull(registration.get().getWebhookUrl());
    assertUrl(registration.get().getEventsUrl().getHref());
    assertUrl(registration.get().getTraceUrl().getHref());
    Assert.assertNotNull(registration.get().getCreatedDate());
    Assert.assertNotNull(registration.get().getUpdatedDate());
    Assert.assertEquals(registration.get().getUpdatedDate(), registration.get().getCreatedDate());
    return registration.get();
  }

  public static void deleteRegistration(RegistrationService registrationService,
      String registrationId) {
    registrationService.delete(registrationId);
    Assert.assertTrue(registrationService.findById(registrationId).isEmpty());
    logger.info("Deleted AIO Event Registration: {}", registrationId);
  }

  private static void assertUrl(String stringUrl) {
    try {
      Assert.assertNotNull(stringUrl);
      URL url = new URL(stringUrl);
      Assert.assertEquals("https", url.getProtocol());
    } catch (MalformedURLException e) {
      Assert.fail("invalid url due to " + e.getMessage());
    }
  }

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

    Registration registration = createRegistration(registrationService, providerId);
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
    Assert.assertEquals(registration.getWebhookStatus(), found.get().getWebhookStatus());
    Assert.assertEquals(registration.isEnabled(),
        found.get().isEnabled());
    Assert.assertEquals(registration.getWebhookUrl(), found.get().getWebhookUrl());
    Assert.assertEquals(registration.getEventsUrl().getHref(), found.get().getEventsUrl().getHref());
    Assert.assertEquals(registration.getTraceUrl().getHref(), found.get().getTraceUrl().getHref());

    List<Registration> registrations = registrationService.getRegistrationsForWorkspace();
    Assert.assertTrue(registrations.size() > 0);

    deleteRegistration(registrationService, registrationId);

    FeignProviderServiceIntegrationTest.deleteProvider(providerService, providerId);
  }
}
