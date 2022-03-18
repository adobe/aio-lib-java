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

import com.adobe.aio.aem.event.osgimapping.OsgiEventMappingStatus;
import com.adobe.aio.aem.event.osgimapping.OsgiEventMappingStatusSupplier;
import com.adobe.aio.aem.event.osgimapping.OsgiEventMappingSupplier;
import com.adobe.aio.aem.status.Status;
import com.adobe.aio.event.management.model.EventMetadata;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(service = OsgiEventMappingStatusSupplier.class,
    property = {
        "label = Adobe I/O Events' Osgi Event Mapping Status Supplier Service",
        "description = Adobe I/O Events'  Osgi Event Mapping Status Supplier Service"
    }
)
public class OsgiEventMappingStatusSupplierImpl implements OsgiEventMappingStatusSupplier {

  private final Logger log = LoggerFactory.getLogger(getClass());
  private final Map<String, OsgiEventMappingStatus> osgiEventMappingStatusByEventCode = new ConcurrentHashMap<>();
  @Reference(
      service = OsgiEventMappingSupplier.class,
      policy = ReferencePolicy.DYNAMIC,
      cardinality = ReferenceCardinality.MULTIPLE,
      bind = "bindEventMetadata",
      unbind = "unbindEventMetadata")
  private volatile List<OsgiEventMappingSupplier> eventMetadataSuppliers;

  protected synchronized void bindEventMetadata(
      final OsgiEventMappingSupplier eventMetadataSupplier) {
    EventMetadata eventMetadata = eventMetadataSupplier.getConfiguredEventMetadata();
    osgiEventMappingStatusByEventCode.put(eventMetadata.getEventCode(),
        new OsgiEventMappingStatus(eventMetadataSupplier));
  }

  protected void unbindEventMetadata(
      final OsgiEventMappingSupplier eventMappingSupplier) {
    log.debug("won't delete I/O event metadata when unbinding/shutting-down");
  }

  @Override
  public Status getStatus() {
    Map<String, Object> details = new HashMap<>(1);
    try {
      if (osgiEventMappingStatusByEventCode.isEmpty()) {
        return new Status(Status.INVALID_CONFIG, null, "Missing event metadata configuration");
      } else {
        details.putAll(osgiEventMappingStatusByEventCode);
        boolean isUp = osgiEventMappingStatusByEventCode.values().stream()
            .allMatch(OsgiEventMappingStatus::isUp);
        return new Status(isUp, details);
      }
    } catch (Exception e) {
      return new Status(Status.DOWN, details, e);
    }
  }

}




