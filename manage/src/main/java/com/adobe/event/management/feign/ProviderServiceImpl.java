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
package com.adobe.event.management.feign;

import com.adobe.event.management.ProviderService;
import com.adobe.event.management.model.Provider;
import com.adobe.event.management.model.Providers;
import com.adobe.util.FeignUtil;
import feign.RequestInterceptor;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;

public class ProviderServiceImpl implements ProviderService {

  private final ProviderApi providerApi;

  public static ProviderService build(final RequestInterceptor authInterceptor) {
    return build(authInterceptor, ProviderApi.DEFAULT_URL);
  }

  public static ProviderService build(final RequestInterceptor authInterceptor, final String url) {
    return new ProviderServiceImpl(authInterceptor, url);
  }

  private ProviderServiceImpl(final RequestInterceptor authInterceptor, final String url) {
    this.providerApi = FeignUtil.getDefaultBuilderWithJacksonEncoder()
        .requestInterceptor(authInterceptor)
        .decode404()
        .target(ProviderApi.class, url);
  }

  @Override
  public List<Provider> getProviders(String consumerOrgId) {
    if (StringUtils.isEmpty(consumerOrgId)) {
      throw new IllegalArgumentException("You must specify a non empty consumerOrgId");
    }
    Providers providers = providerApi.findByConsumerOrgId(consumerOrgId);
    if (providers != null && providers.getProviderCollection() != null) {
      return providers.getProviderCollection().getProviders();
    } else {
      return new ArrayList<>();
    }
  }

  @Override
  public Optional<Provider> findById(String id) {
    return Optional.ofNullable(providerApi.findById(id));
  }

  @Override
  public Optional<Provider> findBy(String consumerOrgId, String providerMetadataId,
      String instanceId) {
    if (StringUtils.isEmpty(providerMetadataId) || StringUtils.isEmpty(consumerOrgId)) {
      throw new IllegalArgumentException(
          "You must specify at least a non empty consumerOrgId and providerMetadataId");
    }
    Providers providers = providerApi.findBy(consumerOrgId, providerMetadataId, instanceId);
    if (providers != null && providers.getProviderCollection() != null
        && providers.getProviderCollection().getProviders() != null) {
      if (providers.getProviderCollection().getProviders().isEmpty()) {
        return Optional.empty();
      } else {
        return Optional.of(providers.getProviderCollection().getProviders().get(1));
        // there can only be one by API contract
      }
    } else {
      return Optional.empty();
    }
  }

}