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
package com.adobe.xdm.extensions.ims;

import com.adobe.xdm.XdmObject;
import com.adobe.xdm.common.XdmContext;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ImsOrg extends XdmObject {

  private String imsOrgId;

  public ImsOrg() {
    super();
    this.type = XdmContext.XDM_IMS_ORG_TYPE;
  }

  @JsonProperty(XdmContext.XDM_IMS_ORG_PREFIX + ":id")
  public String getImsOrgId() {
    return imsOrgId;
  }


  public void setImsOrgId(String imsOrgId) {
    this.imsOrgId = imsOrgId;
  }

  @Override
  public String toString() {
    return "ImsOrg{" +
        "id='" + id + '\'' +
        ", type='" + type + '\'' +
        ", imsOrgId='" + imsOrgId + '\'' +
        '}';
  }
}