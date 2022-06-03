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

import com.adobe.aio.util.Constants;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;
import org.apache.commons.lang3.StringUtils;

/**
 * The Input Model necessary to POST/PUT an Adobe I/O Events Provider
 */
@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(
    ignoreUnknown = true
)
public class ProviderInputModel {

  /**
   * Optional key when creating/POST-ing a new provider.
   * Note it will be ignored when updating/PUT-ing it.
   * If none is provided our API will create a Random UUID for you.
   */
  @JsonProperty("instance_id")
  private final String instanceId;

  /**
   * Optional provider providerMetadataId/type when creating/POST-ing a new provider.
   * Note it will be ignored when updating/PUT-ing it.
   * If none is provided our API will assume you want to create a `Custom Events` Provider
   * and hence will use the associated CUSTOM_EVENTS_PROVIDER_METADATA_ID.
   * @see com.adobe.aio.util.Constants#CUSTOM_EVENTS_PROVIDER_METADATA_ID
   */
  @JsonProperty("provider_metadata")
  private final String providerMetadataId;

  /**
   * The label of this Events Provider, as shown on the Adobe I/O console
   */
  private final String label;
  private final String description;
  /**
   * The documentation url of this Events Provider, as shown on the Adobe I/O console
   */
  @JsonProperty("docs_url")
  private final String docsUrl;

  private ProviderInputModel(final String label, final String description, final String docsUrl, final String instanceId,
      final String providerMetadataId) {
    if (StringUtils.isEmpty(label)) {
      throw new IllegalArgumentException(
          "ProviderUpdateModel is missing a label");
    }
    this.providerMetadataId = (StringUtils.isEmpty(providerMetadataId)) ?
        Constants.CUSTOM_EVENTS_PROVIDER_METADATA_ID : providerMetadataId;
    this.label = label;
    this.description = description;
    this.docsUrl = docsUrl;
    this.instanceId = instanceId;
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

  public String getInstanceId() { return this.instanceId; }

  public String getProviderMetadataId() { return this.providerMetadataId; }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ProviderInputModel that = (ProviderInputModel) o;
    return Objects.equals(instanceId, that.instanceId) && Objects.equals(providerMetadataId,
        that.providerMetadataId) && Objects.equals(label, that.label) && Objects.equals(
        description, that.description) && Objects.equals(docsUrl, that.docsUrl);
  }

  @Override
  public int hashCode() {
    return Objects.hash(instanceId, providerMetadataId, label, description, docsUrl);
  }

  @Override
  public String toString() {
    return "ProviderInputModel{" +
        "label='" + label + '\'' +
        ", description='" + description + '\'' +
        ", docsUrl='" + docsUrl + '\'' +
        ", providerMetadataId='" + providerMetadataId + '\'' +
        ", instanceId='" + instanceId + '\'' +
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
    private String instanceId;
    private String providerMetadataId;

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

    public Builder providerMetadataId(final String providerMetadataId) {
      this.providerMetadataId = providerMetadataId;
      return this;
    }

    public Builder instanceId(final String instanceId) {
      this.instanceId = instanceId;
      return this;
    }

    public ProviderInputModel build() {
      return new ProviderInputModel(label, description, docsUrl, instanceId, providerMetadataId);
    }
  }
}
