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

import static com.adobe.aio.event.management.model.ProviderInputModel.DELIVERY_FORMAT_CLOUD_EVENTS_V1;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name = "Adobe I/O Events' Provider Configuration",
    description = "Adobe I/O Events' Provider Configuration")
public @interface EventProviderConfig {

  @AttributeDefinition(name = "AEM Externalizer Name",
      description = "AEM Link externalizer name (used to compute the aio provider instanceId")
  String externalizer_name() default "author";

  // Event Provider related
  @AttributeDefinition(name = "Event Provider Label (Optional)",
      description = "Please provide a label of your choice for your Custom Events Provider. If empty, the label will be set using the Externalizer URL")
  String aio_provider_label() default "";

  @AttributeDefinition(name = "Event Provider Description (Optional)",
      description = "Please provide a description of your Custom Events Provider. If empty, the description will be set using the Externalizer URL")
  String aio_provider_description() default "";

  @AttributeDefinition(name = "Event Provider Documentation URL (Optional)",
      description = "your custom documentation URL")
  String aio_provider_docs_url() default "https://developer.adobe.com/events/docs/guides/using/aem/";

  @AttributeDefinition(name = "Event Delivery Format",
      description = "Event Delivery Format: either the old legacy`adobe_io` format (used in AIO former AEM integration)"
          + " or better the `cloud_events_v1` open specification (see https://github.com/cloudevents/spec/blob/v1.0/spec.md)")
  String event_delivery_format() default DELIVERY_FORMAT_CLOUD_EVENTS_V1;

}