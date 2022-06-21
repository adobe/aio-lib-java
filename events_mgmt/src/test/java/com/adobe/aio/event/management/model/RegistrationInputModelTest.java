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

public class RegistrationInputModelTest {

  @Test(expected = IllegalArgumentException.class)
  public void invalidEmpty() {
    RegistrationInputModel.builder().build();
  }

  @Test(expected = IllegalArgumentException.class)
  public void invalidMissingClientId() {
    RegistrationInputModel registrationInputModel = RegistrationInputModel.builder()
        .name("some name")
        .description("some description").build();
  }

  @Test(expected = IllegalArgumentException.class)
  public void invalidMissingName() {
    RegistrationInputModel.builder()
        .description("some description")
        .clientId("some client Id").build();
  }

  @Test
  public void valid() {
    String name = "com.adobe.aio.event.management.test.registration";
    String description = name + " description";
    String clientId = "some_clientId";
    RegistrationInputModel registrationInputModel = RegistrationInputModel.builder()
        .name(name)
        .description(description)
        .clientId(clientId).build();
    Assert.assertEquals(clientId, registrationInputModel.getClientId());
    Assert.assertEquals(name, registrationInputModel.getName());
    Assert.assertEquals(description, registrationInputModel.getDescription());
  }
}
