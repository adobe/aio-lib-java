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

import com.adobe.aio.workspace.Workspace;
import com.adobe.aio.event.journal.api.JournalApi;
import com.adobe.aio.event.journal.model.JournalEntry;
import com.adobe.aio.util.FeignUtil;
import com.adobe.aio.util.JacksonUtil;
import feign.RequestInterceptor;
import feign.jackson.JacksonDecoder;
import org.apache.commons.lang3.StringUtils;

class JournalServiceImpl implements JournalService {

  private final JournalApi journalApi;
  private final String imsOrgId;
  private final RequestInterceptor authInterceptor;

  JournalServiceImpl(final RequestInterceptor authInterceptor,
      final String journalUrl, Workspace workspace) {
    if (authInterceptor == null) {
      throw new IllegalArgumentException(
          "JournalService is missing a authentication interceptor");
    }
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
    this.imsOrgId = workspace.getImsOrgId();
    this.authInterceptor = authInterceptor;
    this.journalApi = FeignUtil.getBaseBuilder()
        .decoder(new JournalLinkDecoder(new JacksonDecoder(JacksonUtil.DEFAULT_OBJECT_MAPPER)))
        .requestInterceptor(authInterceptor)
        .target(JournalApi.class, journalUrl);
  }

  @Override
  public JournalEntry getOldest() {
    return journalApi.get(this.imsOrgId);
  }

  @Override
  public JournalEntry getLatest() {
    return journalApi.getLatest(this.imsOrgId);
  }

  @Override
  public JournalEntry getSince(String position) {
    return journalApi.getSince(this.imsOrgId, position);
  }

  @Override
  public JournalEntry getSince(String position, int maxBatchSize) {
    return journalApi.getSince(this.imsOrgId, position, maxBatchSize);
  }

  @Override
  public JournalEntry get(String linkUrl) {
    JournalApi linkJournalApi = FeignUtil.getBaseBuilder()
        .decoder(new JournalLinkDecoder(new JacksonDecoder(JacksonUtil.DEFAULT_OBJECT_MAPPER)))
        .requestInterceptor(authInterceptor)
        .target(JournalApi.class, linkUrl);
    return linkJournalApi.get(this.imsOrgId);
  }

}