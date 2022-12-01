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
package com.adobe.aio.event.publish.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.Objects;
import java.util.UUID;
import org.apache.commons.lang3.StringUtils;

public class CloudEvent {

  public static final String SOURCE_URN_PREFIX = "urn:uuid:";
  public static final String SPEC_VERSION = "1.0";

  @JsonProperty("datacontenttype")
  private final String dataContentType = "application/json";

  @JsonProperty("specversion")
  private final String specVersion = SPEC_VERSION;

  private final String source;

  private final String type;

  private final String id;

  private final JsonNode data;

  private CloudEvent(final String providerId, final String eventCode,
      final String eventId, final JsonNode data) {
    if (StringUtils.isEmpty(providerId)) {
      throw new IllegalArgumentException(
          "CloudEventInputModel is missing an Adobe I/O Events providerId/source");
    }
    if (StringUtils.isEmpty(eventCode)) {
      throw new IllegalArgumentException(
          "CloudEventInputModel is missing an Adobe I/O Events eventCode/type");
    }
    if (StringUtils.isEmpty(eventId)) {
      this.id = UUID.randomUUID().toString();
    } else {
      this.id = eventId;
    }
    this.source = SOURCE_URN_PREFIX + providerId;
    this.type = eventCode;
    this.data = data;
  }

  public String getDataContentType() {
    return dataContentType;
  }

  public String getSpecVersion() {
    return specVersion;
  }

  public String getSource() {
    return source;
  }

  public String getType() {
    return type;
  }

  public String getId() {
    return id;
  }

  public JsonNode getData() {
    return data;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CloudEvent that = (CloudEvent) o;
    return Objects.equals(dataContentType, that.dataContentType) &&
        Objects.equals(specVersion, that.specVersion) &&
        Objects.equals(source, that.source) &&
        Objects.equals(type, that.type) &&
        Objects.equals(id, that.id) &&
        Objects.equals(data, that.data);
  }

  @Override
  public int hashCode() {
    return Objects.hash(dataContentType, specVersion, source, type, id, data);
  }

  @Override
  public String toString() {
    return "CloudEventInputModel{" +
        "source='" + source + '\'' +
        ", type='" + type + '\'' +
        ", id='" + id + '\'' +
        ", data=" + data +
        '}';
  }

  @JsonIgnore
  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {

    private String providerId;
    private String eventCode;
    private String eventId;
    private JsonNode data;

    public Builder() {
    }

    public Builder providerId(String providerId) {
      this.providerId = providerId;
      return this;
    }

    public Builder eventCode(String eventCode) {
      this.eventCode = eventCode;
      return this;
    }

    public Builder eventId(String eventId) {
      this.eventId = eventId;
      return this;
    }

    public Builder data(JsonNode data) {
      this.data = data;
      return this;
    }

    public CloudEvent build() {
      return new CloudEvent(providerId, eventCode, eventId, data);
    }
  }
}
