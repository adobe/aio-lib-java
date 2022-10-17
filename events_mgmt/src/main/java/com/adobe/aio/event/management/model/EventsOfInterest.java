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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;

@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class EventsOfInterest {

  @JsonProperty("event_code")
  protected final String eventCode;

  @JsonProperty("provider_id")
  protected final String providerId;

  @JsonProperty("event_label")
  protected final String eventLabel;

  @JsonProperty("event_description")
  protected final String eventDescription;

  @JsonProperty("provider_label")
  protected final String providerLabel;

  @JsonProperty("provider_description")
  protected final String providerDescription;

  @JsonProperty("provider_docs_url")
  protected final String providerDocsUrl;

  @JsonProperty("event_delivery_format")
  protected final String eventDeliveryFormat;

  @JsonCreator
  public EventsOfInterest(@JsonProperty("event_code") String eventCode,
                  @JsonProperty("provider_id") String providerId,
                  @JsonProperty("event_label") String eventLabel,
                  @JsonProperty("event_description") String eventDescription,
                  @JsonProperty("provider_label") String providerLabel,
                  @JsonProperty("provider_description") String providerDescription,
                  @JsonProperty("provider_docs_url") String providerDocsUrl,
                  @JsonProperty("event_delivery_format") String eventDeliveryFormat) {
    this.eventCode = eventCode;
    this.providerId = providerId;
    this.eventLabel = eventLabel;
    this.eventDescription = eventDescription;
    this.providerLabel = providerLabel;
    this.providerDescription = providerDescription;
    this.providerDocsUrl = providerDocsUrl;
    this.eventDeliveryFormat = eventDeliveryFormat;
  }

  public String getEventCode() {
    return eventCode;
  }

  public String getProviderId() {
    return providerId;
  }

  public String getEventDeliveryFormat() {
    return eventDeliveryFormat;
  }

  public String getEventLabel() {
    return eventLabel;
  }

  public String getEventDescription() {
    return eventDescription;
  }

  public String getProviderLabel() {
    return providerLabel;
  }

  public String getProviderDescription() {
    return providerDescription;
  }

  public String getProviderDocsUrl() {
    return providerDocsUrl;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof EventsOfInterest)) {
      return false;
    }
    EventsOfInterest that = (EventsOfInterest) o;
    return Objects.equals(eventCode, that.eventCode) &&
                    Objects.equals(providerId, that.providerId) &&
                    Objects.equals(eventLabel, that.eventLabel) &&
                    Objects.equals(eventDescription, that.eventDescription) &&
                    Objects.equals(providerLabel, that.providerLabel) &&
                    Objects.equals(providerDescription, that.providerDescription) &&
                    Objects.equals(providerDocsUrl, that.providerDocsUrl) &&
                    Objects.equals(eventDeliveryFormat, that.eventDeliveryFormat);
  }

  @Override
  public int hashCode() {
    return Objects.hash(eventCode, providerId, eventLabel, eventDescription, providerLabel, providerDescription, providerDocsUrl,
                    eventDeliveryFormat);
  }

  @Override
  public String toString() {
    return "EventsOfInterest{" +
                    "eventCode='" + eventCode + '\'' +
                    ", providerId='" + providerId + '\'' +
                    ", eventLabel='" + eventLabel + '\'' +
                    ", eventDescription='" + eventDescription + '\'' +
                    ", providerLabel='" + providerLabel + '\'' +
                    ", providerDescription='" + providerDescription + '\'' +
                    ", providerDocsUrl='" + providerDocsUrl + '\'' +
                    ", eventDeliveryFormat='" + eventDeliveryFormat + '\'' +
                    '}';
  }
}
