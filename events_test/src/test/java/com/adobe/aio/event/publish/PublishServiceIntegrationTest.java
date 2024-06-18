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
package com.adobe.aio.event.publish;

import static com.adobe.aio.event.management.ProviderServiceIntegrationTest.TEST_EVENT_CODE;
import static com.adobe.aio.event.management.ProviderServiceIntegrationTest.TEST_EVENT_PROVIDER_LABEL;
import static com.adobe.aio.event.management.RegistrationServiceIntegrationTest.TEST_REGISTRATION_NAME;
import static org.junit.jupiter.api.Assertions.*;

import com.adobe.aio.event.management.ProviderServiceTester;
import com.adobe.aio.event.management.RegistrationServiceTester;
import com.adobe.aio.exception.AIOException;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

public class PublishServiceIntegrationTest extends PublishServiceTester {

  private final ProviderServiceTester providerServiceTester;
  private final RegistrationServiceTester registrationServiceTester;

  public PublishServiceIntegrationTest() {
    super();
    providerServiceTester = new ProviderServiceTester();
    registrationServiceTester = new RegistrationServiceTester();
  }

  @Test
  public void missingWorkspace() {
    assertThrows(IllegalArgumentException.class, () -> PublishService.builder().workspace(null).build());
  }

  @Test
  public void publishEventTest() {
    String providerId = null;
    String registrationId = null;
    try {
      providerId = providerServiceTester.createOrUpdateProvider(TEST_EVENT_PROVIDER_LABEL,
          TEST_EVENT_CODE).getId();
      registrationId = registrationServiceTester.createOrUpdateJournalRegistration(
          TEST_REGISTRATION_NAME, providerId, TEST_EVENT_CODE).getRegistrationId();

      assertNotNull(publishCloudEvent(null, providerId, TEST_EVENT_CODE));
      assertNotNull(publishCloudEvent(false, providerId, TEST_EVENT_CODE));
      assertNotNull(publishCloudEvent(true, providerId, TEST_EVENT_CODE));
      assertNotNull(publishRawEvent(providerId, TEST_EVENT_CODE));
      assertNotNull(publishRawEvent(Boolean.TRUE, providerId, TEST_EVENT_CODE, "someEventId"));

      final String finalProviderId = providerId;
      Exception ex = assertThrows(AIOException.class,() -> publishRawEvent(Boolean.TRUE, finalProviderId, TEST_EVENT_CODE, null));
      assertEquals("Cannot publish PHI data without an eventId", ex.getMessage());

    } catch (Exception e) {
      logger.error(e.getMessage(), e);
      fail(e.getMessage());
    } finally {
      if (!StringUtils.isEmpty(registrationId)) {
        registrationServiceTester.deleteRegistration(registrationId);
      }
      if (!StringUtils.isEmpty(providerId)) {
        providerServiceTester.deleteProvider(providerId);
      }
    }
  }

}
