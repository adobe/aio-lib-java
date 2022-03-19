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
package com.adobe.aio.event.publish;

import com.adobe.aio.event.publish.feign.FeignPublishService;
import com.adobe.aio.event.publish.model.CloudEvent;
import com.adobe.aio.workspace.Workspace;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;


public interface PublishService {

  CloudEvent publishCloudEvent(String providerId, String eventCode, String eventId, JsonNode data);
  CloudEvent publishCloudEvent(String providerId, String eventCode, String data)
      throws JsonProcessingException;
  CloudEvent publishCloudEvent(String providerId, String eventCode, String eventId,  String data)
      throws JsonProcessingException;
  void publishRawEvent(String providerId, String eventCode, String rawEvent);

  static Builder builder() {
    return new Builder();
  }

  class Builder {

    private Workspace workspace;
    private String url;

    public Builder workspace(Workspace workspace) {
      this.workspace = workspace;
      return this;
    }
    public Builder url(String url) {
      this.url = url;
      return this;
    }

    public PublishService build() {
      return new FeignPublishService(workspace, url);
    }
  }
}
