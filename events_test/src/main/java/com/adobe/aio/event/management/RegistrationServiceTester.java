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
import java.util.function.Supplier;
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

  public Registration createOrUpdateJournalRegistration(String registrationName,
      String providerId, String eventCode){
    return createOrUpdateRegistration(RegistrationCreateModel.builder()
          .name(registrationName)
          .description(TEST_DESCRIPTION)
          .deliveryType(DELIVERY_TYPE_JOURNAL)
          .addEventsOfInterests(getTestEventsOfInterestBuilder(providerId, eventCode).build()));
  }

  public Registration createOrUpdateRuntimeWebhookRegistration(String registrationName, String providerId,
      String eventCode, String runtimeAction) {
    return createOrUpdateRegistration(RegistrationCreateModel.builder()
        .name(registrationName)
        .description(TEST_DESCRIPTION)
        .deliveryType(DELIVERY_TYPE_WEBHOOK)
        .runtimeAction(runtimeAction)
        .addEventsOfInterests(getTestEventsOfInterestBuilder(providerId, eventCode).build()));
  }

  public Registration createOrUpdateRegistration(
      RegistrationCreateModel.Builder registrationInputModelBuilder) {
    return assertRegistrationCreatedOrUpdatedResponseWithEventsOfInterest(registrationInputModelBuilder,
        () -> registrationService.createOrUpdateRegistration(registrationInputModelBuilder));
  }

  public Registration assertRegistrationCreatedOrUpdatedResponseWithEventsOfInterest(
      RegistrationCreateModel.Builder registrationInputModelBuilder,
      Supplier<Optional<Registration>> registrationSupplier) {
    RegistrationCreateModel registrationInputModel =
        registrationInputModelBuilder.clientId(this.workspace.getApiKey()).build();
    Optional<Registration> registrationOptional = registrationSupplier.get();
    assertTrue(registrationOptional.isPresent());
    Registration registrationCreated = registrationOptional.get();
    logger.info("Created/Updated AIO Event Registration: {}", registrationOptional.get());

    assertNotNull(registrationCreated.getRegistrationId());
    assertEquals(registrationInputModel.getDescription(), registrationCreated.getDescription());
    assertEquals(registrationInputModel.getName(), registrationCreated.getName());
    assertEquals(registrationInputModel.getDeliveryType(), registrationCreated.getDeliveryType());
    if (registrationInputModel.getDeliveryType().equals(DELIVERY_TYPE_WEBHOOK)) {
      if (StringUtils.isNotEmpty(registrationInputModel.getRuntimeAction())) {
        assertEquals(registrationInputModel.getRuntimeAction(), registrationCreated.getRuntimeAction());
        assertNotNull(registrationCreated.getWebhookUrl());
      } else {
        assertEquals(registrationInputModel.getWebhookUrl(), registrationCreated.getWebhookUrl());
      }
    } else {
      assertNull(registrationOptional.get().getWebhookUrl());
      assertUrl(registrationOptional.get().getJournalUrl().getHref());
    }

    Set<EventsOfInterest> eventsOfInterestSet = registrationOptional.get().getEventsOfInterests();
    assertEquals(registrationInputModel.getEventsOfInterests().size(),eventsOfInterestSet.size());
    for(EventsOfInterestInputModel eventsOfInterestInput: registrationInputModel.getEventsOfInterests()){
      assertTrue(eventsOfInterestSet.stream()
          .anyMatch(eventsOfInterest -> eventsOfInterest.getEventCode()
              .equals(eventsOfInterestInput.getEventCode())));
    }

    assertEquals("verified", registrationCreated.getWebhookStatus());
    assertEquals(true, registrationCreated.isEnabled());

    assertUrl(registrationOptional.get().getTraceUrl().getHref());
    assertUrl(registrationOptional.get().getJournalUrl().getHref());

    assertNotNull(registrationOptional.get().getCreatedDate());
    assertNotNull(registrationOptional.get().getUpdatedDate());
    return registrationOptional.get();
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
