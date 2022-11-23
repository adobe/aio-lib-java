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
import com.adobe.aio.event.management.api.RegistrationApi;
import com.adobe.aio.event.management.model.Registration;
import com.adobe.aio.event.management.model.RegistrationCreateModel;
import com.adobe.aio.event.management.model.RegistrationPaginatedModel;
import com.adobe.aio.event.management.model.RegistrationUpdateModel;
import com.adobe.aio.feign.AIOHeaderInterceptor;
import com.adobe.aio.ims.feign.JWTAuthInterceptor;
import com.adobe.aio.util.feign.FeignUtil;
import com.adobe.aio.workspace.Workspace;
import com.fasterxml.jackson.core.JsonProcessingException;
import feign.RequestInterceptor;
import java.util.ArrayList;
import java.util.List;
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
    return registrationApi.get(workspace.getConsumerOrgId(), workspace.getProjectId(),
        workspace.getWorkspaceId(), registrationId);
  }

  @Override
  public void delete(String registrationId) {
    if (StringUtils.isEmpty(registrationId)) {
      throw new IllegalArgumentException("registrationId cannot be null or empty");
    }
    registrationApi.delete(workspace.getConsumerOrgId(), workspace.getProjectId(),
        workspace.getWorkspaceId(), registrationId);
  }

  @Override
  public Optional<Registration> createRegistration(
      RegistrationCreateModel.Builder registrationCreateModelBuilder) {
    var inputModel = registrationCreateModelBuilder
        .clientId(workspace.getApiKey()).build();
    return registrationApi.post(workspace.getConsumerOrgId(), workspace.getProjectId(),
        workspace.getWorkspaceId(), inputModel);
  }

  @Override
  public Optional<Registration> updateRegistration(String registrationId,
                  RegistrationUpdateModel.Builder registrationUpdateModelBuilder) {
    var inputModel = registrationUpdateModelBuilder.build();
    return registrationApi.put(workspace.getConsumerOrgId(), workspace.getProjectId(),
                    workspace.getWorkspaceId(), registrationId, inputModel);
  }

  @Override public List<Registration> getRegistrationsForWorkspace() {
    var registrationCollection = registrationApi.getAllForWorkspace(workspace.getConsumerOrgId(), workspace.getProjectId(),
                    workspace.getWorkspaceId());
    return registrationCollection.map(collection -> new ArrayList<>(collection.getRegistrationHalModels()))
                    .orElseGet(ArrayList::new);
  }

  @Override
  public Optional<RegistrationPaginatedModel> getAllRegistrationsForOrg(final long page, final long size) throws JsonProcessingException {
    return registrationApi.getAllForOrg(workspace.getConsumerOrgId(), page, size);
  }
}
