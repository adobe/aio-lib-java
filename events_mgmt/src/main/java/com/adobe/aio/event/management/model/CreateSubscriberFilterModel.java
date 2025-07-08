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
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;

/**
 * Model for creating a new subscriber filter.
 * Contains the name, description, and filter definition for the subscriber filter.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreateSubscriberFilterModel {

  @JsonProperty("name")
  private final String name;

  @JsonProperty("description")
  private final String description;

  @JsonProperty("subscriber_filter")
  private final String subscriberFilter;

  @JsonCreator
  public CreateSubscriberFilterModel(
      @JsonProperty("name") String name,
      @JsonProperty("description") String description,
      @JsonProperty("subscriber_filter") String subscriberFilter) {
    this.name = name;
    this.description = description;
    this.subscriberFilter = subscriberFilter;
  }

  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }

  public String getSubscriberFilter() {
    return subscriberFilter;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    CreateSubscriberFilterModel that = (CreateSubscriberFilterModel) o;
    return Objects.equals(name, that.name) && Objects.equals(description, that.description) && Objects.equals(subscriberFilter, that.subscriberFilter);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, description, subscriberFilter);
  }

  @Override
  public String toString() {
    return "CreateSubscriberFilterModel{" +
        "name='" + name + '\'' +
        ", description='" + description + '\'' +
        '}';
  }
} 