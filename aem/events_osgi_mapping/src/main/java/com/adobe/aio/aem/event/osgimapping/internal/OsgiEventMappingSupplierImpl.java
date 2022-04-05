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

import com.adobe.aio.aem.event.osgimapping.OsgiEventMappingSupplier;
import com.adobe.aio.aem.event.osgimapping.eventhandler.OsgiEventMapping;
import com.adobe.aio.aem.event.osgimapping.ocd.OsgiEventMappingConfig;
import com.adobe.aio.event.management.model.EventMetadata;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.metatype.annotations.Designate;

@Component(immediate = true, service = OsgiEventMappingSupplier.class, property = {
    "label = Adobe I/O Events' Osgi Event Mapping Supplier Service",
    "description = Adobe I/O Events' Osgi Event Mapping Supplier Service"
}
)
@Designate(ocd = OsgiEventMappingConfig.class, factory = true)
public class OsgiEventMappingSupplierImpl implements OsgiEventMappingSupplier {

  private EventMetadata configuredEventMetadata;
  private OsgiEventMapping osgiEventMapping;

  @Activate
  protected void activate(OsgiEventMappingConfig eventMetadataConfig) {
    configuredEventMetadata = EventMetadata.builder()
        .description(eventMetadataConfig.aio_event_description())
        .label(eventMetadataConfig.aio_event_label())
        .eventCode(eventMetadataConfig.aio_event_code())
        .build();
    osgiEventMapping = new OsgiEventMapping(eventMetadataConfig);
  }

  @Override
  public EventMetadata getConfiguredEventMetadata() {
    return configuredEventMetadata;
  }

  @Override
  public OsgiEventMapping getOsgiEventMapping() {
    return osgiEventMapping;
  }

}
