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
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;
import java.util.Set;

/**
 * Model representing the output of subscriber filter validation.
 * Contains the results of testing the filter against events of interest and custom sample events.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class SubscriberFilterValidationOutputModel {

  @JsonProperty("accepted_event_metadata_ids")
  private final Set<EventMetadataIdOutputModel> acceptedEventMetadataIds;

  @JsonProperty("rejected_event_metadata_ids")
  private final Set<EventMetadataIdOutputModel> rejectedEventMetadataIds;

  @JsonProperty("unknown_event_metadata_ids")
  private final Set<EventMetadataIdOutputModel> unknownEventMetadataIds;

  @JsonProperty("accepted_custom_sample_events")
  private final Set<String> acceptedCustomSampleEvents;

  @JsonProperty("rejected_custom_sample_events")
  private final Set<String> rejectedCustomSampleEvents;

  @JsonProperty("invalid_custom_sample_events")
  private final Set<String> invalidCustomSampleEvents;

  public SubscriberFilterValidationOutputModel(
      Set<EventMetadataIdOutputModel> acceptedEventMetadataIds,
      Set<EventMetadataIdOutputModel> rejectedEventMetadataIds,
      Set<EventMetadataIdOutputModel> unknownEventMetadataIds,
      Set<String> acceptedCustomSampleEvents,
      Set<String> rejectedCustomSampleEvents,
      Set<String> invalidCustomSampleEvents) {
    this.acceptedEventMetadataIds = acceptedEventMetadataIds;
    this.rejectedEventMetadataIds = rejectedEventMetadataIds;
    this.unknownEventMetadataIds = unknownEventMetadataIds;
    this.acceptedCustomSampleEvents = acceptedCustomSampleEvents;
    this.rejectedCustomSampleEvents = rejectedCustomSampleEvents;
    this.invalidCustomSampleEvents = invalidCustomSampleEvents;
  }

  public Set<EventMetadataIdOutputModel> getAcceptedEventMetadataIds() {
    return acceptedEventMetadataIds;
  }

  public Set<EventMetadataIdOutputModel> getRejectedEventMetadataIds() {
    return rejectedEventMetadataIds;
  }

  public Set<EventMetadataIdOutputModel> getUnknownEventMetadataIds() {
    return unknownEventMetadataIds;
  }

  public Set<String> getAcceptedCustomSampleEvents() {
    return acceptedCustomSampleEvents;
  }

  public Set<String> getRejectedCustomSampleEvents() {
    return rejectedCustomSampleEvents;
  }

  public Set<String> getInvalidCustomSampleEvents() {
    return invalidCustomSampleEvents;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    SubscriberFilterValidationOutputModel that = (SubscriberFilterValidationOutputModel) o;
    return Objects.equals(acceptedEventMetadataIds, that.acceptedEventMetadataIds) &&
        Objects.equals(rejectedEventMetadataIds, that.rejectedEventMetadataIds) &&
        Objects.equals(unknownEventMetadataIds, that.unknownEventMetadataIds) &&
        Objects.equals(acceptedCustomSampleEvents, that.acceptedCustomSampleEvents) &&
        Objects.equals(rejectedCustomSampleEvents, that.rejectedCustomSampleEvents) &&
        Objects.equals(invalidCustomSampleEvents, that.invalidCustomSampleEvents);
  }

  @Override
  public int hashCode() {
    return Objects.hash(acceptedEventMetadataIds, rejectedEventMetadataIds, unknownEventMetadataIds,
        acceptedCustomSampleEvents, rejectedCustomSampleEvents, invalidCustomSampleEvents);
  }

  @Override
  public String toString() {
    return "SubscriberFilterValidationOutputModel{" +
        "acceptedEventMetadataIds=" + acceptedEventMetadataIds +
        ", rejectedEventMetadataIds=" + rejectedEventMetadataIds +
        ", unknownEventMetadataIds=" + unknownEventMetadataIds +
        ", acceptedCustomSampleEvents=" + acceptedCustomSampleEvents +
        ", rejectedCustomSampleEvents=" + rejectedCustomSampleEvents +
        ", invalidCustomSampleEvents=" + invalidCustomSampleEvents +
        '}';
  }
} 