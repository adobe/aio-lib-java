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
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;

@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class RegistrationUpdateModel {

  protected String name;

  protected String description;

  @JsonProperty("webhook_url")
  protected String webhookUrl;

  @JsonProperty("events_of_interest")
  protected Set<EventsOfInterestInputModel> eventsOfInterestInputModels;

  @JsonProperty("delivery_type")
  protected String deliveryType;

  @JsonProperty("enabled")
  protected Boolean enabled;

  public RegistrationUpdateModel(final String name, final String description, final String webhookUrl,
                  final Set<EventsOfInterestInputModel> eventsOfInterestInputModels, final String deliveryType,
                  final Boolean enabled) {

    if (StringUtils.isBlank(name)){
      throw new IllegalArgumentException("Registration is missing a name");
    }

    if (StringUtils.isBlank(deliveryType)){
      throw new IllegalArgumentException("Registration is missing a delivery_type");
    }

    this.name = name;
    this.description = description;
    this.webhookUrl = webhookUrl;
    this.eventsOfInterestInputModels = eventsOfInterestInputModels;
    if (DeliveryType.fromFriendlyName(deliveryType).isWebhookDelivery() && StringUtils.isEmpty(webhookUrl)) {
      throw new IllegalArgumentException(
                      "Registration is a webhook registration, but missing a webhook url");
    } else {
      this.deliveryType = deliveryType;
    }
    this.enabled = enabled == null || enabled;
  }

  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }

  public String getWebhookUrl() {
    return webhookUrl;
  }

  public Set<EventsOfInterestInputModel> getEventsOfInterests() {
    return eventsOfInterestInputModels;
  }

  public Boolean isEnabled() {
    return enabled;
  }

  public String getDeliveryType() {
    return deliveryType;
  }

  @Override public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof RegistrationUpdateModel)) {
      return false;
    }
    RegistrationUpdateModel that = (RegistrationUpdateModel) o;
    return Objects.equals(name, that.name) &&
                    Objects.equals(description, that.description) &&
                    Objects.equals(webhookUrl, that.webhookUrl) &&
                    Objects.equals(eventsOfInterestInputModels, that.eventsOfInterestInputModels) &&
                    Objects.equals(deliveryType, that.deliveryType) &&
                    Objects.equals(enabled, that.enabled);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, description, webhookUrl, eventsOfInterestInputModels, deliveryType, enabled);
  }

  @Override
  public String toString() {
    return "RegistrationUpdateModel{" +
                    "name='" + name + '\'' +
                    ", description='" + description + '\'' +
                    ", webhookUrl='" + webhookUrl + '\'' +
                    ", eventsOfInterestInputModels=" + eventsOfInterestInputModels +
                    ", deliveryType=" + deliveryType +
                    ", enabled='" + enabled + '\'' +
                    '}';
  }

  @JsonIgnore
  public static Builder builder() {
    return new Builder();
  }

  public static class Builder<T extends Builder> {

    protected String name;
    protected String description;
    protected String deliveryType;
    protected Set<EventsOfInterestInputModel> eventsOfInterestInputModels = new HashSet<>();
    protected String webhookUrl;
    protected Boolean enabled = Boolean.TRUE;

    public Builder() {
    }

    public T name(String name) {
      this.name = name;
      return (T) this;
    }

    public T description(String description) {
      this.description = description;
      return (T) this;
    }

    public T deliveryType(String deliveryType) {
      this.deliveryType = deliveryType;
      return (T) this;
    }

    public T addEventsOfInterests(
                    EventsOfInterestInputModel eventsOfInterestInputModel) {
      this.eventsOfInterestInputModels.add(eventsOfInterestInputModel);
      return (T) this;
    }

    public T addEventsOfInterests(
                    Set<EventsOfInterestInputModel> eventsOfInterestInputModel) {
      this.eventsOfInterestInputModels.addAll(eventsOfInterestInputModel);
      return (T) this;
    }

    public T webhookUrl(String webhookUrl) {
      this.webhookUrl = webhookUrl;
      return (T) this;
    }

    public T enabled(Boolean enabled) {
      this.enabled = enabled;
      return (T) this;
    }

    public RegistrationUpdateModel build() {
      return new RegistrationUpdateModel(name, description, webhookUrl,
                      eventsOfInterestInputModels, deliveryType,
                      enabled);
    }
  }
}

