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
package com.adobe.event.management.api;

import com.adobe.event.management.model.EventMetadata;
import com.adobe.event.management.model.EventMetadataCollection;
import feign.Headers;
import feign.Param;
import feign.RequestLine;
import java.util.Optional;

@Headers({"Accept: application/hal+json"})
public interface EventMetadataApi {

  @RequestLine("GET /events/providers/{providerId}/eventmetadata")
  Optional<EventMetadataCollection> findByProviderId(@Param("providerId") String providerId);

  @RequestLine("GET /events/providers/{providerId}/eventmetadata/{eventCode}")
  Optional<EventMetadata> findByProviderIdAndEventCode(@Param("providerId") String providerId,
      @Param("eventCode") String eventCode);

  @RequestLine("POST /events/{consumerOrgId}/{projectId}/{workspaceId}/providers/{providerId}/eventmetadata")
  @Headers({"Content-Type: application/json"})
  Optional<EventMetadata> create(@Param("consumerOrgId") String consumerOrgId,
      @Param("projectId") String projectId,
      @Param("workspaceId") String workspaceId, @Param("providerId") String providerId,
      EventMetadata body);

  @RequestLine("PUT /events/{consumerOrgId}/{projectId}/{workspaceId}/providers/{providerId}/eventmetadata/{eventCode}")
  @Headers({"Content-Type: application/json"})
  Optional<EventMetadata> update(
      @Param("consumerOrgId") String consumerOrgId, @Param("projectId") String projectId,
      @Param("workspaceId") String workspaceId, @Param("providerId") String providerId,
      @Param("eventCode") String eventCode, EventMetadata body);

  @RequestLine("DELETE /events/{consumerOrgId}/{projectId}/{workspaceId}/providers/{providerId}/eventmetadata")
  void deleteByProviderId(@Param("consumerOrgId") String consumerOrgId,
      @Param("projectId") String projectId, @Param("workspaceId") String workspaceId,
      @Param("providerId") String providerId);

  @RequestLine("DELETE /events/{consumerOrgId}/{projectId}/{workspaceId}/providers/{providerId}/eventmetadata/{eventCode}")
  void deleteByProviderIdAndEventCode(@Param("consumerOrgId") String consumerOrgId,
      @Param("projectId") String projectId, @Param("workspaceId") String workspaceId,
      @Param("providerId") String providerId, @Param("eventCode") String eventCode);

}
