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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * The Adobe I/O Events Provider model
 */
public class Provider {

  @JsonProperty("id")
  private String id;

  @JsonProperty("label")
  private String label;

  @JsonProperty("description")
  private String description;

  @JsonProperty("source")
  private String source;

  @JsonProperty("docs_url")
  private String docsUrl;

  @JsonProperty("publisher")
  private String publisher;

  /**
   * the associated EventMetadata can be eager loaded by the provider http API
   */
  @JsonProperty("_embedded")
  private EventMetadataCollection.EventMetadataList embeddedEventMetadata;

  /**
   * The Id of this Events Provider
   *
   * @return id
   **/
  public String getId() {
    return id;
  }

  /**
   * The label of this Events Provider, as shown on the Adobe I/O console
   *
   * @return label
   **/
  public String getLabel() {
    return label;
  }

  /**
   * The description of this Events Provider, as shown on the Adobe I/O console
   *
   * @return description
   **/
  public String getDescription() {
    return description;
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

  /**
   * The documentation url of this Events Provider, as shown on the Adobe I/O console
   *
   * @return docsUrl
   **/
  public String getDocsUrl() {
    return docsUrl;
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

  public EventMetadataCollection.EventMetadataList getEmbeddedEventMetadata() {
    return embeddedEventMetadata;
  }

  @JsonIgnore
  public List<EventMetadata> getEventMetadata() {
    return (embeddedEventMetadata!=null) ?
        embeddedEventMetadata.getEventmetadata() : new ArrayList<>();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Provider provider = (Provider) o;
    return Objects.equals(id, provider.id) &&
        Objects.equals(label, provider.label) &&
        Objects.equals(description, provider.description) &&
        Objects.equals(source, provider.source) &&
        Objects.equals(docsUrl, provider.docsUrl) &&
        Objects.equals(publisher, provider.publisher) &&
        Objects.equals(embeddedEventMetadata, provider.embeddedEventMetadata);
  }

  @Override
  public int hashCode() {
    return Objects
        .hash(id, label, description, source, docsUrl, publisher, embeddedEventMetadata);
  }

  @Override
  public String toString() {
    return "Provider{" +
        "id='" + id + '\'' +
        ", label='" + label + '\'' +
        ", description='" + description + '\'' +
        ", source='" + source + '\'' +
        ", docsUrl='" + docsUrl + '\'' +
        ", publisher='" + publisher + '\'' +
        ", eventMetadata=" + embeddedEventMetadata +
        '}';
  }
}
