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
 * Model for validating subscriber filters.
 * Contains the subscriber filter to validate and optional custom sample events.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class SubscriberFilterValidationInputModel {

  @JsonProperty("subscriber_filter")
  private final SubscriberFilterModel subscriberFilter;

  @JsonProperty("custom_sample_events")
  private final Set<CustomSampleEventModel> customSampleEventModels;

  public SubscriberFilterValidationInputModel(
      SubscriberFilterModel subscriberFilter,
      Set<CustomSampleEventModel> customSampleEventModels) {
    this.subscriberFilter = subscriberFilter;
    this.customSampleEventModels = customSampleEventModels;
  }

  public SubscriberFilterModel getSubscriberFilter() {
    return subscriberFilter;
  }

  public Set<CustomSampleEventModel> getCustomSampleEventModels() {
    return customSampleEventModels;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    SubscriberFilterValidationInputModel that = (SubscriberFilterValidationInputModel) o;
    return Objects.equals(subscriberFilter, that.subscriberFilter) && Objects.equals(customSampleEventModels, that.customSampleEventModels);
  }

  @Override
  public int hashCode() {
    return Objects.hash(subscriberFilter, customSampleEventModels);
  }

  @Override
  public String toString() {
    return "SubscriberFilterValidationInputModel{" +
        "subscriberFilter=" + subscriberFilter +
        ", customSampleEventModels=" + customSampleEventModels +
        '}';
  }
} 