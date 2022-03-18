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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;
import org.apache.commons.lang3.StringUtils;

public class EventMetadata {

  @JsonProperty("event_code")
  private String eventCode;

  @JsonProperty("label")
  private String label;

  @JsonProperty("description")
  private String description;

  @JsonProperty("sample_event_template")
  private String sampleEventTemplate;

  //used by Jackson deserializer
  private EventMetadata() {
  }

  private EventMetadata(String eventCode, String label, String description,
      String sampleEventTemplate) {
    if (StringUtils.isEmpty(eventCode)) {
      throw new IllegalArgumentException(
          "EventMetadata is missing an Event Code");
    }
    this.eventCode = eventCode;
    this.label = (StringUtils.isEmpty(label)) ? eventCode : label;
    this.description = description;
    this.sampleEventTemplate = sampleEventTemplate;
  }

  public String getDescription() {
    return description;
  }

  public String getLabel() {
    return label;
  }

  public String getEventCode() {
    return eventCode;
  }

  public String getSampleEventTemplate() {
    return sampleEventTemplate;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    EventMetadata that = (EventMetadata) o;
    return Objects.equals(description, that.description) &&
        Objects.equals(label, that.label) &&
        Objects.equals(eventCode, that.eventCode) &&
        Objects.equals(sampleEventTemplate, that.sampleEventTemplate);
  }

  @Override
  public int hashCode() {
    return Objects.hash(description, label, eventCode, sampleEventTemplate);
  }

  @Override
  public String toString() {
    return "EventMetadata{" +
        "eventCode='" + eventCode + '\'' +
        ", label='" + label + '\'' +
        ", description='" + description + '\'' +
        '}';
  }

  @JsonIgnore
  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {

    private String eventCode;
    private String label;
    private String description;
    private String sampleEventTemplate;

    public Builder eventCode(String eventCode) {
      this.eventCode = eventCode;
      return this;
    }

    public Builder label(String label) {
      this.label = label;
      return this;
    }

    public Builder description(String description) {
      this.description = description;
      return this;
    }

    public Builder sampleEventTemplate(String sampleEventTemplate) {
      this.sampleEventTemplate = sampleEventTemplate;
      return this;
    }

    public EventMetadata build() {
      return new EventMetadata(eventCode, label, description, sampleEventTemplate);
    }
  }
}
