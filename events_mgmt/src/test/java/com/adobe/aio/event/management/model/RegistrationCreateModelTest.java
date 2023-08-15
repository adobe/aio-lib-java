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
package com.adobe.aio.event.management.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class RegistrationCreateModelTest {

  @Test
  public void invalidEmpty() {
    assertThrows(IllegalArgumentException.class, () -> RegistrationCreateModel.builder().build());
  }

  @Test
  public void invalidMissingClientId() {
    assertThrows(IllegalArgumentException.class, () -> RegistrationCreateModel.builder()
        .name("some name")
        .description("some description")
        .build()
    );
  }

  @Test
  public void invalidMissingName() {
    assertThrows(IllegalArgumentException.class, () -> RegistrationCreateModel.builder()
        .description("some description")
        .clientId("some client Id").build()
    );
  }

  @Test
  public void missingDeliveryType() {
    String name = "com.adobe.aio.event.management.test.registration";
    String description = name + " description";
    String clientId = "some_clientId";
    assertThrows(IllegalArgumentException.class, () -> RegistrationCreateModel.builder()
        .name(name)
        .description(description)
        .clientId(clientId).build()
    );
  }

  @Test
  public void invalidWebhookUrlMissing() {
    String name = "com.adobe.aio.event.management.test.registration";
    String description = name + " description";
    String clientId = "some_clientId";
    assertThrows(IllegalArgumentException.class, () -> RegistrationCreateModel.builder()
        .name(name)
        .description(description)
        .clientId(clientId)
        .deliveryType("webhook").build()
    );
  }

  @Test
  public void valid() {
    String name = "com.adobe.aio.event.management.test.registration";
    String description = name + " description";
    String clientId = "some_clientId";
    RegistrationCreateModel registrationInputModel = RegistrationCreateModel.builder()
        .name(name)
        .description(description)
        .clientId(clientId)
        .deliveryType("journal").build();
    assertEquals(clientId, registrationInputModel.getClientId());
    assertEquals(name, registrationInputModel.getName());
    assertEquals(description, registrationInputModel.getDescription());
  }
}
