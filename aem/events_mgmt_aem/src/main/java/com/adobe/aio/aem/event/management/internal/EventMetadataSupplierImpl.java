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
package com.adobe.aio.aem.event.management.internal;

import com.adobe.aio.aem.event.management.EventMetadataRegistrationService;
import com.adobe.aio.aem.event.management.EventMetadataSupplier;
import com.adobe.aio.aem.event.management.EventProviderRegistrationService;
import com.adobe.aio.aem.event.management.ocd.EventMetadataConfig;
import com.adobe.aio.event.management.model.EventMetadata;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.sling.event.jobs.JobManager;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(immediate = true, service = EventMetadataSupplier.class, property = {
    "label = Adobe I/O Events' Event Metadata Supplier Service",
    "description = Adobe I/O Events' Event Metadata Supplier Service"
}
)
@Designate(ocd = EventMetadataConfig.class, factory = true)
public class EventMetadataSupplierImpl implements EventMetadataSupplier {

  private final Logger log = LoggerFactory.getLogger(getClass());
  private ObjectMapper objectMapper = new ObjectMapper();

  @Reference
  JobManager jobManager;

  @Reference
  EventMetadataRegistrationService eventMetadataStatusSupplier;

  @Reference
  EventProviderRegistrationService eventProviderRegistrationService;

  private EventMetadata configuredEventMetadata;

  @Activate
  protected void activate(EventMetadataConfig eventMetadataConfig) {
    configuredEventMetadata = EventMetadata.builder()
        .description(eventMetadataConfig.aio_event_description())
        .label(eventMetadataConfig.aio_event_label())
        .eventCode(eventMetadataConfig.aio_event_code())
        .build();
  }

  @Override
  public EventMetadata getConfiguredEventMetadata() {
    return configuredEventMetadata;
  }

}
