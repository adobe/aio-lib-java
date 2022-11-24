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
import io.openapitools.jackson.dataformat.hal.HALLink;
import io.openapitools.jackson.dataformat.hal.annotation.Link;
import io.openapitools.jackson.dataformat.hal.annotation.Resource;
import java.util.Objects;
import java.util.Set;

@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Resource
public class Registration {

  @Link
  private HALLink self;

  @Link("rel:events")
  private HALLink journalUrl;

  @Link("rel:trace")
  private HALLink traceUrl;

  private final Long id;

  private final String name;

  private final String description;

  @JsonProperty("client_id")
  private final String clientId;

  @JsonProperty("registration_id")
  private final String registrationId;

  @JsonProperty("delivery_type")
  private final String deliveryType;

  @JsonProperty("webhook_status")
  private final String webhookStatus;

  @JsonProperty(value = "created_date")
  private final String createdDate;

  @JsonProperty(value = "updated_date")
  private final String updatedDate;

  @JsonProperty("consumer_id")
  private final String consumerId;

  @JsonProperty("project_id")
  private final String projectId;

  @JsonProperty("workspace_id")
  private final String workspaceId;

  @JsonProperty("webhook_url")
  private final String webhookUrl;

  @JsonProperty("runtime_action")
  private final String runtimeAction;

  @JsonProperty("enabled")
  private final boolean enabled;

  @JsonProperty("events_of_interest")
  private final Set<EventsOfInterest> eventsOfInterests;

  @JsonCreator
  public Registration(
                  @JsonProperty("id") Long id,
                  @JsonProperty("name") String name,
                  @JsonProperty("description") String description,
                  @JsonProperty("client_id") String clientId,
                  @JsonProperty("registration_id") String registrationId,
                  @JsonProperty("delivery_type") String deliveryType,
                  @JsonProperty("webhook_status") String webhookStatus,
                  @JsonProperty(value = "created_date") String createdDate,
                  @JsonProperty(value = "updated_date") String updatedDate,
                  @JsonProperty("consumer_id") String consumerId,
                  @JsonProperty("project_id") String projectId,
                  @JsonProperty("workspace_id") String workspaceId,
                  @JsonProperty("webhook_url") String webhookUrl,
                  @JsonProperty("runtime_action") String runtimeAction,
                  @JsonProperty("enabled") boolean enabled,
                  @JsonProperty("events_of_interest") Set<EventsOfInterest> eventsOfInterests) {
    this.id = id;
    this.name = name;
    this.description = description;
    this.clientId = clientId;
    this.registrationId = registrationId;
    this.deliveryType = deliveryType;
    this.webhookStatus = webhookStatus;
    this.createdDate = createdDate;
    this.updatedDate = updatedDate;
    this.consumerId = consumerId;
    this.projectId = projectId;
    this.workspaceId = workspaceId;
    this.webhookUrl = webhookUrl;
    this.runtimeAction = runtimeAction;
    this.enabled = enabled;
    this.eventsOfInterests = eventsOfInterests;
    this.self = self;
    this.traceUrl = traceUrl;
    this.journalUrl = journalUrl;
  }



  public HALLink getSelf() {
    return self;
  }

  public HALLink getJournalUrl() {
    return journalUrl;
  }

  public HALLink getTraceUrl() {
    return traceUrl;
  }

  public Long getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }

  public String getClientId() {
    return clientId;
  }

  public String getRegistrationId() {
    return registrationId;
  }

  public String getDeliveryType() {
    return deliveryType;
  }

  public String getWebhookStatus() {
    return webhookStatus;
  }

  public String getCreatedDate() {
    return createdDate;
  }

  public String getUpdatedDate() {
    return updatedDate;
  }

  public String getConsumerId() {
    return consumerId;
  }

  public String getProjectId() {
    return projectId;
  }

  public String getWorkspaceId() {
    return workspaceId;
  }

  public String getWebhookUrl() {
    return webhookUrl;
  }

  public String getRuntimeAction() {
    return runtimeAction;
  }

  public boolean isEnabled() {
    return enabled;
  }

  public Set<EventsOfInterest> getEventsOfInterests() {
    return eventsOfInterests;
  }

  @Override public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Registration)) {
      return false;
    }
    Registration that = (Registration) o;
    return enabled == that.enabled &&
      Objects.equals(self, that.self) &&
      Objects.equals(journalUrl, that.journalUrl) &&
      Objects.equals(traceUrl, that.traceUrl) &&
      Objects.equals(id, that.id) &&
      Objects.equals(name, that.name) &&
      Objects.equals(description, that.description) &&
      Objects.equals(clientId, that.clientId) &&
      Objects.equals(registrationId, that.registrationId) &&
      Objects.equals(deliveryType, that.deliveryType) &&
      Objects.equals(webhookStatus, that.webhookStatus) &&
      Objects.equals(createdDate, that.createdDate) &&
      Objects.equals(updatedDate, that.updatedDate) &&
      Objects.equals(consumerId, that.consumerId) &&
      Objects.equals(projectId, that.projectId) &&
      Objects.equals(workspaceId, that.workspaceId) &&
      Objects.equals(webhookUrl, that.webhookUrl) &&
      Objects.equals(runtimeAction, that.runtimeAction) &&
      Objects.equals(eventsOfInterests, that.eventsOfInterests);
  }

  @Override public int hashCode() {
    return Objects.hash(self, journalUrl, traceUrl, id, name, description, clientId, registrationId, deliveryType, webhookStatus,
      createdDate, updatedDate, consumerId, projectId, workspaceId, webhookUrl, runtimeAction, enabled, eventsOfInterests);
  }

  @Override public String toString() {
    return "Registration{" +
      "self=" + self +
      ", eventsUrl=" + journalUrl +
      ", traceUrl=" + traceUrl +
      ", id=" + id +
      ", name='" + name + '\'' +
      ", description='" + description + '\'' +
      ", clientId='" + clientId + '\'' +
      ", registrationId='" + registrationId + '\'' +
      ", deliveryType='" + deliveryType + '\'' +
      ", webhookStatus='" + webhookStatus + '\'' +
      ", createdDate='" + createdDate + '\'' +
      ", updatedDate='" + updatedDate + '\'' +
      ", consumerId='" + consumerId + '\'' +
      ", projectId='" + projectId + '\'' +
      ", workspaceId='" + workspaceId + '\'' +
      ", webhookUrl='" + webhookUrl + '\'' +
      ", runtimeAction='" + runtimeAction + '\'' +
      ", enabled=" + enabled +
      ", eventsOfInterests=" + eventsOfInterests +
      '}';
  }
}
