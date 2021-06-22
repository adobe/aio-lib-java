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
import com.adobe.event.management.model.EventMetadata;
import feign.RequestInterceptor;
import java.util.List;
import java.util.Optional;

public interface EventMetadataService {

  static Builder builder() {
    return new Builder();
  }

  List<EventMetadata> getEventMetadata(String providerId);

  Optional<EventMetadata> getEventMetadata(String providerId, String eventCode);

  Optional<EventMetadata> create(String providerId, EventMetadata eventMetadata);

  Optional<EventMetadata> update(String providerId, EventMetadata eventMetadata);

  void delete(String providerId, String eventCode);

  void deleteAll(String providerId);

  class Builder {

    private RequestInterceptor authInterceptor;
    private Workspace workspace;
    private String url;

    public Builder() {
    }

    public Builder authInterceptor(RequestInterceptor authInterceptor) {
      this.authInterceptor = authInterceptor;
      return this;
    }

    public Builder workspace(Workspace workspace) {
      this.workspace = workspace;
      return this;
    }

    public Builder url(String url) {
      this.url = url;
      return this;
    }

    public EventMetadataService build() {
      return new EventMetadataServiceImpl(authInterceptor, workspace, url);
    }
  }
}
