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

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;

/**
 * The Adobe I/O Events Provider model
 */
public class Provider {

  @JsonProperty("id")
  private String id = null;

  @JsonProperty("label")
  private String label = null;

  @JsonProperty("description")
  private String description = null;

  @JsonProperty("source")
  private final String source = null;

  @JsonProperty("docs_url")
  private String docsUrl = null;

  @JsonProperty("publisher")
  private String publisher = null;

  public Provider id(String id) {
    this.id = id;
    return this;
  }

  /**
   * The Id of this Events Provider
   *
   * @return id
   **/
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public Provider label(String label) {
    this.label = label;
    return this;
  }

  /**
   * The label of this Events Provider, as shown on the Adobe I/O console
   *
   * @return label
   **/
  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  public Provider description(String description) {
    this.description = description;
    return this;
  }

  /**
   * The description of this Events Provider, as shown on the Adobe I/O console
   *
   * @return description
   **/
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  /**
   * A URI-reference (this provider id prefixed with &#x60;urn:uuid:&#x60;), identifying the context
   * in which an event happened. Producers MUST ensure that source + event.id is unique for each
   * distinct event. See https://github.com/cloudevents/spec/blob/master/spec.md#source-1
   *
   * @return source
   **/
  public String getSource() {
    return source;
  }

  public Provider docsUrl(String docsUrl) {
    this.docsUrl = docsUrl;
    return this;
  }

  /**
   * The documentation url of this Events Provider, as shown on the Adobe I/O console
   *
   * @return docsUrl
   **/
  public String getDocsUrl() {
    return docsUrl;
  }

  public void setDocsUrl(String docsUrl) {
    this.docsUrl = docsUrl;
  }

  public Provider publisher(String publisher) {
    this.publisher = publisher;
    return this;
  }

  /**
   * The publisher is &#x60;Adobe&#x60; for Adobe Cloud Solution. In the case of multi-instances or
   * on-premise Adobe solutions:  the publisher will be set to the organization_id owning this
   * solution. In the case of event-providers registered by an organization using Adobe I/O
   * registration API:  the publisher will be set to the organization_id which registered this event
   * providers
   *
   * @return publisher
   **/
  public String getPublisher() {
    return publisher;
  }

  public void setPublisher(String publisher) {
    this.publisher = publisher;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Provider providerHalModel = (Provider) o;
    return Objects.equals(this.id, providerHalModel.id) &&
        Objects.equals(this.label, providerHalModel.label) &&
        Objects.equals(this.description, providerHalModel.description) &&
        Objects.equals(this.source, providerHalModel.source) &&
        Objects.equals(this.docsUrl, providerHalModel.docsUrl) &&
        Objects.equals(this.publisher, providerHalModel.publisher);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, label, description, source, docsUrl, publisher);
  }

  @Override
  public String toString() {
    return "Provider{" +
        " label='" + label + '\'' +
        ", id='" + id + '\'' +
        ", description='" + description + '\'' +
        ", source='" + source + '\'' +
        ", docsUrl='" + docsUrl + '\'' +
        ", publisher='" + publisher + '\'' +
        '}';
  }
}
