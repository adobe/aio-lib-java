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
package com.adobe.aio.aem.event.osgimapping.internal;

import com.adobe.aio.aem.event.management.EventMetadataRegistrationJobConsumer;
import com.adobe.aio.aem.event.osgimapping.EventHandlerRegistrationJobConsumer;
import com.adobe.aio.aem.event.osgimapping.OsgiEventMappingSupplier;
import com.adobe.aio.aem.event.osgimapping.eventhandler.OsgiEventMapping;
import com.adobe.aio.aem.event.osgimapping.ocd.OsgiEventMappingConfig;
import com.adobe.aio.event.management.model.EventMetadata;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.Map;
import org.apache.sling.event.jobs.JobManager;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(immediate = true, service = OsgiEventMappingSupplier.class, property = {
    "label = Adobe I/O Events' Osgi Event Mapping Supplier Service",
    "description = Adobe I/O Events' Osgi Event Mapping Supplier Service"
}
)
@Designate(ocd = OsgiEventMappingConfig.class, factory = true)
public class OsgiEventMappingSupplierImpl implements OsgiEventMappingSupplier {

  private final Logger log = LoggerFactory.getLogger(getClass());
  private final ObjectMapper objectMapper = new ObjectMapper();

  @Reference
  JobManager jobManager;

  @Activate
  protected void activate(OsgiEventMappingConfig eventMetadataConfig) {
    Map<String, Object> jobProperties = new HashMap();
    jobProperties.put(EventMetadataRegistrationJobConsumer.AIO_EVENT_CODE_PROPERTY,
        eventMetadataConfig.aio_event_code());
    try {
      EventMetadata configuredEventMetadata = EventMetadata.builder()
          .description(eventMetadataConfig.aio_event_description())
          .label(eventMetadataConfig.aio_event_label())
          .eventCode(eventMetadataConfig.aio_event_code())
          .build();
      jobProperties.put(EventHandlerRegistrationJobConsumer.AIO_OSGI_EVENT_MAPPING_PROPERTY,
          objectMapper.writeValueAsString(new OsgiEventMapping(eventMetadataConfig)));
      jobProperties.put(EventMetadataRegistrationJobConsumer.AIO_EVENT_METADATA_PROPERTY,
          objectMapper.writeValueAsString(configuredEventMetadata));
      jobManager.addJob(EventMetadataRegistrationJobConsumer.AIO_CONFIG_EVENT_METADA_JOB_TOPIC,
          jobProperties);
      jobManager.addJob(EventHandlerRegistrationJobConsumer.AIO_CONFIG_OSGI_EVENT_METADA_JOB_TOPIC,
          jobProperties);
      log.debug("Adobe I/O Events Metadata Config Job {} added.", jobProperties);
    } catch (Exception e) {
      log.error("Adobe I/O Events' Event Metadata Supplier Service Activation Error: {}",
          e.getMessage(), e);
      jobProperties.put(EventMetadataRegistrationJobConsumer.AIO_ERROR_PROPERTY,
          e.getClass().getSimpleName() + ":" + e.getMessage());
      jobManager.addJob(EventMetadataRegistrationJobConsumer.AIO_CONFIG_EVENT_METADA_JOB_TOPIC,
          jobProperties);
    } finally {
      log.info(
          "Adobe I/O Events' Event Metadata Supplier Service Activation Complete with config : {}",
          eventMetadataConfig);
    }
  }

}
