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

import static com.adobe.aio.event.management.ProviderServiceTester.DEFAULT_DESC_SUFFIX;

import com.adobe.aio.event.management.model.EventsOfInterest;
import com.adobe.aio.event.management.model.Registration;
import com.adobe.aio.event.management.model.Registration.IntegrationStatus;
import com.adobe.aio.event.management.model.Registration.Status;
import com.adobe.aio.event.management.model.RegistrationInputModel;
import com.adobe.aio.util.WorkspaceUtil;
import com.adobe.aio.workspace.Workspace;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;
import java.util.Set;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegistrationServiceTester {

  protected final Logger logger = LoggerFactory.getLogger(this.getClass());
  protected final Workspace workspace;
  protected final RegistrationService registrationService;

  public RegistrationServiceTester() {
    workspace = WorkspaceUtil.getSystemWorkspaceBuilder().build();
    registrationService = RegistrationService.builder()
        .workspace(workspace)
        .url(WorkspaceUtil.getSystemProperty(WorkspaceUtil.API_URL))
        .build();
  }

  public static RegistrationInputModel.Builder getRegistrationInputModelBuilder(
      String registrationName) {
    return RegistrationInputModel.builder()
        .name(registrationName)
        .description(registrationName + DEFAULT_DESC_SUFFIX);
  }

  public Registration createJournalRegistration(String registrationName,
      String providerId, String eventCode){
    return createRegistration(getRegistrationInputModelBuilder(registrationName)
        .addEventsOfInterests(EventsOfInterest.builder()
            .eventCode(eventCode)
            .providerId(providerId).build()));
  }

  public Registration createRegistration(
      RegistrationInputModel.Builder registrationInputModelBuilder) {
    RegistrationInputModel registrationInputModel =
        registrationInputModelBuilder.clientId(this.workspace.getApiKey()).build();
    Optional<Registration> registration = registrationService.createRegistration(
        registrationInputModelBuilder);
    Assert.assertTrue(registration.isPresent());
    logger.info("Created AIO Event Registration: {}", registration.get());
    String registrationId = registration.get().getRegistrationId();
    Assert.assertNotNull(registrationId);
    String createdId = registration.get().getRegistrationId();
    Assert.assertEquals(registrationInputModel.getDescription(), registration.get().getDescription());
    Assert.assertEquals(registrationInputModel.getName(), registration.get().getName());
    Assert.assertEquals(registrationInputModel.getDeliveryType(), registration.get().getDeliveryType());

    Set<EventsOfInterest> eventsOfInterestSet = registration.get().getEventsOfInterests();
    Assert.assertEquals(registrationInputModel.getEventsOfInterests().size(),eventsOfInterestSet.size());
    for(EventsOfInterest eventsOfInterestInput: registrationInputModel.getEventsOfInterests()){
      eventsOfInterestSet.contains(eventsOfInterestInput);
    }

    Assert.assertEquals(Status.VERIFIED, registration.get().getStatus());
    Assert.assertEquals(IntegrationStatus.ENABLED, registration.get().getIntegrationStatus());
    Assert.assertNull(registration.get().getWebhookUrl());
    assertUrl(registration.get().getJournalUrl());
    assertUrl(registration.get().getTraceUrl());
    Assert.assertNotNull(registration.get().getCreatedDate());
    Assert.assertNotNull(registration.get().getUpdatedDate());
    Assert.assertEquals(registration.get().getUpdatedDate(), registration.get().getCreatedDate());
    return registration.get();
  }

  public void deleteRegistration(String registrationId) {
    registrationService.delete(registrationId);
    Assert.assertFalse(registrationService.findById(registrationId).isPresent());
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


}
