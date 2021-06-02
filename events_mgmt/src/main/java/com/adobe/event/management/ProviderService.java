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

import com.adobe.event.management.api.ProviderApi;
import com.adobe.event.management.model.Provider;
import feign.RequestInterceptor;
import java.util.List;
import java.util.Optional;

public interface ProviderService {

  List<Provider> getProviders();

  Optional<Provider> findById(String id);

  Optional<Provider> findBy(String providerMetadataId, String instanceId);

  static Builder builder() {
    return new Builder();
  }

  class Builder {

    private RequestInterceptor authInterceptor;
    private String consumerOrgId;
    private String url = ProviderApi.DEFAULT_URL;

    public Builder() {
    }

    public Builder authInterceptor(RequestInterceptor authInterceptor) {
      this.authInterceptor = authInterceptor;
      return this;
    }

    public Builder consumerOrgId(String consumerOrgId) {
      this.consumerOrgId = consumerOrgId;
      return this;
    }

    public Builder url(String url) {
      this.url = url;
      return this;
    }

    public ProviderService build() {
      return new ProviderServiceImpl(authInterceptor, consumerOrgId, url);
    }
  }
}
