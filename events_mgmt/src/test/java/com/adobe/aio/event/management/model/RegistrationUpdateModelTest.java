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

import org.junit.Assert;
import org.junit.Test;

public class RegistrationUpdateModelTest {

  public static final String NAME = "com.adobe.aio.event.management.test.registration";
  public static final String DESCRIPTION = NAME + " description";
  public static final String DELIVERY_TYPE = "journal";

  @Test(expected = IllegalArgumentException.class)
  public void invalidEmpty() {
    RegistrationCreateModel.builder().build();
  }


  @Test(expected = IllegalArgumentException.class)
  public void invalidMissingName() {
    RegistrationUpdateModel.builder()
        .description(DESCRIPTION)
        .build();
  }

  @Test(expected = IllegalArgumentException.class)
  public void missingDeliveryType() {
    RegistrationUpdateModel.builder()
        .name(NAME)
        .description(DESCRIPTION)
        .build();
  }

  @Test(expected = IllegalArgumentException.class)
  public void invalidWebhookUrlMissing() {
    RegistrationUpdateModel.builder()
                    .name(NAME)
                    .description(DESCRIPTION)
                    .deliveryType("webhook").build();
  }

  @Test
  public void valid() {
    RegistrationUpdateModel registrationInputModel = RegistrationUpdateModel.builder()
                    .name(NAME)
                    .description(DESCRIPTION)
                    .deliveryType(DELIVERY_TYPE).build();
    Assert.assertEquals(NAME, registrationInputModel.getName());
    Assert.assertEquals(DESCRIPTION, registrationInputModel.getDescription());
  }
}
