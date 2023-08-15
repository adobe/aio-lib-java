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

public class EventMetadataModelTest {

  @Test
  public void invalid() {
    assertThrows(IllegalArgumentException.class, () -> EventMetadata.builder().build());
  }

  @Test
  public void valid() {
    String eventCode = "com.adobe.aio.event.management.test.event";
    String eventDescription = eventCode + " description";
    EventMetadata eventMetadata = EventMetadata.builder()
        .eventCode(eventCode)
        .description(eventDescription).build();
    assertEquals(eventCode, eventMetadata.getEventCode());
    assertEquals(eventCode, eventMetadata.getLabel());
    assertEquals(eventDescription, eventMetadata.getDescription());
  }
}
