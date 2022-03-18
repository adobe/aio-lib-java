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
package com.adobe.aio.event.management.api;

import com.adobe.aio.event.management.model.Provider;
import com.adobe.aio.event.management.model.ProviderCollection;
import com.adobe.aio.event.management.model.ProviderInputModel;
import feign.Headers;
import feign.Param;
import feign.RequestLine;
import java.util.Optional;

@Headers({"Accept: application/hal+json"})
public interface ProviderApi {

  /**
   * @param id            The provider uuid
   * @param eventmetadata if set to true the event_metadata will also be fetched
   * @return the associated Adobe I/O Events Provider
   */
  @RequestLine("GET /events/providers/{id}?eventmetadata={eventmetadata}")
  Optional<Provider> findById(@Param("id") String id,
      @Param("eventmetadata") Boolean eventmetadata);

  /**
   * @param consumerOrgId The consumer organization Id used to look up the Adobe I/O Events
   *                      Providers
   * @return Providers the Adobe I/O Events Providers entitled to the provided consumerOrgId
   */
  @RequestLine("GET /events/{consumerOrgId}/providers")
  Optional<ProviderCollection> findByConsumerOrgId(
      @Param("consumerOrgId") String consumerOrgId);

  @RequestLine("GET /events/{consumerOrgId}/providers?providerMetadataId={providerMetadataId}&instanceId={instanceId}")
  Optional<ProviderCollection> findBy(
      @Param("consumerOrgId") String consumerOrgId,
      @Param("providerMetadataId") String providerMetadataId,
      @Param("instanceId") String instanceId);


  @RequestLine("POST /events/{consumerOrgId}/{projectId}/{workspaceId}/providers")
  @Headers({"Content-Type: application/json"})
  Optional<Provider> create(
      @Param("consumerOrgId") String consumerOrgId,
      @Param("projectId") String projectId,
      @Param("workspaceId") String workspaceId,
      ProviderInputModel body);

  @RequestLine("PUT /events/{consumerOrgId}/{projectId}/{workspaceId}/providers/{providerId}")
  @Headers({"Content-Type: application/json"})
  Optional<Provider> update(
      @Param("consumerOrgId") String consumerOrgId,
      @Param("projectId") String projectId,
      @Param("workspaceId") String workspaceId,
      @Param("providerId") String providerId,
      ProviderInputModel body);

  @RequestLine("DELETE /events/{consumerOrgId}/{projectId}/{workspaceId}/providers/{providerId}")
  void delete(@Param("consumerOrgId") String consumerOrgId,
      @Param("projectId") String projectId,
      @Param("workspaceId") String workspaceId,
      @Param("providerId") String providerId);

}
