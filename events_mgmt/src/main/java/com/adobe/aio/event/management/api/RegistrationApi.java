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

import com.adobe.aio.event.management.model.Registration;
import com.adobe.aio.event.management.model.RegistrationCollection;
import com.adobe.aio.event.management.model.RegistrationCreateModel;
import com.adobe.aio.event.management.model.RegistrationPaginatedModel;
import com.adobe.aio.event.management.model.RegistrationUpdateModel;
import feign.Headers;
import feign.Param;
import feign.RequestLine;
import java.util.Optional;

@Headers({"Accept: application/hal+json"})
public interface RegistrationApi {

    /**
     * Creates a Webhook or a Journal registration
     *
     * @param consumerOrgId Your consumer organization Id
     * @param projectId     The Id associated with your project
     * @param workspaceId   The Id associated with your workspace
     * @param body          your Registration Input
     * @return Registration
     */
    @RequestLine("POST /events/{consumerOrgId}/{projectId}/{workspaceId}/registrations")
    @Headers({"Content-Type: application/json"})
    Optional<Registration> post(
      @Param("consumerOrgId") String consumerOrgId,
      @Param("projectId") String projectId,
      @Param("workspaceId") String workspaceId,
      RegistrationCreateModel body
    );

    /**
     * Updates a Webhook or a Journal registration
     *
     * @param consumerOrgId Your consumer organization Id
     * @param projectId     The Id associated with your project
     * @param workspaceId   The Id associated with your workspace
     * @param registrationId The Registration Id
     * @param body          your Registration Input
     * @return Registration
     */
    @RequestLine("PUT /events/{consumerOrgId}/{projectId}/{workspaceId}/registrations/{registrationId}")
    @Headers({"Content-Type: application/json"})
    Optional<Registration> put(
      @Param("consumerOrgId") String consumerOrgId,
      @Param("projectId") String projectId,
      @Param("workspaceId") String workspaceId,
      @Param("registrationId") String registrationId,
      RegistrationUpdateModel body
    );

    /**
     * GET a registration
     *
     * @param consumerOrgId Your consumer organization Id
     * @param projectId     The Id associated with your project
     * @param workspaceId   The Id associated with your workspace
     * @param registrationId The Registration Id
     * @return Registration
     */
    @RequestLine("GET /events/{consumerOrgId}/{projectId}/{workspaceId}/registrations/{registrationId}")
    @Headers({"Content-Type: application/json"})
    Optional<Registration> get(
      @Param("consumerOrgId") String consumerOrgId,
      @Param("projectId") String projectId,
      @Param("workspaceId") String workspaceId,
      @Param("registrationId") String registrationId
    );

    /**
     * GET all registrations for a project/workspace
     *
     * @param consumerOrgId Your consumer organization Id
     * @param projectId     The Id associated with your project
     * @param workspaceId   The Id associated with your workspace
     * @return Registration
     */
    @RequestLine("GET /events/{consumerOrgId}/{projectId}/{workspaceId}/registrations")
    @Headers({"Content-Type: application/json"})
    Optional<RegistrationCollection> getAllForWorkspace(
      @Param("consumerOrgId") String consumerOrgId,
      @Param("projectId") String projectId,
      @Param("workspaceId") String workspaceId);

    /**
     * GET all registrations for an org
     *
     * @param consumerOrgId Your consumer organization Id
     * @return Registration
     */
    @RequestLine("GET /events/{consumerOrgId}/registrations?page={page}&size={size}")
    @Headers({"Content-Type: application/json"})
    Optional<RegistrationPaginatedModel> getAllForOrg(
      @Param("consumerOrgId") String consumerOrgId,
      @Param("page") Long page,
      @Param("size") Long size);

    /**
     * DELETE a registration
     *
     * @param consumerOrgId Your consumer organization Id
     * @param projectId     The Id associated with your project
     * @param workspaceId   The Id associated with your workspace
     * @param registrationId The Registration Id
     */
    @RequestLine("DELETE /events/{consumerOrgId}/{projectId}/{workspaceId}/registrations/{registrationId}")
    void delete(
      @Param("consumerOrgId") String consumerOrgId,
      @Param("projectId") String projectId,
      @Param("workspaceId") String workspaceId,
      @Param("registrationId") String registrationId
    );


}
