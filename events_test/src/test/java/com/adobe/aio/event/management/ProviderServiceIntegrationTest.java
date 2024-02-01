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

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.adobe.aio.event.management.model.EventMetadata;
import com.adobe.aio.event.management.model.Provider;
import com.adobe.aio.util.WorkspaceUtil;
import org.junit.jupiter.api.Test;

import static com.adobe.aio.event.management.model.ProviderInputModel.*;

import static org.junit.jupiter.api.Assertions.*;

public class ProviderServiceIntegrationTest extends ProviderServiceTester {

  public static final String TEST_EVENT_CODE = "com.adobe.aio.event.management.test.event";
  public static final String TEST_EVENT_PROVIDER_LABEL = "com.adobe.aio.event.management.test";

  public ProviderServiceIntegrationTest() {
    super();
  }

  @Test
  public void missingWorkspace() {
    assertThrows(IllegalArgumentException.class, () -> ProviderService.builder().build());
  }

  @Test
  public void getProvidersWithInvalidConsumerOrgId() {
    ProviderService providerService = ProviderService.builder()
        .workspace(WorkspaceUtil.getSystemWorkspaceBuilder()
            .consumerOrgId("invalid").build())
        .url(WorkspaceUtil.getSystemProperty(WorkspaceUtil.API_URL))
        .build();
    assertTrue(providerService.getProviders().isEmpty());
  }

  @Test
  public void invalidFindByArg() {
    assertThrows(IllegalArgumentException.class, () -> providerService.findProviderBy("", ""));
  }

  @Test
  public void getNotFound() {
    String idNotToBeFound = "this_id_should_not_exist";
    assertFalse(providerService.findProviderById(idNotToBeFound).isPresent());
    assertTrue(providerService.getEventMetadata(idNotToBeFound).isEmpty());
    assertFalse(
        providerService.findCustomEventsProviderByInstanceId(idNotToBeFound).isPresent());
  }

  @Test
  public void createGetUpdateDelete() {
    Provider provider = createOrUpdateProvider(TEST_EVENT_PROVIDER_LABEL, TEST_EVENT_CODE);
    String providerId = provider.getId();
    try {
      String instanceId = provider.getInstanceId();

      assertEquals(1, providerService.getEventMetadata(providerId).size());
      logger.info("Fetched All EventMetadata `{}` of AIO Events Provider `{}`", providerId);

      String updatedEventMetadataDescription = "updated EventMetadata Description";
      Optional<EventMetadata> eventMetadata = providerService.updateEventMetadata(providerId,
          getTestEventMetadataBuilder(TEST_EVENT_CODE).description(updatedEventMetadataDescription)
              .build());
      assertTrue(eventMetadata.isPresent());
      logger.info("Updated EventMetadata `{}` of AIO Events Provider `{}`", eventMetadata,
          providerId);
      assertEquals(updatedEventMetadataDescription, eventMetadata.get().getDescription());

      Optional<EventMetadata> eventMetadataFromGet = providerService.getEventMetadata(providerId,
          TEST_EVENT_CODE);
      assertTrue(eventMetadataFromGet.isPresent());
      assertEquals(eventMetadata, eventMetadataFromGet);
      logger.info("Fetched EventMetadata `{}` of AIO Events Provider `{}`", eventMetadataFromGet,
          providerId);

      Optional<Provider> providerById = providerService.findProviderById(providerId);
      assertTrue(providerById.isPresent());
      List<EventMetadata> eventMetadataList = providerService.getEventMetadata(providerId);
      assertTrue(eventMetadataList.size() == 1);
      assertEquals(eventMetadata.get(), eventMetadataList.get(0));
      logger.info("Found AIO Events Provider `{}` by Id", providerById);

      Optional<Provider> providerByInstanceId = providerService.findCustomEventsProviderByInstanceId(
          instanceId);
      assertTrue(providerByInstanceId.isPresent());
      assertEquals(providerId, providerByInstanceId.get().getId());
      logger.info("Found AIO Events Provider `{}` by InstanceId", providerById);

      providerService.deleteEventMetadata(providerId, TEST_EVENT_CODE);
      assertFalse(providerService.getEventMetadata(providerId, TEST_EVENT_CODE).isPresent());
      assertTrue(providerService.getEventMetadata(providerId).isEmpty());
      logger.info("Deleted EventMetadata {} from AIO Events Provider `{}`", TEST_EVENT_CODE,
          providerById);

      // update provider and event metadata
      String updatedProviderDescription = "updated Provider Description";
      Provider updatedProvider = createOrUpdateProvider(getTestProviderInputModelBuilder(TEST_EVENT_PROVIDER_LABEL)
              .instanceId(instanceId)
              .description(updatedProviderDescription)
              .eventDeliveryFormat(DELIVERY_FORMAT_ADOBE_IO)
              .build(), Collections.singleton(getTestEventMetadataBuilder(TEST_EVENT_CODE).build()));
      assertTrue(updatedProvider != null);
      logger.info("Updated AIO Events Provider: {}", provider);
      assertEquals(providerId, updatedProvider.getId());
      assertEquals(updatedProviderDescription, updatedProvider.getDescription());
      assertEquals(DELIVERY_FORMAT_ADOBE_IO, updatedProvider.getEventDeliveryFormat());

      updatedEventMetadataDescription = "updated EventMetadata Description";
      eventMetadata = providerService.updateEventMetadata(providerId,
          getTestEventMetadataBuilder(TEST_EVENT_CODE).description(updatedEventMetadataDescription)
              .build());

      assertTrue(eventMetadata.isPresent());
      logger.info("Updated EventMetadata `{}` of AIO Events Provider `{}`", eventMetadata,
          providerId);
      assertEquals(updatedEventMetadataDescription, eventMetadata.get().getDescription());

      providerService.deleteAllEventMetadata(providerId);
      assertTrue(providerService.getEventMetadata(providerId).isEmpty());
      logger.info("Deleted All EventMetadata from AIO Events Provider `{}`", providerId);
    } catch (Exception e) {
      logger.error(e.getMessage(), e);
      fail(e.getMessage());
    } finally {
      deleteProvider(providerId);
    }
  }

}
