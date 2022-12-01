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
package com.adobe.xdm.event;

import com.adobe.xdm.common.XdmContext;
import com.adobe.xdm.common.XdmEvent;
import com.adobe.xdm.content.ContentRepository;
import com.adobe.xdm.extensions.aem.AemUser;
import com.adobe.xdm.extensions.aem.OsgiEvent;
import com.adobe.xdm.extensions.ims.ImsOrg;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class OsgiEmittedEvent extends
    XdmEvent<OsgiEvent, ImsOrg, ContentRepository, AemUser> {

  public OsgiEmittedEvent() {
    super();
    this.object = new OsgiEvent();
    this.type = XdmContext.XDM_EVENT_EMITTED_TYPE;
  }


}
