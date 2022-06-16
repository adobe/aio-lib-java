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

  public static final String TEST_REGISTRATION_NAME = "com.adobe.aio.event.management.test.registration";
  public static final String TEST_REGISTRATION_DESC = TEST_REGISTRATION_NAME + " description";

  public static RegistrationInputModel.Builder getRegistrationInputModelBuilder() {
    return RegistrationInputModel.builder()
        .name(TEST_REGISTRATION_NAME)
        .description(TEST_REGISTRATION_DESC);
  }

  @Test(expected = IllegalArgumentException.class)
  public void invalidEmpty() {
    RegistrationInputModel.builder().build();
  }

  @Test(expected = IllegalArgumentException.class)
  public void invalidMissingClientId() {
    getRegistrationInputModelBuilder().build();
  }

  @Test(expected = IllegalArgumentException.class)
  public void invalidMissingName() {
    RegistrationInputModel.builder()
        .description(TEST_REGISTRATION_DESC)
        .clientId("some client Id").build();
  }

  @Test
  public void valid() {
    String clientId = "some_clientId";
    RegistrationInputModel registrationInputModel = getRegistrationInputModelBuilder().clientId(
        clientId).build();
    Assert.assertEquals(clientId, registrationInputModel.getClientId());
    Assert.assertEquals(TEST_REGISTRATION_NAME, registrationInputModel.getName());
    Assert.assertEquals(TEST_REGISTRATION_DESC, registrationInputModel.getDescription());
  }
}
