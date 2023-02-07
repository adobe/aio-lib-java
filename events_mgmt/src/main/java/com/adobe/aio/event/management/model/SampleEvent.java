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


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRawValue;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.Objects;

@JsonInclude(Include.NON_NULL)
public class SampleEvent {

  /**
   * the delivery format of the sample Event
   */
  private String format;

  /**
   * A json sample event json Object as expected in the webhook or journal deliveries
   */
  @JsonProperty("sample_payload")
  protected JsonNode samplePayload;


  public String getFormat() {
    return format;
  }

  /**
   * @return A json sample event payload as expected in the webhook or journal deliveries
   */
  @JsonRawValue
  public String getSamplePayload() {
    return samplePayload == null ? null : samplePayload.toString();
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SampleEvent that = (SampleEvent) o;
    return Objects.equals(format, that.format) && Objects.equals(samplePayload,
        that.samplePayload);
  }

  @Override
  public int hashCode() {
    return Objects.hash(format, samplePayload);
  }

  @Override
  public String toString() {
    return "SampleEventModel{" +
        "format='" + format + '\'' +
        ", samplePayload=" + samplePayload +
        '}';
  }
}
