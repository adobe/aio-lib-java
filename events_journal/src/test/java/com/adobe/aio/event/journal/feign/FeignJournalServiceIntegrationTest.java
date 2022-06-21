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
package com.adobe.aio.event.journal.feign;

import static com.adobe.aio.event.management.feign.FeignProviderServiceIntegrationTest.TEST_EVENT_CODE;
import static com.adobe.aio.event.publish.feign.FeignPublishServiceIntegrationTest.RAW_EVENT_ID;

import com.adobe.aio.event.journal.JournalService;
import com.adobe.aio.event.journal.model.Event;
import com.adobe.aio.event.journal.model.JournalEntry;
import com.adobe.aio.event.management.ProviderService;
import com.adobe.aio.event.management.RegistrationService;
import com.adobe.aio.event.management.feign.FeignProviderServiceIntegrationTest;
import com.adobe.aio.event.management.feign.FeignRegistrationServiceIntegrationTest;
import com.adobe.aio.event.management.model.Provider;
import com.adobe.aio.event.management.model.Registration;
import com.adobe.aio.event.publish.PublishService;
import com.adobe.aio.event.publish.feign.FeignPublishServiceIntegrationTest;
import com.adobe.aio.util.WorkspaceUtil;
import com.adobe.aio.workspace.Workspace;
import java.util.function.BiPredicate;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FeignJournalServiceIntegrationTest {

  private static final Logger logger = LoggerFactory.getLogger(
      FeignJournalServiceIntegrationTest.class);

  private static final long JOURNAL_POLLING_TIME_OUT_IN_MILLISECONDS = 1000l * 60l * 2l;

  private static final String CLOUD_EVENT_ID_FIELD = "id";
  private static final String CLOUD_EVENT_DATA_FIELD = "data";

  private static final BiPredicate<Event, String> isEventIdInTheCloudEventData = (event, eventId) ->
      event.getEvent().has(CLOUD_EVENT_DATA_FIELD) &&
          event.getEvent().get(CLOUD_EVENT_DATA_FIELD).has(RAW_EVENT_ID) &&
          event.getEvent().get(CLOUD_EVENT_DATA_FIELD).get(RAW_EVENT_ID).asText().contains(eventId);
  private static final BiPredicate<Event, String> isEventIdTheCloudEventId = (event, eventId) ->
      event.getEvent().has(CLOUD_EVENT_ID_FIELD) && event.getEvent().get(CLOUD_EVENT_ID_FIELD)
          .asText().equals(eventId);

  private Workspace workspace;
  private ProviderService providerService;
  private RegistrationService registrationService;
  private PublishService publishService;

  @Before
  public void setUp() {
    workspace = WorkspaceUtil.getSystemWorkspaceBuilder().build();
    registrationService = RegistrationService.builder()
        .workspace(workspace)
        .url(WorkspaceUtil.getSystemProperty(WorkspaceUtil.API_URL))
        .build();
    providerService = ProviderService.builder()
        .workspace(workspace)
        .url(WorkspaceUtil.getSystemProperty(WorkspaceUtil.API_URL))
        .build();
    publishService = PublishService.builder()
        .workspace(workspace)
        .url(WorkspaceUtil.getSystemProperty(WorkspaceUtil.PUBLISH_URL))
        .build();
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
    Provider provider = FeignProviderServiceIntegrationTest.createTestProvider(providerService);
    Registration registration = FeignRegistrationServiceIntegrationTest.createRegistration(
        registrationService, provider.getId());

    String cloudEventId = FeignPublishServiceIntegrationTest.publishCloudEvent(publishService, provider.getId(), TEST_EVENT_CODE);

    boolean wasCloudEventPolled = pollJournalForEvent(workspace,
        registration.getJournalUrl(), cloudEventId, isEventIdTheCloudEventId);

    String rawEventId = FeignPublishServiceIntegrationTest.publishRawEvent(publishService, provider.getId(), TEST_EVENT_CODE);

    boolean wasRawEventPolled = pollJournalForEvent(workspace,
        registration.getJournalUrl(), rawEventId, isEventIdInTheCloudEventData);

    // we want to clean up the provider and registration even if the journal polling failed.
    FeignRegistrationServiceIntegrationTest.deleteRegistration(registrationService,
        registration.getRegistrationId());
    FeignProviderServiceIntegrationTest.deleteProvider(providerService, provider.getId());

    Assert.assertTrue("The published CloudEvent was not retrieved in the Journal",
        wasCloudEventPolled);
    Assert.assertTrue("The published Raw Event was not retrieved in the Journal",
        wasRawEventPolled);
  }

  private static boolean pollJournalForEvent(Workspace workspace,
      String journalUrl, String eventId, BiPredicate<Event, String> isEventIdInEvent)
      throws InterruptedException {
    JournalService journalService = JournalService.builder()
        .workspace(workspace)
        .url(journalUrl)
        .build();

    long pollingDuration = 0;
    JournalEntry entry = journalService.getOldest();
    while (!isEventIdInJournalEntry(entry, eventId, isEventIdInEvent)) {
      if (entry.isEmpty()) {
        logger.info("Empty journal entry, we will retry-after {} seconds.",
            entry.getRetryAfterInSeconds());
        long sleeptime = entry.getRetryAfterInSeconds() * 1000l;
        pollingDuration += sleeptime;
        if (pollingDuration < JOURNAL_POLLING_TIME_OUT_IN_MILLISECONDS) {
          Thread.sleep(sleeptime);
        } else {
          logger.error("We polled the journal for " + JOURNAL_POLLING_TIME_OUT_IN_MILLISECONDS
              + " milliseconds and could NOT find the expected eventId.");
          return false;
        }
      }
      entry = journalService.get(entry.getNextLink());
    }
    return true;
  }

  private static boolean isEventIdInJournalEntry(JournalEntry entry, String eventId,
      BiPredicate<Event, String> isEventIdInEvent) {
    if (!entry.isEmpty()) {
      for (Event event : entry.getEvents()) {
        logger.debug("Journal Event {}: ", event);
        if (isEventIdInEvent.test(event, eventId)) {
          logger.info("EventId {} found in a cloudEvent in this journal entry", eventId);
          return true;
        }
      }
      logger.info("EventId {} not found in this journal entry: {}", eventId, entry);
    }
    return false;
  }

}
