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

public class EventMetadataModelTest {

  public static final String TEST_EVENT_CODE = "com.adobe.aio.event.management.test.event";
  public static final String TEST_EVENT_DESC = TEST_EVENT_CODE + " description";

  public static EventMetadata.Builder getTestEventMetadataBuilder(){
    return EventMetadata.builder()
        .eventCode(TEST_EVENT_CODE)
        .description(TEST_EVENT_DESC);
  }

  @Test(expected = IllegalArgumentException.class)
  public void invalid(){
    EventMetadata.builder().build();
  }

  @Test
  public void valid(){
    EventMetadata eventMetadata = getTestEventMetadataBuilder().build();
    Assert.assertEquals(TEST_EVENT_CODE,eventMetadata.getEventCode());
    Assert.assertEquals(TEST_EVENT_CODE,eventMetadata.getLabel());
    Assert.assertEquals(TEST_EVENT_DESC,eventMetadata.getDescription());
  }
}
