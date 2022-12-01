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

@Resource
@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class RegistrationPaginatedModel extends RegistrationCollection{

  @Link
  private HALLink next;

  @Link
  private HALLink prev;

  @Link
  private HALLink first;

  @Link
  private HALLink last;

  private PageMetadata page;

  public HALLink getNext() {
    return next;
  }

  public HALLink getPrev() {
    return prev;
  }

  public void setPrev(HALLink prev) {
    this.prev = prev;
  }

  public HALLink getFirst() {
    return first;
  }

  public HALLink getLast() {
    return last;
  }

  public PageMetadata getPage() {
    return page;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof RegistrationPaginatedModel)) {
      return false;
    }
    if (!super.equals(o)) {
      return false;
    }
    RegistrationPaginatedModel that = (RegistrationPaginatedModel) o;
    return Objects.equals(next, that.next) &&
        Objects.equals(prev, that.prev) &&
        Objects.equals(first, that.first) &&
        Objects.equals(last, that.last) &&
        Objects.equals(page, that.page);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), next, prev, first, last, page);
  }

  @Override
  public String toString() {
    return "RegistrationPaginatedHalModel{" +
        "next=" + next +
        ", prev=" + prev +
        ", first=" + first +
        ", last=" + last +
        ", page=" + page +
        ", self=" + getSelf() +
        ", Registrations=" + getRegistrationHalModels() +
        '}';
  }

  public static class PageMetadata {
    private final int size; //page size
    private final int number;// page number
    private final int numberOfElements; // number of elements in page fetched
    private final long totalElements; // total number of elements
    private final int totalPages; // total number of pages

    @JsonCreator
    public PageMetadata(@JsonProperty("size") int size,
                    @JsonProperty("number") int number,
                    @JsonProperty("numberOfElements") int numberOfElements,
                    @JsonProperty("totalElements") long totalElements,
                    @JsonProperty("totalPages") int totalPages) {
      this.size = size;
      this.number = number;
      this.numberOfElements = numberOfElements;
      this.totalElements = totalElements;
      this.totalPages = totalPages;
    }

    public int getSize() {
      return size;
    }

    public int getNumber() {
      return number;
    }

    public int getNumberOfElements() {
      return numberOfElements;
    }

    public long getTotalElements() {
      return totalElements;
    }

    public int getTotalPages() {
      return totalPages;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }
      PageMetadata that = (PageMetadata) o;
      return size == that.size &&
          number == that.number &&
          numberOfElements == that.numberOfElements &&
          totalElements == that.totalElements &&
          totalPages == that.totalPages;
    }

    @Override
    public int hashCode() {
      return Objects.hash(size, number, numberOfElements, totalElements, totalPages);
    }

    @Override
    public String toString() {
      return "PageMetadata{" +
          "size=" + size +
          ", number=" + number +
          ", numberOfElements=" + numberOfElements +
          ", totalElements=" + totalElements +
          ", totalPages=" + totalPages +
          '}';
    }
  }
}
