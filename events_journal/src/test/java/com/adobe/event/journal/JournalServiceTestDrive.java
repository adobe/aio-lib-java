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
package com.adobe.event.journal;

import com.adobe.Workspace;
import com.adobe.event.journal.model.Entry;
import com.adobe.ims.JWTAuthInterceptor;
import com.adobe.util.FileUtil;
import com.adobe.util.PrivateKeyBuilder;
import feign.RequestInterceptor;
import java.security.PrivateKey;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JournalServiceTestDrive {

  private static final Logger logger = LoggerFactory.getLogger(JournalServiceTestDrive.class);

  // use your own property file filePath or classpath and don't push back to git
  private static final String DEFAULT_TEST_DRIVE_PROPERTIES = "workspace.secret.properties";
  private static final String AIO_JOURNAL_URL = "aio_journal_url";

  /**
   * use your own property file filePath or classpath. WARNING: don't push back to github as it
   * contains many secrets. We do provide a sample/template workspace.properties file in the
   * ./src/test/resources folder
   */
  private static final String DEFAULT_TEST_PROPERTIES = "workspace.secret.properties";


  public static void main(String[] args) {
    try {

      Properties prop =
          FileUtil.readPropertiesFromClassPath(
              (args != null && args.length > 0) ? args[0] : DEFAULT_TEST_DRIVE_PROPERTIES);

      PrivateKey privateKey = new PrivateKeyBuilder().properties(prop).build();

      Workspace workspace = Workspace.builder()
          .properties(prop)
          .privateKey(privateKey)
          .build();

      RequestInterceptor authInterceptor = JWTAuthInterceptor.builder()
          .workspace(workspace)
          .build();

      String journalUrl=prop.getProperty(AIO_JOURNAL_URL);
      int nofEvents = 0;
      int nofEntries = 1;

      JournalService journalService = JournalService.builder()
          .authInterceptor(authInterceptor) // [1]
          .workspace(workspace) // [2]
          .url(journalUrl) // [3]
          .build(); //

      Entry entry = journalService.getOldest(); // [4]
      nofEvents += entry.size();
      logger.info("entry 1: {}", entry);
      while (!entry.isEmpty()) {
        entry = journalService.get(entry.getNextLink()); // [5]
        nofEvents += entry.size();
        nofEntries++;
        if (!entry.isEmpty()) {
          logger.info("entry {}: {}", nofEntries, entry);
        }
      }
      logger.info("The journal ({}) currently holds {} events (in {} journal entries)",
          journalUrl, nofEvents, nofEntries-1);

      logger.info("Latest entry: {}", journalService.getLatest());

      System.exit(0);
    } catch (Exception e) {
      logger.error(e.getMessage(), e);
      System.exit(-1);
    }
  }


}
