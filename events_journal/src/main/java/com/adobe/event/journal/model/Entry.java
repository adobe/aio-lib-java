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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Entry {

  private static final String NEXT = "next";

  /**
   * ordered list of events
   */
  private List<Event> events;
  @JsonProperty("_page")
  private Page page;

  @JsonIgnore
  private Map<String, String> links = new HashMap<>();

  /**
   * will be set when empty()
   */
  @JsonIgnore
  private int retryAfterInSeconds;

  public List<Event> getEvents() {
    return events;
  }

  public Page getPage() {
    return page;
  }

  @JsonIgnore
  public String getNextLink() {
    return links.get(NEXT);
  }

  @JsonIgnore
  public Map<String, String> getLinks() {
    return links;
  }

  public void setLinks(Map<String, String> links) {
    this.links = links;
  }

  @JsonIgnore
  public int getRetryAfterInSeconds() {
    return retryAfterInSeconds;
  }

  public void setRetryAfterInSeconds(String retryAfterInSeconds) {
    this.retryAfterInSeconds = Integer.valueOf(retryAfterInSeconds);
  }

  @JsonIgnore
  public int size() {
    return this.isEmpty() ? 0 : events.size();
  }

  @JsonIgnore
  public boolean isEmpty() {
    return (events == null || events.isEmpty());
  }

  @Override
  public String toString() {
    if (!this.isEmpty()) {
      return "Entry{" +
          "events=" + events +
          ", page=" + page +
          ", links=" + links +
          '}';
    } else {
      return "Entry{" +
          "retryAfterInSeconds=" + retryAfterInSeconds +
          ", links=" + links +
          '}';
    }
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Entry entry = (Entry) o;
    return retryAfterInSeconds == entry.retryAfterInSeconds &&
        Objects.equals(events, entry.events) &&
        Objects.equals(page, entry.page) &&
        Objects.equals(links, entry.links);
  }

  @Override
  public int hashCode() {
    return Objects.hash(events, page, links, retryAfterInSeconds);
  }
}
