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
package com.adobe.aio.event.management.feign;

import static com.adobe.aio.util.Constants.API_MANAGEMENT_URL;

import com.adobe.aio.event.management.RegistrationService;
import com.adobe.aio.feign.AIOHeaderInterceptor;
import com.adobe.aio.ims.feign.JWTAuthInterceptor;
import com.adobe.aio.workspace.Workspace;
import com.adobe.aio.event.management.api.RegistrationApi;
import com.adobe.aio.event.management.model.Registration;
import com.adobe.aio.event.management.model.RegistrationInputModel;
import com.adobe.aio.util.feign.FeignUtil;
import feign.RequestInterceptor;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;

public class FeignRegistrationService implements RegistrationService {

  private final RegistrationApi registrationApi;
  private final Workspace workspace;

  public FeignRegistrationService(final Workspace workspace,
                                  final String url) {
    String apiUrl = StringUtils.isEmpty(url) ? API_MANAGEMENT_URL : url;
    if (workspace == null) {
      throw new IllegalArgumentException("RegistrationService is missing a workspace context");
    }
    if (StringUtils.isEmpty(workspace.getImsOrgId())) {
      throw new IllegalArgumentException("Workspace is missing an imsOrgId context");
    }
    if (StringUtils.isEmpty(workspace.getConsumerOrgId())) {
      throw new IllegalArgumentException("Workspace is missing a consumerOrgId context");
    }
    if (StringUtils.isEmpty(workspace.getCredentialId())) {
      throw new IllegalArgumentException("Workspace is missing a credentialId context");
    }
    if (StringUtils.isEmpty(workspace.getApiKey())) {
      throw new IllegalArgumentException("Workspace is missing an apiKey context");
    }
    workspace.validateWorkspaceContext();
    RequestInterceptor authInterceptor = JWTAuthInterceptor.builder().workspace(workspace).build();
    this.registrationApi = FeignUtil.getDefaultBuilder()
        .requestInterceptor(authInterceptor)
        .requestInterceptor(AIOHeaderInterceptor.builder().workspace(workspace).build())
        .target(RegistrationApi.class, apiUrl);
    this.workspace = workspace;
  }

  @Override
  public Optional<Registration> findById(String registrationId) {
    if (StringUtils.isEmpty(registrationId)) {
      throw new IllegalArgumentException("registrationId cannot be null or empty");
    }
    return registrationApi.get(workspace.getImsOrgId(), workspace.getConsumerOrgId(),
        workspace.getCredentialId(), registrationId);
  }

  @Override
  public void delete(String registrationId) {
    if (StringUtils.isEmpty(registrationId)) {
      throw new IllegalArgumentException("registrationId cannot be null or empty");
    }
    registrationApi.delete(workspace.getImsOrgId(), workspace.getConsumerOrgId(),
        workspace.getCredentialId(), registrationId);
  }

  @Override
  public Optional<Registration> createRegistration(
      RegistrationInputModel.Builder registrationInputModelBuilder) {
    RegistrationInputModel inputModel = registrationInputModelBuilder
        .clientId(workspace.getApiKey()).build();
    return registrationApi.post(workspace.getImsOrgId(), workspace.getConsumerOrgId(),
        workspace.getCredentialId(), inputModel);
  }

}
