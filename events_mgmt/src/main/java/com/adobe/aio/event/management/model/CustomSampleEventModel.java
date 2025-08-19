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
import com.fasterxml.jackson.databind.JsonNode;
import java.util.Objects;

/**
 * Model representing a custom sample event used for subscriber filter validation.
 * Contains a sample payload and a name for identification.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CustomSampleEventModel {

  @JsonProperty("sample_payload")
  private final JsonNode samplePayload;

  @JsonProperty("name")
  private final String name;

  public CustomSampleEventModel(JsonNode samplePayload, String name) {
    this.samplePayload = samplePayload;
    this.name = name;
  }

  public JsonNode getSamplePayload() {
    return samplePayload;
  }

  public String getName() {
    return name;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    CustomSampleEventModel that = (CustomSampleEventModel) o;
    return Objects.equals(samplePayload, that.samplePayload) && Objects.equals(name, that.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(samplePayload, name);
  }

  @Override
  public String toString() {
    return "CustomSampleEventModel{" + "name='" + name + '\'' + '}';
  }
} 