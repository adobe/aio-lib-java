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
package com.adobe.aio.aem.event.osgimapping.ocd;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name = "Adobe I/O Events' OSGI Event Mapping",
    description = "Adobe I/O Events' OSGI Event Mapping")
public @interface OsgiEventMappingConfig {

  String EMPTY = "";
  String OSGI_EVENT_HANDLER_CLASS_NAME_DEFAULT = "com.adobe.io.event.osgi.eventhandler.OsgiEventHandler";
  String IO_XDM_EVENT_CLASS_NAME_DEFAULT = "com.adobe.xdm.event.OsgiEmittedEvent";

  @AttributeDefinition(name = "Adobe I/O Event Code",
      description = "Adobe I/O Event Code (a code unique to your Event provider, i.e. unique to your AEM instance/cluster")
  String aio_event_code() default EMPTY;

  @AttributeDefinition(name = "Adobe I/O Event Label",
      description = "Adobe I/O Event Label as it will appear on the Adobe I/O Developer Console")
  String aio_event_label() default EMPTY;

  @AttributeDefinition(name = "Adobe I/O Event Label",
      description = "Adobe I/O Event Description as it will appear on the Adobe I/O Developer Console")
  String aio_event_description() default EMPTY;

  @AttributeDefinition(name = "Adobe I/O XDM Event Type",
      description = "Adobe I/O XDM Event Type (i.e Class Name) to map the OSGI event to, use the default to map your custom osgi events")
  String aio_xdm_event_type() default IO_XDM_EVENT_CLASS_NAME_DEFAULT;

  @AttributeDefinition(name = "Osgi Topic",
      description = "The OSGI Topic you want to observe")
  String osgi_topic() default EMPTY;

  @AttributeDefinition(name = "Osgi Filter",
      description = "The OSGI Filter you want to apply in your osgi event observation, if left empty no osgi filtering is done")
  String osgi_filter() default EMPTY;

  @AttributeDefinition(name = "OSGI JCR Path Filter",
      description = "The JCR osgiJcrPathFilter to filter the OSGI events further, if left empty no resource path filtering is done")
  String osgi_jcr_path_filter() default EMPTY;

  @AttributeDefinition(name = "Osgi Event Handler Type",
      description = "Osgi Event Handler Type ( i.e Class Name) use the default to map any osgi event)")
  String osgi_event_handler_type() default OSGI_EVENT_HANDLER_CLASS_NAME_DEFAULT;

}
