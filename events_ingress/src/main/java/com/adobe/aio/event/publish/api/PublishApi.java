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
package com.adobe.aio.event.publish.api;

import com.adobe.aio.event.publish.model.CloudEvent;
import com.fasterxml.jackson.databind.JsonNode;
import feign.Headers;
import feign.Param;
import feign.RequestLine;

@Headers("Accept: application/json")
public interface PublishApi {

  String DEFAULT_URL = "https://eventsingress.adobe.io";

  /**
   * publish a Cloud Event Payload
   *
   * @param body your CloudEvent Input Model
   * @param isPhiData true if the event contains PHI Data
   */
  @RequestLine("POST")
  @Headers({
          "Content-Type: application/cloudevents+json",
          "x-event-phidata: {isPhiData}"})
  void publishCloudEvent(
          @Param("isPhiData") Boolean isPhiData,
          CloudEvent body);

  /**
   * publish a Raw Event Json Payload
   * @param isPhiData true if the event contains PHI Data
   * @param eventCode the Adobe I/O EventMetadata eventCode associated with the Event
   * @param providerId  the Adobe I/O EventMetadata ProviderId associated with the Event
   * @param eventId the eventId you want to assign to this the Event (mandatory if isPhiData is true)
   * @param rawEvent the Raw Event Json Payload to publish
   */
  @RequestLine("POST")
  @Headers({
          "Content-Type: application/json",
          "x-event-phidata: {isPhiData}",
          "x-event-id: {eventId}",
          "x-adobe-event-provider-id: {providerId}",
          "x-adobe-event-code: {eventCode}"
  })
  void publishRawEvent(
          @Param("isPhiData") Boolean isPhiData,
          @Param("providerId") String providerId,
          @Param("eventCode") String eventCode,
          @Param("eventId") String eventId,
          JsonNode rawEvent);

}
