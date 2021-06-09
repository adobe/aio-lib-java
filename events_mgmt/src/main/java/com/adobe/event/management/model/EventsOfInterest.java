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
package com.adobe.event.management.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;

public class EventsOfInterest {

  @JsonProperty("event_code")
  String eventCode;

  @JsonProperty("provider_id")
  String providerId;

  private EventsOfInterest() {
  }

  private EventsOfInterest(String providerId, String eventCode) {
    this.providerId = providerId;
    this.eventCode = eventCode;
  }

  public String getProviderId() {
    return providerId;
  }

  public String getEventCode() {
    return eventCode;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    EventsOfInterest that = (EventsOfInterest) o;
    return Objects.equals(eventCode, that.eventCode) &&
        Objects.equals(providerId, that.providerId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(eventCode, providerId);
  }

  @Override
  public String toString() {
    return "EventsOfInterest{" +
        "eventCode='" + eventCode + '\'' +
        ", providerId='" + providerId + '\'' +
        '}';
  }

  @JsonIgnore
  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {

    private String providerId;
    private String eventCode;

    public Builder setProviderId(String providerId) {
      this.providerId = providerId;
      return this;
    }

    public Builder setEventCode(String eventCode) {
      this.eventCode = eventCode;
      return this;
    }

    public EventsOfInterest build() {
      return new EventsOfInterest(providerId, eventCode);
    }
  }
}
