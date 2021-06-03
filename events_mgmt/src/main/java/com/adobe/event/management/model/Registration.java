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
import java.util.Objects;

public class Registration extends RegistrationInputModel {

  public enum WebHookStatus {
    ACCEPTED,
    DELETED,
    VERIFICATION_FAILED,
    HOOK_UNREACHABLE,
    UNSTABLE,
    VERIFIED;
  }

  public enum WebHookType {
    USER, APP
  }

  public enum IntegrationStatus {
    ENABLED, DISABLED
  }

  @JsonProperty("registration_id")
  private String registrationId;

  @JsonProperty("status")
  private WebHookStatus status;

  @JsonProperty("type")
  private WebHookType type;

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


  public String getRegistrationId() {
    return this.registrationId;
  }

  public void setRegistrationId(String registrationId) {
    this.registrationId = registrationId;
  }

  public WebHookStatus getStatus() {
    return this.status;
  }

  public void setStatus(WebHookStatus status) {
    this.status = status;
  }

  public WebHookType getType() {
    return type;
  }

  public void setType(WebHookType type) {
    this.type = type;
  }

  public IntegrationStatus getIntegrationStatus() {
    return integrationStatus;
  }

  public void setIntegrationStatus(IntegrationStatus integrationStatus) {
    this.integrationStatus = integrationStatus;
  }

  public String getJournalUrl() {
    return journalUrl;
  }

  public void setJournalUrl(String journalUrl) {
    this.journalUrl = journalUrl;
  }

  public String getTraceUrl() {
    return traceUrl;
  }

  public void setTraceUrl(String traceUrl) {
    this.traceUrl = traceUrl;
  }

  public String getCreatedDate() {
    return createdDate;
  }

  public void setCreatedDate(String createdDate) {
    this.createdDate = createdDate;
  }

  public String getUpdatedDate() {
    return updatedDate;
  }

  public void setUpdatedDate(String updatedDate) {
    this.updatedDate = updatedDate;
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
