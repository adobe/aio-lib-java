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
package com.adobe.aio.event.journal;

import static com.adobe.aio.event.publish.PublishServiceTester.DATA_EVENT_ID_NODE;

import com.adobe.aio.event.journal.model.Event;
import com.adobe.aio.event.journal.model.JournalEntry;
import com.adobe.aio.util.WorkspaceUtil;
import com.adobe.aio.workspace.Workspace;
import java.util.function.BiPredicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JournalServiceTester {

  protected final Logger logger = LoggerFactory.getLogger(this.getClass());

  private static final long JOURNAL_POLLING_TIME_OUT_IN_MILLISECONDS = 1000l * 60l * 2l;

  private static final String CLOUD_EVENT_ID_FIELD = "id";
  private static final String CLOUD_EVENT_DATA_FIELD = "data";

  public static final BiPredicate<Event, String> isEventIdInTheCloudEventData = (event, eventId) ->
      event.getEvent().has(CLOUD_EVENT_DATA_FIELD) &&
          event.getEvent().get(CLOUD_EVENT_DATA_FIELD).has(DATA_EVENT_ID_NODE) &&
          event.getEvent().get(CLOUD_EVENT_DATA_FIELD).get(DATA_EVENT_ID_NODE).asText()
              .contains(eventId);
  public static final BiPredicate<Event, String> isEventIdTheCloudEventId = (event, eventId) ->
      event.getEvent().has(CLOUD_EVENT_ID_FIELD) && event.getEvent().get(CLOUD_EVENT_ID_FIELD)
          .asText().equals(eventId);


  private final Workspace workspace;

  public JournalServiceTester() {
    workspace = WorkspaceUtil.getSystemWorkspaceBuilder().build();
  }

  public boolean pollJournalForEvent(String journalUrl, String eventId,
      BiPredicate<Event, String> isEventIdInEvent)
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

  private boolean isEventIdInJournalEntry(JournalEntry entry, String eventId,
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
