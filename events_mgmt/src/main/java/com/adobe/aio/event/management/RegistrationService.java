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
package com.adobe.aio.event.management;

import com.adobe.aio.event.management.feign.FeignRegistrationService;
import com.adobe.aio.workspace.Workspace;
import com.adobe.aio.event.management.model.Registration;
import com.adobe.aio.event.management.model.RegistrationInputModel;
import java.util.Optional;

public interface RegistrationService {

  Optional<Registration> findById(String registrationId);

  void delete(String registrationId);

  Optional<Registration> createRegistration(
      RegistrationInputModel.Builder registrationInputModelBuilder);


  static Builder builder() {
    return new Builder();
  }

  class Builder {

    private Workspace workspace;
    private String url;


    public Builder workspace(Workspace workspace) {
      this.workspace = workspace;
      return this;
    }

    public Builder url(String url) {
      this.url = url;
      return this;
    }

    public RegistrationService build() {
      return new FeignRegistrationService(workspace, url);
    }
  }
}
