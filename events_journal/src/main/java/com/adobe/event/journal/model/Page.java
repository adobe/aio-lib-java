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

import java.util.Objects;

public class Page {

  /**
   * position corresponding to the last event returned in this batch
   */
  private String last;
  /**
   * number of events returned in this batch
   */
  private int count;

  public String getLast() {
    return last;
  }

  public int getCount() {
    return count;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Page page = (Page) o;
    return count == page.count &&
        Objects.equals(last, page.last);
  }

  @Override
  public int hashCode() {
    return Objects.hash(last, count);
  }

  @Override
  public String toString() {
    return "Page{" +
        "last='" + last + '\'' +
        ", count=" + count +
        '}';
  }
}
