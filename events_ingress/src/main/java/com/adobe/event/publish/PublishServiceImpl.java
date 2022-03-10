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
package com.adobe.event.publish;

import com.adobe.event.publish.api.PublishApi;
import com.adobe.event.publish.model.CloudEvent;
import com.adobe.util.FeignUtil;
import com.adobe.util.JacksonUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import feign.RequestInterceptor;
import org.apache.commons.lang3.StringUtils;

class PublishServiceImpl implements PublishService {

  private final PublishApi publishApi;

  PublishServiceImpl(final RequestInterceptor authInterceptor,
      final String url) {
    String apiUrl = StringUtils.isEmpty(url) ? PublishApi.DEFAULT_URL : url;
    if (authInterceptor == null) {
      throw new IllegalArgumentException(
          "PublishService is missing a authentication interceptor");
    }
    this.publishApi = FeignUtil.getDefaultBuilder()
        .requestInterceptor(authInterceptor)
        .target(PublishApi.class, apiUrl);
  }

  @Override
  public CloudEvent publishCloudEvent(String providerId, String eventCode,
      String data) throws JsonProcessingException {
    return publishCloudEvent(providerId, eventCode, null, JacksonUtil.getJsonNode(data));
  }

  @Override
  public CloudEvent publishCloudEvent(String providerId, String eventCode, String eventId,
      String data) throws JsonProcessingException {
    return publishCloudEvent(providerId, eventCode, eventId, JacksonUtil.getJsonNode(data));
  }

  @Override
  public CloudEvent publishCloudEvent(String providerId, String eventCode, String eventId,
      JsonNode data) {
    CloudEvent inputModel = CloudEvent.builder()
        .providerId(providerId).eventCode(eventCode).eventId(eventId)
        .data(data).build();
    publishApi.publishCloudEvent(inputModel);
    return inputModel;
  }

  @Override
  public void publishRawEvent(String providerId, String eventCode, String rawEvent) {
    publishApi.publishRawEvent(providerId,  eventCode, rawEvent);
  }
}