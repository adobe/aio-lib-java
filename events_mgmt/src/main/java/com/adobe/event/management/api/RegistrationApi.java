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

import com.adobe.event.management.model.Registration;
import com.adobe.event.management.model.RegistrationInputModel;
import feign.Headers;
import feign.Param;
import feign.RequestLine;
import java.util.Optional;

@Headers("Accept: application/json")
public interface RegistrationApi {

  /**
   * Creates a Webhook or a Journal registration
   *
   * @param imsOrgId      your Ims Org Id
   * @param consumerOrgId Your consumer organization Id
   * @param credentialId  The integration Id associated with your project/workspace
   * @param body          your Registration Input
   * @return Registration
   */
  @RequestLine("POST /events/organizations/{consumerOrgId}/integrations/{credentialId}/registrations")
  @Headers({
      "Content-Type: application/json",
      "x-ims-org-id: {imsOrgId}",
  })
  Optional<Registration> post(
      @Param("imsOrgId") String imsOrgId,
      @Param("consumerOrgId") String consumerOrgId,
      @Param("credentialId") String credentialId,
      RegistrationInputModel body
  );

  /**
   * GET a registration
   *
   * @param imsOrgId       your Ims Org Id
   * @param consumerOrgId  Your consumer organization Id
   * @param credentialId   The integration Id associated with your project/workspace
   * @param registrationId The Registration Id
   * @return Registration
   */
  @RequestLine("GET /events/organizations/{consumerOrgId}/integrations/{credentialId}/registrations/{registrationId}")
  @Headers({
      "Content-Type: application/json",
      "x-ims-org-id: {imsOrgId}",
  })
  Optional<Registration> get(
      @Param("imsOrgId") String imsOrgId,
      @Param("consumerOrgId") String consumerOrgId,
      @Param("credentialId") String credentialId,
      @Param("registrationId") String registrationId
  );

  /**
   * DELETE a registration
   *
   * @param imsOrgId       your Ims Org Id
   * @param consumerOrgId  Your consumer organization Id
   * @param credentialId   The integration Id associated with your project/workspace
   * @param registrationId The Registration Id
   */
  @RequestLine("DELETE /events/organizations/{consumerOrgId}/integrations/{credentialId}/registrations/{registrationId}")
  @Headers({
      "Content-Type: application/json",
      "x-ims-org-id: {imsOrgId}",
  })
  void delete(
      @Param("imsOrgId") String imsOrgId,
      @Param("consumerOrgId") String consumerOrgId,
      @Param("credentialId") String credentialId,
      @Param("registrationId") String registrationId
  );


}
