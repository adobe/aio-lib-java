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
package com.adobe.aio.aem.event.osgimapping.eventhandler;

import com.adobe.aio.aem.event.osgimapping.ocd.OsgiEventMappingConfig;

public class OsgiEventMapping {

  private String eventCode;
  private String xdmEventType;
  private String osgiTopic;
  private String osgiFilter;
  private String pathFilter;
  private String eventHandlerType;

  public OsgiEventMapping(OsgiEventMappingConfig conf) {
    this(conf.aio_event_code(), conf.aio_xdm_event_type(), conf.osgi_topic(),
        conf.osgi_filter(), conf.osgi_jcr_path_filter(), conf.osgi_event_handler_type());
  }

  // for jackson
  private OsgiEventMapping() {
  }

  private OsgiEventMapping(final String eventCode, final String xdmEventType,
      final String osgiTopic, final String osgiFilter, final String pathFilter,
      final String eventHandlerType) {
    this.eventCode = eventCode;
    this.xdmEventType = xdmEventType;
    this.osgiTopic = osgiTopic;
    this.osgiFilter = osgiFilter;
    this.pathFilter = pathFilter;
    this.eventHandlerType = eventHandlerType;
  }

  public String getEventCode() {
    return eventCode;
  }

  public String getXdmEventType() {
    return xdmEventType;
  }

  public String getOsgiTopic() {
    return osgiTopic;
  }

  public String getOsgiFilter() {
    return osgiFilter;
  }

  public String getPathFilter() {
    return pathFilter;
  }

  public String getEventHandlerType() {
    return eventHandlerType;
  }

  @Override
  public String toString() {
    return "OsgiEventMapping{" +
        "eventCode='" + eventCode + '\'' +
        ", xdmEventType='" + xdmEventType + '\'' +
        ", osgiTopic='" + osgiTopic + '\'' +
        ", osgiFilter='" + osgiFilter + '\'' +
        ", pathFilter='" + pathFilter + '\'' +
        ", eventHandlerType='" + eventHandlerType + '\'' +
        '}';
  }
}
