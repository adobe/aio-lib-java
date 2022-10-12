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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;

@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class EventsOfInterest {

  @JsonProperty("event_code")
  String eventCode;

  @JsonProperty("provider_id")
  String providerId;

  @JsonProperty("event_label")
  protected String eventLabel;

  @JsonProperty("event_description")
  protected String eventDescription;

  @JsonProperty("provider_label")
  protected String providerLabel;

  @JsonProperty("provider_description")
  protected String providerDescription;

  @JsonProperty("provider_docs_url")
  protected String providerDocsUrl;

  @JsonProperty("event_delivery_format")
  protected String eventDeliveryFormat;

  public String getEventCode() {
    return eventCode;
  }

  public void setEventCode(String eventCode) {
    this.eventCode = eventCode;
  }

  public String getProviderId() {
    return providerId;
  }

  public void setProviderId(String providerId) {
    this.providerId = providerId;
  }

  public String getEventDeliveryFormat() {
    return eventDeliveryFormat;
  }

  public void setEventDeliveryFormat(String eventDeliveryFormat) {
    this.eventDeliveryFormat = eventDeliveryFormat;
  }

  public String getEventLabel() {
    return eventLabel;
  }

  public void setEventLabel(String eventLabel) {
    this.eventLabel = eventLabel;
  }

  public String getEventDescription() {
    return eventDescription;
  }

  public void setEventDescription(String eventDescription) {
    this.eventDescription = eventDescription;
  }

  public String getProviderLabel() {
    return providerLabel;
  }

  public void setProviderLabel(String providerLabel) {
    this.providerLabel = providerLabel;
  }

  public String getProviderDescription() {
    return providerDescription;
  }

  public void setProviderDescription(String providerDescription) {
    this.providerDescription = providerDescription;
  }

  public String getProviderDocsUrl() {
    return providerDocsUrl;
  }

  public void setProviderDocsUrl(String providerDocsUrl) {
    this.providerDocsUrl = providerDocsUrl;
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
