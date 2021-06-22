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
package com.adobe.event.management;

import static com.adobe.util.Constants.API_MANAGEMENT_URL;

import com.adobe.Workspace;
import com.adobe.event.management.api.EventMetadataApi;
import com.adobe.event.management.model.EventMetadata;
import com.adobe.event.management.model.EventMetadataCollection;
import com.adobe.util.FeignUtil;
import feign.RequestInterceptor;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;

class EventMetadataServiceImpl implements EventMetadataService {

  private final EventMetadataApi eventMetadataApi;
  private final Workspace workspace;

  EventMetadataServiceImpl(final RequestInterceptor authInterceptor,
      final Workspace workspace, final String url) {
    String apiUrl = StringUtils.isEmpty(url) ? API_MANAGEMENT_URL : url;
    if (authInterceptor == null) {
      throw new IllegalArgumentException(
          "EventMetadataService is missing a authentication interceptor");
    }
    if (workspace == null) {
      throw new IllegalArgumentException("EventMetadataService is missing a workspace context");
    }
    workspace.validateWorkspaceContext();
    this.eventMetadataApi = FeignUtil.getDefaultBuilder()
        .requestInterceptor(authInterceptor)
        .target(EventMetadataApi.class, apiUrl);
    this.workspace = workspace;
  }

  @Override
  public List<EventMetadata> getEventMetadata(String providerId) {
    Optional<EventMetadataCollection> eventMetadataCollection =
        eventMetadataApi.findByProviderId(providerId);
    if (eventMetadataCollection.isPresent()) {
      return eventMetadataCollection.get().getEventMetadata();
    } else {
      return new ArrayList<>();
    }
  }

  @Override
  public Optional<EventMetadata> getEventMetadata(String providerId, String eventCode) {
    return eventMetadataApi.findByProviderIdAndEventCode(providerId, eventCode);
  }

  @Override
  public Optional<EventMetadata> create(String providerId, EventMetadata eventMetadata) {
    return eventMetadataApi.create(
        workspace.getConsumerOrgId(), workspace.getProjectId(), workspace.getWorkspaceId(),
        providerId, eventMetadata);
  }

  @Override
  public Optional<EventMetadata> update(String providerId, EventMetadata eventMetadata) {
    return eventMetadataApi.update(
        workspace.getConsumerOrgId(), workspace.getProjectId(), workspace.getWorkspaceId(),
        providerId, eventMetadata.getEventCode(), eventMetadata);
  }

  @Override
  public void delete(String providerId, String eventCode) {
    eventMetadataApi.deleteByProviderIdAndEventCode(
        workspace.getConsumerOrgId(), workspace.getProjectId(), workspace.getWorkspaceId(),
        providerId, eventCode);
  }

  @Override
  public void deleteAll(String providerId) {
    eventMetadataApi.deleteByProviderId(
        workspace.getConsumerOrgId(), workspace.getProjectId(), workspace.getWorkspaceId(),
        providerId);
  }

}