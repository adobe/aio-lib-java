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
package com.adobe.aio.aem.event.management.ocd;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name = "Adobe I/O Events' Event Metadata",
    description = "Adobe I/O Events' Event Metadata")
public @interface EventMetadataConfig {

  String EMPTY = "";

  @AttributeDefinition(name = "Adobe I/O Event Code",
      description = "Adobe I/O Event Code (a code unique to your Event provider, i.e. unique to your AEM instance/cluster")
  String aio_event_code() default EMPTY;

  @AttributeDefinition(name = "Adobe I/O Event Label",
      description = "Adobe I/O Event Label as it will appear on the Adobe I/O Developer Console")
  String aio_event_label() default EMPTY;

  @AttributeDefinition(name = "Adobe I/O Event Label",
      description = "Adobe I/O Event Description as it will appear on the Adobe I/O Developer Console")
  String aio_event_description() default EMPTY;

}
