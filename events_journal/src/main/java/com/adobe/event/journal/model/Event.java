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
package com.adobe.event.journal.model;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.Objects;

public class Event {

  /**
   * unique position of this event in the journal
   */
  private String position;
  /**
   * actual event data
   */
  private JsonNode event;

  public JsonNode getEvent() {
    return event;
  }

  public String getPosition() {
    return position;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Event event1 = (Event) o;
    return Objects.equals(position, event1.position) &&
        Objects.equals(event, event1.event);
  }

  @Override
  public int hashCode() {
    return Objects.hash(position, event);
  }

  @Override
  public String toString() {
    return "Event{" +
        "position='" + position + '\'' +
        ", event='" + event + '\'' +
        '}';
  }
}
