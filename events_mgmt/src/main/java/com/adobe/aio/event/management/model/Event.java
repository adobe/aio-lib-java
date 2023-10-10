/*
 * Copyright 2023 Adobe. All rights reserved.
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
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.Objects;

/**
 * Event POJO that is fetched from the AIO Event Test Webhook App (a.k.a eg-auditor) endpoint
 */
@JsonInclude(Include.NON_NULL)
public class Event {

  private final String id;

  private final String env;

  private final String clientId;

  private final String registrationId;

  private final String timeStamp;

  private final JsonNode headers;

  private final JsonNode message;

  @JsonCreator
  public Event(@JsonProperty("id") String id, @JsonProperty("env") String env,
      @JsonProperty("clientId") String clientId,
      @JsonProperty("registrationId") String registrationId,
      @JsonProperty("timestamp") String timeStamp,
      @JsonProperty("headers") JsonNode headers,
      @JsonProperty("message") JsonNode message) {
    this.id = id;
    this.env = env;
    this.clientId = clientId;
    this.registrationId = registrationId;
    this.timeStamp = timeStamp;
    this.headers = headers;
    this.message = message;
  }

  public String getId() {
    return id;
  }

  public String getEnv() {
    return env;
  }

  public String getClientId() {
    return clientId;
  }

  public String getRegistrationId() {
    return registrationId;
  }

  public String getTimeStamp() {
    return timeStamp;
  }

  public JsonNode getHeaders() {
    return headers;
  }

  public JsonNode getMessage() {
    return message;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Event)) {
      return false;
    }
    Event event = (Event) o;
    return Objects.equals(id, event.id) && Objects.equals(env, event.env)
        && Objects.equals(clientId, event.clientId) && Objects.equals(
        registrationId, event.registrationId) && Objects.equals(timeStamp, event.timeStamp)
        && Objects.equals(headers, event.headers) && Objects.equals(message,
        event.message);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, env, clientId, registrationId, timeStamp, headers, message);
  }

  @Override
  public String toString() {
    return "Event{" +
        "id='" + id + '\'' +
        ", env='" + env + '\'' +
        ", clientId='" + clientId + '\'' +
        ", registrationId='" + registrationId + '\'' +
        ", timeStamp='" + timeStamp + '\'' +
        ", headers='" + headers + '\'' +
        ", message='" + message + '\'' +
        '}';
  }
}
