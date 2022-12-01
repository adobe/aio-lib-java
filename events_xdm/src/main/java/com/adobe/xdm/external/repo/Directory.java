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
package com.adobe.xdm.external.repo;

import static com.adobe.xdm.common.XdmContext.XDM_DIRECTORY_PREFIX;
import static com.adobe.xdm.common.XdmContext.XDM_DIRECTORY_TYPE;

import com.adobe.xdm.XdmObject;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Directory extends XdmObject {

  private String assetId;
  private String name;
  private String path;
  private String etag;
  private String format;

  public Directory() {
    this.type = XDM_DIRECTORY_TYPE;
  }

  @JsonProperty(XDM_DIRECTORY_PREFIX + ":asset_id")
  public String getAssetId() {
    return assetId;
  }

  public void setAssetId(String assetId) {
    this.assetId = assetId;
  }

  @JsonProperty(XDM_DIRECTORY_PREFIX + ":name")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @JsonProperty(XDM_DIRECTORY_PREFIX + ":path")
  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;
  }

  @JsonProperty(XDM_DIRECTORY_PREFIX + ":etag")
  public String getEtag() {
    return etag;
  }

  public void setEtag(String etag) {
    this.etag = etag;
  }

  @JsonProperty(XDM_DIRECTORY_PREFIX + ":format")
  public String getFormat() {
    return format;
  }

  public void setFormat(String format) {
    this.format = format;
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

    Directory directory = (Directory) o;

    if (assetId != null ? !assetId.equals(directory.assetId) : directory.assetId != null) {
      return false;
    }
    if (name != null ? !name.equals(directory.name) : directory.name != null) {
      return false;
    }
    if (path != null ? !path.equals(directory.path) : directory.path != null) {
      return false;
    }
    if (etag != null ? !etag.equals(directory.etag) : directory.etag != null) {
      return false;
    }
    return format != null ? format.equals(directory.format) : directory.format == null;
  }

  @Override
  public int hashCode() {
    int result = super.hashCode();
    result = 31 * result + (assetId != null ? assetId.hashCode() : 0);
    result = 31 * result + (name != null ? name.hashCode() : 0);
    result = 31 * result + (path != null ? path.hashCode() : 0);
    result = 31 * result + (etag != null ? etag.hashCode() : 0);
    result = 31 * result + (format != null ? format.hashCode() : 0);
    return result;
  }

  @Override
  public String toString() {
    return "Directory{" +
        "assetId='" + assetId + '\'' +
        ", name='" + name + '\'' +
        ", path='" + path + '\'' +
        ", etag='" + etag + '\'' +
        ", format='" + format + '\'' +
        ", id='" + id + '\'' +
        ", type='" + type + '\'' +
        '}';
  }
}
