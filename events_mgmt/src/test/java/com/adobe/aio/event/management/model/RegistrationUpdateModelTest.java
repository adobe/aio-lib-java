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

public class RegistrationUpdateModelTest {

  public static final String NAME = "com.adobe.aio.event.management.test.registration";
  public static final String DESCRIPTION = NAME + " description";
  public static final String DELIVERY_TYPE = "journal";

  @Test
  public void invalidEmpty() {
    assertThrows(IllegalArgumentException.class, () -> RegistrationCreateModel.builder().build());
  }

  @Test
  public void invalidMissingName() {
    assertThrows(IllegalArgumentException.class, () ->
        RegistrationUpdateModel.builder()
            .description(DESCRIPTION)
            .build()
    );
  }

  @Test
  public void missingDeliveryType() {
    assertThrows(IllegalArgumentException.class, () ->
        RegistrationUpdateModel.builder()
            .name(NAME)
            .description(DESCRIPTION)
            .build()
    );
  }

  @Test
  public void invalidWebhookUrlMissing() {
    assertThrows(IllegalArgumentException.class, () ->
        RegistrationUpdateModel.builder()
            .name(NAME)
            .description(DESCRIPTION)
            .deliveryType("webhook").build()
    );
  }

  @Test
  public void valid() {
    RegistrationUpdateModel registrationInputModel = RegistrationUpdateModel.builder()
        .name(NAME)
        .description(DESCRIPTION)
        .deliveryType(DELIVERY_TYPE).build();
    assertEquals(NAME, registrationInputModel.getName());
    assertEquals(DESCRIPTION, registrationInputModel.getDescription());
  }
}
