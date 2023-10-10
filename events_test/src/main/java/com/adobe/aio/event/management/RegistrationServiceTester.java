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

import com.adobe.aio.event.management.model.EventsOfInterest;
import com.adobe.aio.event.management.model.EventsOfInterestInputModel;
import com.adobe.aio.event.management.model.Registration;
import com.adobe.aio.event.management.model.RegistrationCreateModel;
import com.adobe.aio.event.management.model.RegistrationUpdateModel;
import com.adobe.aio.util.WorkspaceUtil;
import com.adobe.aio.workspace.Workspace;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.*;

public class RegistrationServiceTester {

  public static final String TEST_DESCRIPTION = "Test description";
  public static final String DELIVERY_TYPE_JOURNAL = "journal";
  public static final String DELIVERY_TYPE_WEBHOOK = "webhook";
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

  public RegistrationService getRegistrationService(){
    return this.registrationService;
  }

  public static EventsOfInterestInputModel.Builder getTestEventsOfInterestBuilder(String providerId, String eventCode) {
    return EventsOfInterestInputModel.builder()
                    .eventCode(eventCode)
                    .providerId(providerId);
  }

  public Registration createJournalRegistration(String registrationName,
      String providerId, String eventCode){
    return createRegistration(RegistrationCreateModel.builder()
          .name(registrationName)
          .description(TEST_DESCRIPTION)
          .deliveryType(DELIVERY_TYPE_JOURNAL)
          .addEventsOfInterests(getTestEventsOfInterestBuilder(providerId, eventCode).build()));
  }

  public Registration createRuntimeWebhookRegistration(String registrationName, String providerId,
      String eventCode, String runtimeAction) {
    return createRegistration(RegistrationCreateModel.builder()
        .name(registrationName)
        .description(TEST_DESCRIPTION)
        .deliveryType(DELIVERY_TYPE_WEBHOOK)
        .runtimeAction(runtimeAction)
        .addEventsOfInterests(getTestEventsOfInterestBuilder(providerId, eventCode).build()));
  }

  public Registration createRegistration(
      RegistrationCreateModel.Builder registrationInputModelBuilder) {
    RegistrationCreateModel registrationInputModel =
        registrationInputModelBuilder.clientId(this.workspace.getApiKey()).build();
    Optional<Registration> registration = registrationService.createRegistration(registrationInputModelBuilder);
    assertTrue(registration.isPresent());
    Registration registrationCreated = registration.get();
    logger.info("Created AIO Event Registration: {}", registration.get());
    String registrationId = registrationCreated.getRegistrationId();
    assertNotNull(registrationId);
    assertEquals(registrationInputModel.getDescription(), registrationCreated.getDescription());
    assertEquals(registrationInputModel.getName(), registrationCreated.getName());
    assertEquals(registrationInputModel.getDeliveryType(), registrationCreated.getDeliveryType());

    if (!registrationInputModel.getDeliveryType().equals(DELIVERY_TYPE_JOURNAL)) {
      if (StringUtils.isNotEmpty(registrationInputModel.getRuntimeAction())) {
        String runtimeAction = registration.get().getRuntimeAction();
        assertEquals(registrationInputModel.getRuntimeAction(), runtimeAction);
      }
      assertNotNull(registration.get().getWebhookUrl());
    } else {
      assertNull(registration.get().getWebhookUrl());
      assertUrl(registration.get().getJournalUrl().getHref());
    }

    Set<EventsOfInterest> eventsOfInterestSet = registration.get().getEventsOfInterests();
    assertEquals(registrationInputModel.getEventsOfInterests().size(),eventsOfInterestSet.size());
    for(EventsOfInterestInputModel eventsOfInterestInput: registrationInputModel.getEventsOfInterests()){
      assertTrue(eventsOfInterestSet.stream()
                      .anyMatch(eventsOfInterest -> eventsOfInterest.getEventCode()
                                      .equals(eventsOfInterestInput.getEventCode())));
    }

    assertEquals("verified", registrationCreated.getWebhookStatus());
    assertEquals(true, registrationCreated.isEnabled());

    assertUrl(registration.get().getTraceUrl().getHref());
    assertNotNull(registration.get().getCreatedDate());
    assertNotNull(registration.get().getUpdatedDate());
    return registration.get();
  }

  public Registration updateRegistration(Registration registrationToUpdate, String runtimeActionToUpdate) {
    EventsOfInterest eventsOfInterest = registrationToUpdate.getEventsOfInterests().iterator().next();
    String providerId = eventsOfInterest.getProviderId();
    String eventCode = eventsOfInterest.getEventCode();
    Optional<Registration> updatedRegistration =
        registrationService.updateRegistration(registrationToUpdate.getRegistrationId(),
            RegistrationUpdateModel.builder()
            .name(registrationToUpdate.getName())
            .description(TEST_DESCRIPTION)
            .deliveryType(DELIVERY_TYPE_WEBHOOK)
            .runtimeAction(runtimeActionToUpdate)
            .addEventsOfInterests(getTestEventsOfInterestBuilder(providerId, eventCode).build()));

    assertTrue(updatedRegistration.isPresent());
    logger.info("Updated AIO Event Registration: {}", updatedRegistration.get());
    return updatedRegistration.get();
  }

  public void deleteRegistration(String registrationId) {
    if (registrationService.findById(registrationId).isPresent()) {
      registrationService.delete(registrationId);
      assertFalse(registrationService.findById(registrationId).isPresent());
      logger.info("Deleted AIO Event Registration: {}", registrationId);
    } else {
      logger.info("no registration exists for registration_id=`{}` to delete", registrationId);
    }
  }

  private static void assertUrl(String stringUrl) {
    try {
      assertNotNull(stringUrl);
      URL url = new URL(stringUrl);
    } catch (MalformedURLException e) {
      fail("invalid url due to " + e.getMessage());
    }
  }


}
