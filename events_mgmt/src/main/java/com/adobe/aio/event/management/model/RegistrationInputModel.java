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
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;

public class RegistrationInputModel {

  @JsonProperty("client_id")
  private final String clientId;

  private final String name;

  private final String description;

  @JsonProperty("delivery_type")
  private final DeliveryType deliveryType;

  @JsonProperty("events_of_interest")
  private final Set<EventsOfInterest> eventsOfInterests;

  @JsonProperty("webhook_url")
  private final String webhookUrl;

  private RegistrationInputModel(final String clientId, final String name, final String description,
      final DeliveryType deliveryType, final Set<EventsOfInterest> eventsOfInterests,
      final String webhookUrl) {
    if (StringUtils.isBlank(name)){
      throw new IllegalArgumentException(
          "RegistrationInputModel is missing a name");
    }
    if (StringUtils.isBlank(clientId)){
      throw new IllegalArgumentException(
          "RegistrationInputModel is missing a clientId");
    }
    if (deliveryType == null && StringUtils.isEmpty(webhookUrl)) {
      this.deliveryType = DeliveryType.JOURNAL;
    } else if (deliveryType == null && !StringUtils.isEmpty(webhookUrl)) {
      this.deliveryType = DeliveryType.WEBHOOK;
    } else if (deliveryType != DeliveryType.JOURNAL && StringUtils.isEmpty(webhookUrl)) {
      throw new IllegalArgumentException(
          "RegistrationInputModel is a webhook registration, but missing a webhook url");
    } else {
      this.deliveryType = deliveryType;
    }
    this.webhookUrl = webhookUrl;//Todo validate url, must be https
    this.clientId = clientId;
    this.name = name;
    this.description = description;
    this.eventsOfInterests = eventsOfInterests;
  }

  public String getClientId() {
    return clientId;
  }

  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }

  public DeliveryType getDeliveryType() {
    return deliveryType;
  }

  public Set<EventsOfInterest> getEventsOfInterests() {
    return eventsOfInterests;
  }

  public String getWebhookUrl() {
    return webhookUrl;
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

  @JsonIgnore
  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {

    private String clientId;
    private String name;
    private String description;
    private DeliveryType deliveryType;
    private Set<EventsOfInterest> eventsOfInterests = new HashSet<>();
    private String webhookUrl;

    public Builder() {
    }

    public Builder clientId(String clientId) {
      this.clientId = clientId;
      return this;
    }

    public Builder name(String name) {
      this.name = name;
      return this;
    }

    public Builder description(String description) {
      this.description = description;
      return this;
    }

    public Builder deliveryType(DeliveryType deliveryType) {
      this.deliveryType = deliveryType;
      return this;
    }

    public Builder addEventsOfInterests(
        EventsOfInterest eventsOfInterest) {
      this.eventsOfInterests.add(eventsOfInterest);
      return this;
    }

    public Builder addEventsOfInterests(
        Set<EventsOfInterest> eventsOfInterest) {
      this.eventsOfInterests.addAll(eventsOfInterest);
      return this;
    }
    
    public Builder webhookUrl(String webhookUrl) {
      this.webhookUrl = webhookUrl;
      return this;
    }

    public RegistrationInputModel build() {
      return new RegistrationInputModel(clientId, name, description, deliveryType,
          eventsOfInterests,
          webhookUrl);
    }
  }
}
