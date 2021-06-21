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
package com.adobe.event.management.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;
import org.apache.commons.lang3.StringUtils;

/**
 * The Input Model necessary to update/PUT an Adobe I/O Events Provider
 */
public class ProviderInputModel {

  /**
   * The label of this Events Provider, as shown on the Adobe I/O console
   */
  protected final String label;
  protected final String description;
  /**
   * The documentation url of this Events Provider, as shown on the Adobe I/O console
   */
  @JsonProperty("docs_url")
  protected final String docsUrl;

  protected ProviderInputModel(final String label, final String description, final String docsUrl) {
    if (StringUtils.isEmpty(label)) {
      throw new IllegalArgumentException(
          "ProviderUpdateModel is missing a label");
    }
    this.label = label;
    this.description = description;
    this.docsUrl = docsUrl;
  }

  public String getLabel() {
    return this.label;
  }

  public String getDescription() {
    return this.description;
  }

  public String getDocsUrl() {
    return this.docsUrl;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ProviderInputModel that = (ProviderInputModel) o;
    return Objects.equals(label, that.label) &&
        Objects.equals(description, that.description) &&
        Objects.equals(docsUrl, that.docsUrl);
  }

  @Override
  public int hashCode() {
    return Objects.hash(label, description, docsUrl);
  }

  @Override
  public String toString() {
    return "ProviderUpdateModel{" +
        "label='" + label + '\'' +
        ", description='" + description + '\'' +
        ", docsUrl='" + docsUrl + '\'' +
        '}';
  }

  @JsonIgnore
  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {

    private String label;
    private String description;
    private String docsUrl;

    public Builder() {
    }

    public Builder label(final String label) {
      this.label = label;
      return this;
    }

    public Builder description(final String description) {
      this.description = description;
      return this;
    }

    public Builder docsUrl(final String docsUrl) {
      this.docsUrl = docsUrl;
      return this;
    }

    public ProviderInputModel build() {
      return new ProviderInputModel(label, description, docsUrl);
    }
  }
}
