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
import com.adobe.aio.event.journal.api.JournalApi;
import com.adobe.aio.event.journal.model.JournalEntry;
import com.adobe.aio.feign.AIOHeaderInterceptor;
import com.adobe.aio.ims.feign.JWTAuthInterceptor;
import com.adobe.aio.util.JacksonUtil;
import com.adobe.aio.util.feign.FeignUtil;
import com.adobe.aio.workspace.Workspace;
import feign.RequestInterceptor;
import feign.jackson.JacksonDecoder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FeignJournalService implements JournalService {

  private final Logger logger = LoggerFactory.getLogger(this.getClass());
  private final JournalApi journalApi;
  private final String imsOrgId;
  private final RequestInterceptor authInterceptor;
  private final RequestInterceptor aioHeaderInterceptor;

  public FeignJournalService(final Workspace workspace, final String journalUrl) {
    if (StringUtils.isEmpty(journalUrl)) {
      throw new IllegalArgumentException(
          "JournalService is missing aj ournalUrl");
    }
    if (workspace == null) {
      throw new IllegalArgumentException("RegistrationService is missing a workspace context");
    }
    if (StringUtils.isEmpty(workspace.getImsOrgId())) {
      throw new IllegalArgumentException("Workspace is missing an imsOrgId context");
    }
    workspace.validateWorkspaceContext();
    this.imsOrgId = workspace.getImsOrgId();
    authInterceptor = JWTAuthInterceptor.builder().workspace(workspace).build();
    aioHeaderInterceptor = AIOHeaderInterceptor.builder().workspace(workspace).build();
    this.journalApi = FeignUtil.getBaseBuilder()
        .decoder(new JournalLinkDecoder(new JacksonDecoder(JacksonUtil.DEFAULT_OBJECT_MAPPER)))
        .requestInterceptor(authInterceptor)
        .requestInterceptor(aioHeaderInterceptor)
        .target(JournalApi.class, journalUrl);
  }

  @Override
  public JournalEntry getOldest() {
    return logIfNull(journalApi.get(this.imsOrgId));
  }

  @Override
  public JournalEntry getLatest() {
    return logIfNull(journalApi.getLatest(this.imsOrgId));
  }

  @Override
  public JournalEntry getSince(String position) {
    return logIfNull(journalApi.getSince(this.imsOrgId, position));
  }

  @Override
  public JournalEntry getSince(String position, int maxBatchSize) {
    return logIfNull(journalApi.getSince(this.imsOrgId, position, maxBatchSize));
  }

  private JournalEntry logIfNull(JournalEntry journalEntry) {
    if (journalEntry == null) {
      logger.error("No/null Journal Entry, check your Journal url");
    }
    return journalEntry;
  }

  @Override
  public JournalEntry get(String linkUrl) {
    JournalApi linkJournalApi = FeignUtil.getBaseBuilder()
        .decoder(new JournalLinkDecoder(new JacksonDecoder(JacksonUtil.DEFAULT_OBJECT_MAPPER)))
        .requestInterceptor(authInterceptor)
        .requestInterceptor(aioHeaderInterceptor)
        .target(JournalApi.class, linkUrl);
    JournalEntry journalEntry = linkJournalApi.get(this.imsOrgId);
    if (journalEntry == null) {
      logger.error("No/null Journal Entry, check your Journal link url: " + linkUrl);
    }
    return journalEntry;
  }

}
