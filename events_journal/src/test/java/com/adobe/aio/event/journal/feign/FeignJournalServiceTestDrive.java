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

import com.adobe.aio.event.journal.JournalService;
import com.adobe.aio.event.journal.model.JournalEntry;
import com.adobe.aio.util.WorkspaceUtil;
import com.adobe.aio.workspace.Workspace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FeignJournalServiceTestDrive {

  private static final Logger logger = LoggerFactory.getLogger(FeignJournalServiceTestDrive.class);

  private static final String AIO_JOURNAL_URL = "aio_journal_url";

  public static void main(String[] args) {
    try {

      Workspace workspace = WorkspaceUtil.getSystemWorkspaceBuilder().build();
      String journalUrl = WorkspaceUtil.getSystemProperty(AIO_JOURNAL_URL);
      int nofEvents = 0;
      int nofEntries = 1;

      JournalService journalService = JournalService.builder()
          .workspace(workspace) // [1]
          .url(journalUrl) // [2]
          .build(); //

      JournalEntry entry = journalService.getOldest(); // [3]
      nofEvents += entry.size();
      logger.info("entry 1: {}", entry);
      while (!entry.isEmpty()) {
        entry = journalService.get(entry.getNextLink()); // [4]
        nofEvents += entry.size();
        nofEntries++;
        if (!entry.isEmpty()) {
          logger.info("entry {}: {}", nofEntries, entry);
        }
      }
      logger.info("The journal ({}) currently holds {} events (in {} journal entries)",
          journalUrl, nofEvents, nofEntries - 1);

      logger.info("Latest entry: {}", journalService.getLatest());

      String position = "tuna:4c06140b-8bcc-448e-a6e8-48c6dda4949c.tuna:ec78bee9-e81b-4d66-bc9d-81edd112d0ce.4098bf54-25a4-4268-b0e5-13fa850b2db1.0.1623764399.jdsptlaewfsynoewgv7h";
      logger.info("since entry: {}", journalService.getSince(position));
      logger.info("since entry: {}", journalService.getSince(position, 3));

      System.exit(0);
    } catch (Exception e) {
      logger.error(e.getMessage(), e);
      System.exit(-1);
    }
  }


}
