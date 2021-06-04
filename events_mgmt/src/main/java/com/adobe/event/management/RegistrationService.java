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

import com.adobe.Workspace;
import com.adobe.event.management.model.Registration;
import com.adobe.event.management.model.RegistrationInputModel;
import feign.RequestInterceptor;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;

public interface RegistrationService {

  Optional<Registration> findById(String registrationId);

  Optional<Registration> createRegistration(
      RegistrationInputModel.Builder registrationInputModelBuilder);

  static Builder builder() {
    return new Builder();
  }

  class Builder {

    private RequestInterceptor authInterceptor;
    private Workspace workspace;
    private String url;

    private static void validateWorkspace(Workspace workspace) {
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
    }

    public Builder authInterceptor(RequestInterceptor authInterceptor) {
      this.authInterceptor = authInterceptor;
      return this;
    }

    public Builder workspace(Workspace workspace) {
      validateWorkspace(workspace);
      this.workspace = workspace;
      return this;
    }

    public Builder url(String url) {
      this.url = url;
      return this;
    }

    public RegistrationServiceImpl build() {
      return new RegistrationServiceImpl(authInterceptor, workspace, url);
    }
  }
}
