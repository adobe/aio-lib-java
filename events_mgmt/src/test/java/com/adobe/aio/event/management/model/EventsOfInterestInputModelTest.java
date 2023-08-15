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

public class EventsOfInterestInputModelTest {

  @Test
  public void invalidEmpty() {
    assertThrows(IllegalArgumentException.class, () -> EventsOfInterestInputModel.builder().build());
  }

  @Test
  public void invalidMissingProviderId() {
    assertThrows(IllegalArgumentException.class, () -> EventsOfInterestInputModel.builder().eventCode("some.event.code").build());
  }

  @Test
  public void invalidMissingEventCode() {
    assertThrows(IllegalArgumentException.class, () -> EventsOfInterestInputModel.builder().providerId("someId").build());
  }

  @Test
  public void valid() {
    String eventCode = "com.adobe.aio.event.management.test.event";
    String providerId = "some_provider_id";
    EventsOfInterestInputModel eventsOfInterestInputModel = EventsOfInterestInputModel.builder()
        .eventCode(eventCode)
        .providerId(providerId).build();
    assertEquals(eventCode, eventsOfInterestInputModel.getEventCode());
    assertEquals(providerId, eventsOfInterestInputModel.getProviderId());
  }

}
