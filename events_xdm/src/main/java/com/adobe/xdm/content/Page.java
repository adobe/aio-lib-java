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
package com.adobe.xdm.content;

import com.adobe.xdm.XdmObject;
import com.adobe.xdm.common.XdmContext;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;

@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Page extends XdmObject {

  private String title;
  private String path;
  private String version;

  public Page() {
    super();
    this.type = XdmContext.XDM_COMPONENTIZED_PAGE_TYPE;
  }

  @JsonProperty(XdmContext.XDM_COMPONENTIZED_PAGE_PREFIX + ":title")
  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  @JsonProperty(XdmContext.XDM_COMPONENTIZED_PAGE_PREFIX + ":path")
  public String getPath() {
    return path;
  }

  public void setPath(String pathname) {
    this.path = pathname;
  }

  @JsonProperty(XdmContext.XDM_COMPONENTIZED_PAGE_PREFIX + ":version")
  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
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
    Page page = (Page) o;
    return Objects.equals(id, page.id) &&
        Objects.equals(type, page.type) &&
        Objects.equals(title, page.title) &&
        Objects.equals(path, page.path) &&
        Objects.equals(version, page.version);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), id, type, title, path, version);
  }

  @Override
  public String toString() {
    return "Page{" +
        "title='" + title + '\'' +
        ", path='" + path + '\'' +
        ", version='" + version + '\'' +
        ", id='" + id + '\'' +
        ", type='" + type + '\'' +
        '}';
  }
}