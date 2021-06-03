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

@Headers("Accept: application/json")
public interface RegistrationApi {

  /**
   * Creates a Webhook or a Journal registration
   *
   * @param consumerOrgId Your consumer organization Id
   * @param integrationId The integration Id associated with your project/workspace
   * @param body          your Registration Input
   * @param xImsOrgId     (optional)
   * @return Registration
   */
  @RequestLine("POST /organizations/{consumerOrgId}/integrations/{integrationId}/registrations")
  @Headers({
      "Content-Type: application/json",
      "x-ims-org-id: {xImsOrgId}",
  })
  Registration createRegistration(
      @Param("consumerOrgId") String consumerOrgId,
      @Param("integrationId") String integrationId,
      RegistrationInputModel body,
      @Param("xImsOrgId") String xImsOrgId); // todo test without


}
