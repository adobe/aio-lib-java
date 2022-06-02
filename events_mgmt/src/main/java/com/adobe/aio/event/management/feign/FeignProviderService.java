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

import com.adobe.aio.event.management.ProviderService;
import com.adobe.aio.feign.AIOHeaderInterceptor;
import com.adobe.aio.ims.feign.JWTAuthInterceptor;
import com.adobe.aio.workspace.Workspace;
import com.adobe.aio.event.management.api.EventMetadataApi;
import com.adobe.aio.event.management.api.ProviderApi;
import com.adobe.aio.event.management.model.EventMetadata;
import com.adobe.aio.event.management.model.EventMetadataCollection;
import com.adobe.aio.event.management.model.Provider;
import com.adobe.aio.event.management.model.ProviderCollection;
import com.adobe.aio.event.management.model.ProviderInputModel;
import com.adobe.aio.util.Constants;
import com.adobe.aio.util.feign.FeignUtil;
import feign.RequestInterceptor;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FeignProviderService implements ProviderService {

  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  private final ProviderApi providerApi;
  private final EventMetadataApi eventMetadataApi;
  private final Workspace workspace;

  public FeignProviderService(final Workspace workspace, final String url) {
    String apiUrl = StringUtils.isEmpty(url) ? API_MANAGEMENT_URL : url;
    
    if (workspace == null) {
      throw new IllegalArgumentException("ProviderService is missing a workspace context");
    }
    workspace.validateWorkspaceContext();
    RequestInterceptor authInterceptor = JWTAuthInterceptor.builder().workspace(workspace).build();
    RequestInterceptor aioHeaderInterceptor = AIOHeaderInterceptor.builder().workspace(workspace).build();

    this.providerApi = FeignUtil.getDefaultBuilder()
        .requestInterceptor(authInterceptor)
        .requestInterceptor(aioHeaderInterceptor)
        .errorDecoder(new ConflictErrorDecoder())
        .target(ProviderApi.class, apiUrl);
    this.eventMetadataApi = FeignUtil.getDefaultBuilder()
        .requestInterceptor(authInterceptor)
        .requestInterceptor(aioHeaderInterceptor)
        .target(EventMetadataApi.class, apiUrl);
    this.workspace = workspace;
  }

  @Override
  public List<Provider> getProviders() {
    Optional<ProviderCollection> providers = providerApi
        .findByConsumerOrgId(workspace.getConsumerOrgId());
    if (providers.isPresent()) {
      return providers.get().getProviders();
    } else {
      return new ArrayList<>();
    }
  }

  @Override
  public Optional<Provider> findProviderById(final String providerId) {
    return providerApi.findById(providerId, true);
  }

  @Override
  public void deleteProvider(final String providerId) {
    logger.info("Deleting provider id `{}`",providerId);
    providerApi.delete(workspace.getConsumerOrgId(), workspace.getProjectId(),
        workspace.getWorkspaceId(), providerId);
  }

  @Override
  public Optional<Provider> createProvider(final ProviderInputModel providerInputModel) {
    logger.info("Creating provider using `{}`",providerInputModel);
    return providerApi.create(workspace.getConsumerOrgId(), workspace.getProjectId(),
        workspace.getWorkspaceId(), providerInputModel);
  }

  @Override
  public Optional<Provider> createOrUpdateProvider(final ProviderInputModel providerInputModel) {
    try {
      return createProvider(providerInputModel);
    }
    catch (ConflictException e){
      String providerId = e.getConflictingId().orElseThrow(()->e);
      logger.info("Another provider (id:`{}`) exist with conflicting natural keys, "
            + "trying to update it ...",providerId);
      return updateProvider(providerId,providerInputModel);
    }
  }

  @Override
  public Optional<Provider> updateProvider(final String id,
      final ProviderInputModel providerUpdateModel) {
    logger.info("Updating provider `{}` using `{}`",id, providerUpdateModel);
    return providerApi.update(workspace.getConsumerOrgId(), workspace.getProjectId(),
        workspace.getWorkspaceId(), id, providerUpdateModel);
  }

  @Override
  public Optional<Provider> findCustomEventsProviderByInstanceId(final String instanceId){
    return findProviderBy(Constants.CUSTOM_EVENTS_PROVIDER_METADATA_ID,instanceId);
  }

  @Override
  public Optional<Provider> findProviderBy(final String providerMetadataId,
      final String instanceId) {
    if (StringUtils.isEmpty(providerMetadataId) || StringUtils
        .isEmpty(workspace.getConsumerOrgId())) {
      throw new IllegalArgumentException(
          "You must specify at least a non empty consumerOrgId and providerMetadataId");
    }
    Optional<ProviderCollection> providerCollection = providerApi
        .findBy(workspace.getConsumerOrgId(), providerMetadataId, instanceId);
    if (providerCollection.isPresent()) {
      if (providerCollection.get().getProviders().isEmpty()) {
        return Optional.empty();
      } else {
        return Optional.of(providerCollection.get().getProviders().get(0));
        // there can only be one by API contract
      }
    } else {
      return Optional.empty();
    }
  }

  @Override
  public List<EventMetadata> getEventMetadata(final String providerId) {
    Optional<EventMetadataCollection> eventMetadataCollection =
        eventMetadataApi.findByProviderId(providerId);
    if (eventMetadataCollection.isPresent()) {
      return eventMetadataCollection.get().getEventMetadata();
    } else {
      return new ArrayList<>();
    }
  }

  @Override
  public Optional<EventMetadata> getEventMetadata(final String providerId, final String eventCode) {
    return eventMetadataApi.findByProviderIdAndEventCode(providerId, eventCode);
  }

  @Override
  public Optional<EventMetadata> createEventMetadata(final String providerId,
      final EventMetadata eventMetadata) {
    logger.info("Creating Event Metadata for provider `{}` using `{}`", providerId, eventMetadata);
    return eventMetadataApi.create(
        workspace.getConsumerOrgId(), workspace.getProjectId(), workspace.getWorkspaceId(),
        providerId, eventMetadata);
  }

  @Override
  public Optional<EventMetadata> updateEventMetadata(final String providerId,
      final EventMetadata eventMetadata) {
    logger.info("Updating Event Metadata for provider `{}` using `{}`", providerId, eventMetadata);
    return eventMetadataApi.update(
        workspace.getConsumerOrgId(), workspace.getProjectId(), workspace.getWorkspaceId(),
        providerId, eventMetadata.getEventCode(), eventMetadata);
  }

  @Override
  public void deleteEventMetadata(final String providerId, final String eventCode) {
    logger.info("Deleting Event Metadata for provider `{}` and eventCode `{}`", providerId, eventCode);
    eventMetadataApi.deleteByProviderIdAndEventCode(
        workspace.getConsumerOrgId(), workspace.getProjectId(), workspace.getWorkspaceId(),
        providerId, eventCode);
  }

  @Override
  public void deleteAllEventMetadata(final String providerId) {
    logger.info("Deleting All Event Metadata for provider `{}`", providerId);
    eventMetadataApi.deleteByProviderId(
        workspace.getConsumerOrgId(), workspace.getProjectId(), workspace.getWorkspaceId(),
        providerId);
  }

}
