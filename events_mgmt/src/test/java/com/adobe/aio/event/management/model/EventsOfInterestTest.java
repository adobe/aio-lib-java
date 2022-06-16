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

import static com.adobe.aio.event.management.model.EventMetadataModelTest.TEST_EVENT_CODE;

import org.junit.Assert;
import org.junit.Test;

public class EventsOfInterestTest {

  public static EventsOfInterest.Builder getTestEventsOfInterestBuilder(String providerId){
    return EventsOfInterest.builder()
        .eventCode(TEST_EVENT_CODE)
        .providerId(providerId);
  }

  @Test(expected = IllegalArgumentException.class)
  public void invalidEmpty(){
    EventsOfInterest.builder().build();
  }

  @Test(expected = IllegalArgumentException.class)
  public void invalidMissingProviderId(){
    EventsOfInterest.builder().eventCode(TEST_EVENT_CODE).build();
  }

  @Test(expected = IllegalArgumentException.class)
  public void invalidMissingEventCode(){
    EventsOfInterest.builder().providerId("someId").build();
  }

  @Test
  public void valid(){
    String providerId = "some_provider_id";
    EventsOfInterest eventsOfInterest = getTestEventsOfInterestBuilder(providerId).build();
    Assert.assertEquals(TEST_EVENT_CODE,eventsOfInterest.getEventCode());
    Assert.assertEquals(providerId,eventsOfInterest.getProviderId());
  }

}
