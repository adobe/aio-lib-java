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

import com.adobe.aio.event.management.model.EventMetadata;
import com.adobe.aio.event.management.model.Provider;
import com.adobe.aio.event.management.model.ProviderInputModel;
import com.adobe.aio.event.publish.model.CloudEvent;
import com.adobe.aio.util.WorkspaceUtil;
import com.adobe.aio.workspace.Workspace;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProviderServiceTester {

  public static final String DEFAULT_DESC_SUFFIX = " description";
  public static final String DEFAULT_PROVIDER_DOCS_URL = "https://github.com/adobe/aio-lib-java";

  protected final Logger logger = LoggerFactory.getLogger(this.getClass());
  protected final ProviderService providerService;

  public ProviderServiceTester() {
    this.providerService = ProviderService.builder()
        .workspace(WorkspaceUtil.getSystemWorkspaceBuilder().build())
        .url(WorkspaceUtil.getSystemProperty(WorkspaceUtil.API_URL))
        .build();
  }

  public static EventMetadata.Builder getTestEventMetadataBuilder(String eventCode) {
    return EventMetadata.builder()
        .eventCode(eventCode)
        .description(eventCode + DEFAULT_DESC_SUFFIX);
  }

  public static ProviderInputModel.Builder getTestProviderInputModelBuilder(String label) {
    return ProviderInputModel.builder()
        .label(label)
        .description(label + DEFAULT_DESC_SUFFIX)
        .docsUrl(DEFAULT_PROVIDER_DOCS_URL);
  }

  public ProviderService getProviderService(){
    return providerService;
  }

  public Provider createOrUpdateProvider(String providerLabel, String eventCode) {
    return createOrUpdateProvider(getTestProviderInputModelBuilder(providerLabel).build(),
        Set.of(getTestEventMetadataBuilder(eventCode).build()));
  }

  public Provider createOrUpdateProvider(ProviderInputModel providerInputModel,
      Set<EventMetadata> eventMetadataSet) {
    return assertProviderResponseAndCreateEventMetadata(providerInputModel, eventMetadataSet,
        () -> providerService.createOrUpdateProvider(providerInputModel));
  }

  public Provider createProvider(ProviderInputModel providerInputModel,
      Set<EventMetadata> eventMetadataSet) {
    return assertProviderResponseAndCreateEventMetadata(providerInputModel, eventMetadataSet,
        () -> providerService.createProvider(providerInputModel));
  }

  private Provider assertProviderResponseAndCreateEventMetadata(ProviderInputModel providerInputModel,
      Set<EventMetadata> eventMetadataSet, Supplier<Optional<Provider>> providerSupplier) {
    Optional<Provider> provider = providerSupplier.get();
    Assert.assertTrue(provider.isPresent());
    logger.info("Created AIO Events Provider: {}", provider);
    String providerId = provider.get().getId();
    Assert.assertTrue(StringUtils.isNotBlank(providerId));
    Assert.assertTrue(StringUtils.isNotBlank(provider.get().getInstanceId()));
    Assert.assertNotNull(provider.get().getId());
    Assert.assertNotNull(provider.get().getInstanceId());
    Assert.assertEquals(providerInputModel.getLabel(), provider.get().getLabel());
    Assert.assertEquals(providerInputModel.getDescription(), provider.get().getDescription());
    Assert.assertEquals(providerInputModel.getDocsUrl(), provider.get().getDocsUrl());
    Assert.assertEquals(WorkspaceUtil.getSystemProperty(Workspace.IMS_ORG_ID),
        provider.get().getPublisher());
    Assert.assertEquals(CloudEvent.SOURCE_URN_PREFIX + provider.get().getId(),
        provider.get().getSource());
    Assert.assertEquals(providerInputModel.getProviderMetadataId(),
        provider.get().getProviderMetadata());

    for (EventMetadata eventMetadataInput : eventMetadataSet) {
      Optional<EventMetadata> eventMetadata = providerService.createEventMetadata(providerId,
          eventMetadataInput);
      Assert.assertTrue(eventMetadata.isPresent());
      Assert.assertEquals(eventMetadataInput.getEventCode(), eventMetadata.get().getEventCode());
      Assert.assertEquals(eventMetadataInput.getDescription(),
          eventMetadata.get().getDescription());
      Assert.assertEquals(eventMetadataInput.getEventCode(), eventMetadata.get().getLabel());
      logger.info("Added EventMetadata `{}` to AIO Events Provider `{}`", eventMetadata,
          providerId);
    }
    return provider.get();
  }

  public void deleteProvider(String providerId) {
    providerService.deleteProvider(providerId);
    logger.info("Deleted AIO Events Provider: {}", providerId);
    Optional deleted = providerService.findProviderById(providerId);
    Assert.assertTrue(deleted.isEmpty());
    logger.info("No more AIO Events Provider with id: {}", providerId);
  }

}
