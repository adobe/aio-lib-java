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

import com.adobe.aio.aem.event.management.EventMetadataStatus;
import com.adobe.aio.aem.event.management.EventMetadataStatusSupplier;
import com.adobe.aio.aem.event.management.EventMetadataSupplier;
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

@Component(property = {
    "label = Adobe I/O Events' Event Metadata Status Supplier Service",
    "description = Adobe I/O Events' Event Metadata Status Supplier Service"
}
)
public class EventMetadataStatusSupplierImpl implements EventMetadataStatusSupplier {

  private final Logger log = LoggerFactory.getLogger(getClass());
  private final Map<String, EventMetadataStatus> eventMetadataStatusByEventCode = new ConcurrentHashMap<>();
  @Reference(
      service = EventMetadataSupplier.class,
      policy = ReferencePolicy.DYNAMIC,
      cardinality = ReferenceCardinality.MULTIPLE,
      bind = "bindEventMetadata",
      unbind = "unbindEventMetadata")
  private volatile List<EventMetadataSupplier> eventMetadataSuppliers;

  protected synchronized void bindEventMetadata(
      final EventMetadataSupplier eventMetadataSupplier) {
    EventMetadata eventMetadata = eventMetadataSupplier.getConfiguredEventMetadata();
    eventMetadataStatusByEventCode.put(eventMetadata.getEventCode(),
        new EventMetadataStatus(eventMetadataSupplier));
  }

  protected void unbindEventMetadata(
      final EventMetadataSupplier eventMetadataBuilder) {
    log.debug("won't delete I/O event metadata when unbinding/shutting-down");
  }

  @Override
  public Status getStatus() {
    Map<String, Object> details = new HashMap<>(1);
    try {
      if (eventMetadataStatusByEventCode.isEmpty()) {
        return new Status(Status.INVALID_CONFIG, null, "Missing event metadata configuration");
      } else {
        details.putAll(eventMetadataStatusByEventCode);
        boolean isUp = eventMetadataStatusByEventCode.values().stream()
            .allMatch(EventMetadataStatus::isUp);
        return new Status(isUp, details);
      }
    } catch (Exception e) {
      return new Status(Status.DOWN, details, e);
    }
  }

}




