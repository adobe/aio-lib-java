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


import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class RegistrationInputModel {

  public enum DeliveryType {
    WEBHOOK, JOURNAL, WEBHOOK_BATCH
  }

  @JsonProperty("client_id")
  protected String clientId;

  protected String name;

  protected String description;

  @JsonProperty("delivery_type")
  protected DeliveryType deliveryType;

  @JsonProperty("events_of_interest")
  protected Set<EventsOfInterest> eventsOfInterests = new HashSet<>();

  @JsonProperty("webhook_url")
  protected String webhookUrl;

  public String getClientId() {
    return clientId;
  }

  public void setClientId(String clientId) {
    this.clientId = clientId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public DeliveryType getDeliveryType() {
    return deliveryType;
  }

  public void setDeliveryType(
      DeliveryType deliveryType) {
    this.deliveryType = deliveryType;
  }

  public Set<EventsOfInterest> getEventsOfInterests() {
    return eventsOfInterests;
  }

  public void setEventsOfInterests(
      Set<EventsOfInterest> eventsOfInterests) {
    this.eventsOfInterests = eventsOfInterests;
  }

  public String getWebhookUrl() {
    return webhookUrl;
  }

  public void setWebhookUrl(String webhookUrl) {
    this.webhookUrl = webhookUrl;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    RegistrationInputModel that = (RegistrationInputModel) o;
    return Objects.equals(clientId, that.clientId) &&
        Objects.equals(name, that.name) &&
        Objects.equals(description, that.description) &&
        deliveryType == that.deliveryType &&
        Objects.equals(eventsOfInterests, that.eventsOfInterests) &&
        Objects.equals(webhookUrl, that.webhookUrl);
  }

  @Override
  public int hashCode() {
    return Objects.hash(clientId, name, description, deliveryType, eventsOfInterests, webhookUrl);
  }

  @Override
  public String toString() {
    return "RegistrationInputModel{" +
        "clientId='" + clientId + '\'' +
        ", name='" + name + '\'' +
        ", description='" + description + '\'' +
        ", deliveryType=" + deliveryType +
        ", eventsOfInterests=" + eventsOfInterests +
        ", webhookUrl='" + webhookUrl + '\'' +
        '}';
  }
}
