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
import com.adobe.event.management.model.Provider;
import com.adobe.event.management.model.ProviderInputModel;
import feign.RequestInterceptor;
import java.util.List;
import java.util.Optional;

/**
 * This interface methods are returning either Optional or List.
 *
 * When the underlying Adobe IO http API endpoints are responding with `404`,
 * these methods will return empty Optional or empty List.
 *
 * When the underlying Adobe IO http API endpoints are responding with other `4xx` or `5xx` errors,
 * these methods will throw runtime FeignException exposing these error codes.
 *
 * We may work on a more specific error handling as part of https://github.com/adobe/aio-lib-java/issues/7.
 *
 */
public interface ProviderService {

  List<Provider> getProviders();

  Optional<Provider> findProviderById(final String providerId);

  void deleteProvider(final String providerId);

  Optional<Provider> createProvider(final ProviderInputModel providerCreateModel);

  Optional<Provider> updateProvider(final String id, final ProviderInputModel providerUpdateModel);

  Optional<Provider> findProviderBy(final String providerMetadataId, final String instanceId);

  List<EventMetadata> getEventMetadata(final String providerId);

  Optional<EventMetadata> getEventMetadata(final String providerId, final String eventCode);

  Optional<EventMetadata> createEventMetadata(final String providerId, final EventMetadata eventMetadata);

  Optional<EventMetadata> updateEventMetadata(final String providerId, final EventMetadata eventMetadata);

  void deleteEventMetadata(final String providerId, final String eventCode);

  void deleteAllEventMetadata(final String providerId);

  static Builder builder() {
    return new Builder();
  }

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

    public ProviderService build() {
      return new ProviderServiceImpl(authInterceptor, workspace, url);
    }
  }
}
