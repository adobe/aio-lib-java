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
package com.adobe.xdm.common;

import com.adobe.xdm.XdmObject;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class XdmEvent
    <Ob extends XdmObject,
        To extends XdmObject,
        Ge extends XdmObject,
        Ac extends XdmObject> {

  protected String id;
  protected String type;
  protected String published;
  protected To to;
  protected Ge generator;
  protected Ac actor;
  protected Ob object;

  @JsonProperty("@id")
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  @JsonProperty("@type")
  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  @JsonProperty(XdmContext.XDM_EVENT_ENVELOPE_PREFIX + ":objectType")
  public String getObjectType() {
    return (object == null) ? null : object.getType();
  }

  @JsonProperty(XdmContext.W3C_ACTIVITYSTREAMS_PREFIX + ":published")
  public String getPublished() {
    return published;
  }

  public void setPublished(String published) {
    this.published = published;
  }

  @JsonProperty(XdmContext.W3C_ACTIVITYSTREAMS_PREFIX + ":to")
  public To getTo() {
    return to;
  }

  public void setTo(To to) {
    this.to = to;
  }

  @JsonProperty(XdmContext.W3C_ACTIVITYSTREAMS_PREFIX + ":actor")
  public Ac getActor() {
    return actor;
  }

  public void setActor(Ac actor) {
    this.actor = actor;
  }

  @JsonProperty(XdmContext.W3C_ACTIVITYSTREAMS_PREFIX + ":generator")
  public Ge getGenerator() {
    return generator;
  }

  public void setGenerator(Ge generator) {
    this.generator = generator;
  }

  @JsonProperty(XdmContext.W3C_ACTIVITYSTREAMS_PREFIX + ":object")
  public Ob getObject() {
    return object;
  }

  public void setObject(Ob object) {
    this.object = object;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || !this.getClass().isAssignableFrom(o.getClass())) {
      return false;
    }

    XdmEvent<?, ?, ?, ?> xdmEvent = (XdmEvent<?, ?, ?, ?>) o;

    if (id != null ? !id.equals(xdmEvent.id) : xdmEvent.id != null) {
      return false;
    }
    if (type != null ? !type.equals(xdmEvent.type) : xdmEvent.type != null) {
      return false;
    }
    if (published != null ? !published.equals(xdmEvent.published) : xdmEvent.published != null) {
      return false;
    }
    if (to != null ? !to.equals(xdmEvent.to) : xdmEvent.to != null) {
      return false;
    }
    if (generator != null ? !generator.equals(xdmEvent.generator) : xdmEvent.generator != null) {
      return false;
    }
    if (actor != null ? !actor.equals(xdmEvent.actor) : xdmEvent.actor != null) {
      return false;
    }
    return object != null ? object.equals(xdmEvent.object) : xdmEvent.object == null;
  }

  @Override
  public int hashCode() {
    int result = id != null ? id.hashCode() : 0;
    result = 31 * result + (type != null ? type.hashCode() : 0);
    result = 31 * result + (published != null ? published.hashCode() : 0);
    result = 31 * result + (to != null ? to.hashCode() : 0);
    result = 31 * result + (generator != null ? generator.hashCode() : 0);
    result = 31 * result + (actor != null ? actor.hashCode() : 0);
    result = 31 * result + (object != null ? object.hashCode() : 0);
    return result;
  }

  @Override
  public String toString() {
    return "XdmEvent{" +
        "id='" + id + '\'' +
        ", type='" + type + '\'' +
        ", published='" + published + '\'' +
        ", to=" + to +
        ", generator=" + generator +
        ", actor=" + actor +
        ", object=" + object +
        '}';
  }
}
