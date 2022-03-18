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

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Registration {

  public enum Type {
    USER, APP
  }

  public enum Status {
    ACCEPTED,
    DELETED,
    VERIFICATION_FAILED,
    HOOK_UNREACHABLE,
    UNSTABLE,
    VERIFIED
  }

  public enum IntegrationStatus {
    ENABLED, DISABLED
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

  @JsonProperty("registration_id")
  private String registrationId;

  @JsonProperty("status")
  private Status status;

  @JsonProperty("type")
  private Type type;

  @JsonProperty("integration_status")
  private IntegrationStatus integrationStatus;

  @JsonProperty(value = "events_url")
  private String journalUrl;

  @JsonProperty(value = "trace_url")
  private String traceUrl;

  @JsonProperty(value = "created_date")
  private String createdDate;

  @JsonProperty(value = "updated_date")
  private String updatedDate;

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

  public String getRegistrationId() {
    return this.registrationId;
  }

  public Status getStatus() {
    return this.status;
  }

  public Type getType() {
    return type;
  }

  public IntegrationStatus getIntegrationStatus() {
    return integrationStatus;
  }

  public String getJournalUrl() {
    return journalUrl;
  }

  public String getTraceUrl() {
    return traceUrl;
  }

  public String getCreatedDate() {
    return createdDate;
  }

  public String getUpdatedDate() {
    return updatedDate;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Registration that = (Registration) o;
    return Objects.equals(registrationId, that.registrationId) &&
        Objects.equals(clientId, that.clientId) &&
        Objects.equals(name, that.name) &&
        Objects.equals(description, that.description) &&
        Objects.equals(eventsOfInterests, that.eventsOfInterests) &&
        Objects.equals(webhookUrl, that.webhookUrl) &&
        status == that.status &&
        type == that.type &&
        integrationStatus == that.integrationStatus &&
        deliveryType == that.deliveryType &&
        Objects.equals(journalUrl, that.journalUrl) &&
        Objects.equals(traceUrl, that.traceUrl) &&
        Objects.equals(createdDate, that.createdDate) &&
        Objects.equals(updatedDate, that.updatedDate);
  }

  @Override
  public int hashCode() {
    return Objects
        .hash(registrationId, clientId, name, description, eventsOfInterests,
            webhookUrl, status, type, integrationStatus, deliveryType, journalUrl, traceUrl,
            createdDate, updatedDate);
  }

  @Override
  public String toString() {
    return "Registration{" +
        " name='" + name + '\'' +
        ", registrationId='" + registrationId + '\'' +
        ", description='" + description + '\'' +
        ", eventsOfInterests=" + eventsOfInterests +
        ", deliveryType=" + deliveryType +
        ", clientId='" + clientId + '\'' +
        ", status=" + status +
        ", type=" + type +
        ", integrationStatus=" + integrationStatus +
        ", webhookUrl='" + webhookUrl + '\'' +
        ", journalUrl='" + journalUrl + '\'' +
        ", traceUrl='" + traceUrl + '\'' +
        ", createdDate='" + createdDate + '\'' +
        ", updatedDate='" + updatedDate + '\'' +
        '}';
  }
}
