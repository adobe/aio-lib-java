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

import com.adobe.aio.event.management.ProviderService;
import com.adobe.aio.event.management.model.EventMetadata;
import com.adobe.aio.event.management.model.EventMetadataModelTest;
import com.adobe.aio.event.management.model.Provider;
import com.adobe.aio.event.management.model.ProviderInputModelTest;
import com.adobe.aio.ims.util.TestUtil;
import java.util.List;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FeignProviderServiceIntegrationTest {

  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  private ProviderService providerService;

  @Before
  public void setUp() {
    providerService = ProviderService.builder()
        .workspace(TestUtil.getTestWorkspaceBuilder().build())
        .url(TestUtil.getTestProperty(TestUtil.API_URL))
        .build();
  }

  @Test(expected = IllegalArgumentException.class)
  public void missingWorkspace() {
    ProviderService.builder().build();
  }

  @Test
  public void getProvidersWithInvalidConsumerOrgId() {
    ProviderService providerService = ProviderService.builder()
        .workspace(TestUtil.getTestWorkspaceBuilder()
            .consumerOrgId("invalid").build())
        .url(TestUtil.getTestProperty(TestUtil.API_URL))
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
    Optional<Provider> provider = providerService.createProvider(
        ProviderInputModelTest.getProviderInputModelBuilder().build());
    Assert.assertTrue(provider.isPresent());
    logger.info("Created AIO Events Provider: {}", provider);
    String providerId = provider.get().getId();
    String instanceId = provider.get().getInstanceId();
    Assert.assertTrue(StringUtils.isNotBlank(providerId));

    Assert.assertTrue(providerService.getEventMetadata(providerId).isEmpty());
    logger.info("Fetched All EventMetadata `{}` of AIO Events Provider `{}`", providerId);

    Optional<EventMetadata> eventMetadata = providerService.createEventMetadata(providerId,
        EventMetadataModelTest.getTestEventMetadataBuilder().build());
    Assert.assertTrue(eventMetadata.isPresent());
    logger.info("Added EventMetadata `{}` to AIO Events Provider `{}`", eventMetadata, providerId);

    Assert.assertEquals(1, providerService.getEventMetadata(providerId).size());
    logger.info("Fetched All EventMetadata `{}` of AIO Events Provider `{}`", providerId);

    String updatedEventMetadataDescription = "updated EventMetadata Description";
    eventMetadata = providerService.updateEventMetadata(providerId,
        EventMetadataModelTest.getTestEventMetadataBuilder()
            .description(updatedEventMetadataDescription).build());
    Assert.assertTrue(eventMetadata.isPresent());
    logger.info("Updated EventMetadata `{}` of AIO Events Provider `{}`", eventMetadata,
        providerId);
    Assert.assertEquals(updatedEventMetadataDescription, eventMetadata.get().getDescription());

    Optional<EventMetadata> eventMetadataFromGet = providerService.getEventMetadata(providerId,
        EventMetadataModelTest.TEST_EVENT_CODE);
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

    providerService.deleteEventMetadata(providerId, EventMetadataModelTest.TEST_EVENT_CODE);
    Assert.assertTrue(providerService.getEventMetadata(providerId,
        EventMetadataModelTest.TEST_EVENT_CODE).isEmpty());
    Assert.assertTrue(providerService.getEventMetadata(providerId).isEmpty());
    logger.info("Deleted EventMetadata {} from AIO Events Provider `{}`",
        EventMetadataModelTest.TEST_EVENT_CODE, providerById);

    String updatedProviderDescription = "updated Provider Description";
    provider = providerService.createOrUpdateProvider
        (ProviderInputModelTest.getProviderInputModelBuilder()
            .instanceId(instanceId)
            .description(updatedProviderDescription).build());
    Assert.assertTrue(provider.isPresent());
    logger.info("Updated AIO Events Provider: {}", provider);
    Assert.assertEquals(providerId, provider.get().getId());
    Assert.assertEquals(updatedProviderDescription, provider.get().getDescription());

    providerService.createEventMetadata(providerId,
        EventMetadataModelTest.getTestEventMetadataBuilder().build());
    Assert.assertTrue(eventMetadata.isPresent());
    logger.info("Added EventMetadata `{}` to AIO Events Provider `{}`", eventMetadata, providerId);
    providerService.deleteAllEventMetadata(providerId);
    Assert.assertTrue(providerService.getEventMetadata(providerId).isEmpty());
    logger.info("Deleted All EventMetadata from AIO Events Provider `{}`", providerById);

    providerService.deleteProvider(providerId);
    logger.info("Deleted AIO Events Provider: {}", providerById);

    providerById = providerService.findProviderById(providerId);
    Assert.assertTrue(providerById.isEmpty());
    logger.info("No more AIO Events Provider with id: {}", providerId);
  }

}
