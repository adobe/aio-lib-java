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

import static com.adobe.aio.event.management.model.ProviderInputModel.DELIVERY_FORMAT_ADOBE_IO;
import static com.adobe.aio.event.management.model.ProviderInputModel.DELIVERY_FORMAT_CLOUD_EVENTS_V1;
import static com.adobe.aio.util.Constants.CUSTOM_EVENTS_PROVIDER_METADATA_ID;

import com.adobe.aio.event.management.ProviderService;
import com.adobe.aio.event.management.model.EventMetadata;
import com.adobe.aio.event.management.model.Provider;
import com.adobe.aio.event.management.model.ProviderInputModel;
import com.adobe.aio.event.publish.model.CloudEvent;
import com.adobe.aio.util.WorkspaceUtil;
import com.adobe.aio.workspace.Workspace;
import java.util.List;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FeignProviderServiceIntegrationTest {

  public static final String TEST_EVENT_CODE = "com.adobe.aio.event.management.test.event";
  public static final String TEST_EVENT_DESC = TEST_EVENT_CODE + " description";
  public static final String TEST_EVENT_PROVIDER_LABEL = "com.adobe.aio.event.management.test";
  public static final String TEST_EVENT_PROVIDER_DESC = TEST_EVENT_PROVIDER_LABEL + " description";
  public static final String TEST_EVENT_PROVIDER_URL = "https://github.com/adobe/aio-lib-java";

  private final static Logger logger = LoggerFactory.getLogger(
      FeignProviderServiceIntegrationTest.class);

  private ProviderService providerService;

  public static EventMetadata.Builder getTestEventMetadataBuilder() {
    return EventMetadata.builder()
        .eventCode(TEST_EVENT_CODE)
        .description(TEST_EVENT_DESC);
  }

  public static ProviderInputModel.Builder getTestProviderInputModelBuilder() {
    return ProviderInputModel.builder()
        .label(TEST_EVENT_PROVIDER_LABEL)
        .description(TEST_EVENT_PROVIDER_DESC)
        .docsUrl(TEST_EVENT_PROVIDER_URL);
  }

  public static Provider createTestProvider(ProviderService providerService) {
    Optional<Provider> provider = providerService.createProvider(
        getTestProviderInputModelBuilder().build());
    Assert.assertTrue(provider.isPresent());
    logger.info("Created AIO Events Provider: {}", provider);
    String providerId = provider.get().getId();
    Assert.assertTrue(StringUtils.isNotBlank(providerId));
    Assert.assertTrue(StringUtils.isNotBlank(provider.get().getInstanceId()));
    Assert.assertNotNull(provider.get().getId());
    Assert.assertNotNull(provider.get().getInstanceId());
    Assert.assertEquals(TEST_EVENT_PROVIDER_LABEL, provider.get().getLabel());
    Assert.assertEquals(TEST_EVENT_PROVIDER_DESC, provider.get().getDescription());
    Assert.assertEquals(TEST_EVENT_PROVIDER_URL, provider.get().getDocsUrl());
    Assert.assertEquals(WorkspaceUtil.getSystemProperty(Workspace.IMS_ORG_ID),
        provider.get().getPublisher());
    Assert.assertEquals(CloudEvent.SOURCE_URN_PREFIX + provider.get().getId(), provider.get().getSource());
    Assert.assertEquals(DELIVERY_FORMAT_CLOUD_EVENTS_V1,provider.get().getEventDeliveryFormat());
    Assert.assertEquals(CUSTOM_EVENTS_PROVIDER_METADATA_ID,provider.get().getProviderMetadata());

    Optional<EventMetadata> eventMetadata = providerService.createEventMetadata(providerId,
        getTestEventMetadataBuilder().build());
    Assert.assertTrue(eventMetadata.isPresent());
    Assert.assertEquals(TEST_EVENT_CODE, eventMetadata.get().getEventCode());
    Assert.assertEquals(TEST_EVENT_DESC, eventMetadata.get().getDescription());
    Assert.assertEquals(TEST_EVENT_CODE, eventMetadata.get().getLabel());
    logger.info("Added EventMetadata `{}` to AIO Events Provider `{}`", eventMetadata, providerId);
    return provider.get();
  }

  public static void deleteProvider(ProviderService providerService, String providerId) {
    providerService.deleteProvider(providerId);
    logger.info("Deleted AIO Events Provider: {}", providerId);
    Optional deleted = providerService.findProviderById(providerId);
    Assert.assertTrue(deleted.isEmpty());
    logger.info("No more AIO Events Provider with id: {}", providerId);
  }

  @Before
  public void setUp() {
    providerService = ProviderService.builder()
        .workspace(WorkspaceUtil.getSystemWorkspaceBuilder().build())
        .url(WorkspaceUtil.getSystemProperty(WorkspaceUtil.API_URL))
        .build();
  }

  @Test(expected = IllegalArgumentException.class)
  public void missingWorkspace() {
    ProviderService.builder().build();
  }

  @Test
  public void getProvidersWithInvalidConsumerOrgId() {
    ProviderService providerService = ProviderService.builder()
        .workspace(WorkspaceUtil.getSystemWorkspaceBuilder()
            .consumerOrgId("invalid").build())
        .url(WorkspaceUtil.getSystemProperty(WorkspaceUtil.API_URL))
        .build();
    Assert.assertTrue(providerService.getProviders().isEmpty());
  }

  @Test(expected = IllegalArgumentException.class)
  public void invalidFindByArg() {
    providerService.findProviderBy("", "");
  }

  @Test
  public void getNotFound() {
    String idNotToBeFound = "this_id_should_not_exist";
    Assert.assertTrue(providerService.findProviderById(idNotToBeFound).isEmpty());
    Assert.assertTrue(providerService.getEventMetadata(idNotToBeFound).isEmpty());
    Assert.assertTrue(
        providerService.findCustomEventsProviderByInstanceId(idNotToBeFound).isEmpty());
  }

  @Test
  public void createGetUpdateDelete() {
    Provider provider = createTestProvider(providerService);
    String providerId = provider.getId();
    String instanceId = provider.getInstanceId();

    Assert.assertEquals(1, providerService.getEventMetadata(providerId).size());
    logger.info("Fetched All EventMetadata `{}` of AIO Events Provider `{}`", providerId);

    String updatedEventMetadataDescription = "updated EventMetadata Description";
    Optional<EventMetadata> eventMetadata = providerService.updateEventMetadata(providerId,
        getTestEventMetadataBuilder().description(updatedEventMetadataDescription).build());
    Assert.assertTrue(eventMetadata.isPresent());
    logger.info("Updated EventMetadata `{}` of AIO Events Provider `{}`", eventMetadata,
        providerId);
    Assert.assertEquals(updatedEventMetadataDescription, eventMetadata.get().getDescription());

    Optional<EventMetadata> eventMetadataFromGet = providerService.getEventMetadata(providerId,
        TEST_EVENT_CODE);
    Assert.assertTrue(eventMetadataFromGet.isPresent());
    Assert.assertEquals(eventMetadata, eventMetadataFromGet);
    logger.info("Fetched EventMetadata `{}` of AIO Events Provider `{}`", eventMetadataFromGet,
        providerId);

    Optional<Provider> providerById = providerService.findProviderById(providerId);
    Assert.assertTrue(providerById.isPresent());
    List<EventMetadata> eventMetadataList = providerService.getEventMetadata(providerId);
    Assert.assertTrue(eventMetadataList.size() == 1);
    Assert.assertEquals(eventMetadata.get(), eventMetadataList.get(0));
    logger.info("Found AIO Events Provider `{}` by Id", providerById);

    Optional<Provider> providerByInstanceId = providerService.findCustomEventsProviderByInstanceId(
        instanceId);
    Assert.assertTrue(providerByInstanceId.isPresent());
    Assert.assertEquals(providerId, providerByInstanceId.get().getId());
    logger.info("Found AIO Events Provider `{}` by InstanceId", providerById);

    providerService.deleteEventMetadata(providerId, TEST_EVENT_CODE);
    Assert.assertTrue(providerService.getEventMetadata(providerId, TEST_EVENT_CODE).isEmpty());
    Assert.assertTrue(providerService.getEventMetadata(providerId).isEmpty());
    logger.info("Deleted EventMetadata {} from AIO Events Provider `{}`", TEST_EVENT_CODE,
        providerById);

    try {
      providerService.createProvider(
          getTestProviderInputModelBuilder().instanceId(instanceId).build());
      Assert.fail("We should have had a ConflictException thrown");
    } catch (ConflictException ex) {
      logger.info("Cannot create an AIO Events provider with the same instanceId: {}",
          ex.getMessage());
    }

    String updatedProviderDescription = "updated Provider Description";
    Optional<Provider> updatedProvider = providerService.createOrUpdateProvider
        (getTestProviderInputModelBuilder()
            .instanceId(instanceId)
            .description(updatedProviderDescription)
            .eventDeliveryFormat(DELIVERY_FORMAT_ADOBE_IO)
            .build());
    Assert.assertTrue(updatedProvider.isPresent());
    logger.info("Updated AIO Events Provider: {}", provider);
    Assert.assertEquals(providerId, updatedProvider.get().getId());
    Assert.assertEquals(updatedProviderDescription, updatedProvider.get().getDescription());
    Assert.assertEquals(DELIVERY_FORMAT_ADOBE_IO, updatedProvider.get().getEventDeliveryFormat());

    providerService.createEventMetadata(providerId, getTestEventMetadataBuilder().build());
    Assert.assertTrue(eventMetadata.isPresent());
    logger.info("Added EventMetadata `{}` to AIO Events Provider `{}`", eventMetadata, providerId);
    providerService.deleteAllEventMetadata(providerId);
    Assert.assertTrue(providerService.getEventMetadata(providerId).isEmpty());
    logger.info("Deleted All EventMetadata from AIO Events Provider `{}`", providerId);

    deleteProvider(providerService, providerId);
  }

}
