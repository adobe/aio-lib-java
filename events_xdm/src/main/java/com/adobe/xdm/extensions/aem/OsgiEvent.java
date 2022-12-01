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
package com.adobe.xdm.extensions.aem;

import com.adobe.xdm.XdmObject;
import com.adobe.xdm.common.XdmContext;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Hashtable;

@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class OsgiEvent extends XdmObject {

  String topic;
  Hashtable properties;

  public OsgiEvent() {
    super();
    this.type = XdmContext.OSGI_EVENT_TYPE;
  }

  @JsonProperty(XdmContext.OSGI_EVENT_PREFIX + ":topic")
  public String getTopic() {
    return topic;
  }

  public void setTopic(String topic) {
    this.topic = topic;
    this.type =  XdmContext.OSGI_EVENT_TYPE +":"+topic;
  }

  @JsonProperty(XdmContext.OSGI_EVENT_PREFIX + ":properties")
  public Hashtable getProperties() {
    return properties;
  }

  public void setProperties(Hashtable properties) {
    this.properties = properties;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    if (!super.equals(o)) {
      return false;
    }

    OsgiEvent that = (OsgiEvent) o;

    if (topic != null ? !topic.equals(that.topic) : that.topic != null) {
      return false;
    }
    return properties != null ? properties.equals(that.properties) : that.properties == null;
  }

  @Override
  public int hashCode() {
    int result = super.hashCode();
    result = 31 * result + (topic != null ? topic.hashCode() : 0);
    result = 31 * result + (properties != null ? properties.hashCode() : 0);
    return result;
  }

  @Override
  public String toString() {
    return "OsgiEvent{" +
        "id='" + id + '\'' +
        ", type='" + type + '\'' +
        ", topic='" + topic + '\'' +
        ", properties=" + properties +
        '}';
  }
}