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
package com.adobe.xdm.assets;

import com.adobe.xdm.XdmObject;
import com.adobe.xdm.common.XdmContext;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Asset extends XdmObject {

  private String assetId;

  private String assetName;

  private String etag;

  private String path;

  private String format;


  public Asset() {
    super();
    this.type = XdmContext.XDM_ASSET_TYPE;
  }

  @JsonProperty(XdmContext.XDM_ASSET_PREFIX + ":asset_id")
  public String getAssetId() {
    return this.assetId;
  }

  public void setAssetId(String assetId) {
    this.assetId = assetId;
  }

  @JsonProperty(XdmContext.XDM_ASSET_PREFIX + ":asset_name")
  public String getAssetName() {
    return assetName;
  }

  public void setAssetName(String assetName) {
    this.assetName = assetName;
  }

  @JsonProperty(XdmContext.XDM_ASSET_PREFIX + ":etag")
  public String getEtag() {
    return etag;
  }

  public void setEtag(String etag) {
    this.etag = etag;
  }

  @JsonProperty(XdmContext.XDM_ASSET_PREFIX + ":path")
  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;
  }

  @JsonProperty(XdmContext.XDM_ASSET_PREFIX + ":format")
  public String getFormat() {
    return this.format;
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

    Asset asset = (Asset) o;

    if (assetName != null ? !assetName.equals(asset.assetName) : asset.assetName != null) {
      return false;
    }
    if (etag != null ? !etag.equals(asset.etag) : asset.etag != null) {
      return false;
    }
    if (path != null ? !path.equals(asset.path) : asset.path != null) {
      return false;
    }
    return format != null ? format.equals(asset.format) : asset.format == null;
  }

  @Override
  public int hashCode() {
    int result = super.hashCode();
    result = 31 * result + (assetName != null ? assetName.hashCode() : 0);
    result = 31 * result + (etag != null ? etag.hashCode() : 0);
    result = 31 * result + (path != null ? path.hashCode() : 0);
    result = 31 * result + (format != null ? format.hashCode() : 0);
    return result;
  }

  @Override
  public String toString() {
    return "Asset{" +
        "id='" + id + '\'' +
        ", type='" + type + '\'' +
        ", assetId='" + assetId + '\'' +
        ", assetName='" + assetName + '\'' +
        ", etag='" + etag + '\'' +
        ", path='" + path + '\'' +
        ", format='" + format + '\'' +
        '}';
  }
}