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


@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ContentRepository extends XdmObject {

  protected String root;

  public ContentRepository() {
    super();
    this.type = XdmContext.XDM_CONTENT_REPOSITORY_TYPE;
  }

  @JsonProperty(XdmContext.XDM_CONTENT_REPOSITORY_PREFIX + ":root")
  public String getRoot() {
    return root;
  }

  public void setRoot(String root) {
    this.root = root;
  }

  @Override
  public String toString() {
    return "ContentRepository{" +
        "id='" + id + '\'' +
        ", type='" + type + '\'' +
        ", root='" + root + '\'' +
        '}';
  }
}