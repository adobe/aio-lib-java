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
import com.adobe.aio.aem.event.management.EventMetadataRegistrationService;
import com.adobe.aio.aem.event.management.EventMetadataSupplier;
import com.adobe.aio.aem.event.management.EventProviderRegistrationService;
import com.adobe.aio.aem.status.Status;
import com.adobe.aio.event.management.model.EventMetadata;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(immediate = true, service = EventMetadataRegistrationService.class,
    property = {
        "label = Adobe I/O Events' Event Metadata Registration Service",
        "description = Adobe I/O Events' Event Metadata Registration Service"
    })
public class EventMetadataRegistrationServiceImpl implements EventMetadataRegistrationService {

  private final Logger log = LoggerFactory.getLogger(getClass());
  private final Map<String, EventMetadataStatus> eventMetadataStatusByEventCode = new ConcurrentHashMap<>();

  /**
   * Ideally the annotation should be placed on the bind Method
   * and we could make this List non-volatile
   * and the bind and unbind method non synchronized.
   */
  @Reference(
      service = EventMetadataSupplier.class,
      policy = ReferencePolicy.DYNAMIC,
      cardinality = ReferenceCardinality.MULTIPLE,
      bind = "bindEventMetadataSupplier",
      unbind = "unbindEventMetadataSupplier")
  private volatile List<EventMetadataSupplier> eventMetadataSupplierList;

  @Reference
  EventProviderRegistrationService eventProviderRegistrationService;

  private BundleContext bundleContext;

  protected synchronized void bindEventMetadataSupplier(
      final EventMetadataSupplier eventMetadataSupplier) {
    EventMetadata configuredEventMetadata = eventMetadataSupplier.getConfiguredEventMetadata();
    registerEventMetadata(configuredEventMetadata);
    log.debug("binding Adobe I/O Events' Metadata : {} ", configuredEventMetadata);
  }

  @Override
  public void registerEventMetadata(EventMetadata configuredEventMetadata) {
    // making this async and with delay in order to avoid workspace config resolution issue
    // it looks like bind is called before activation
    Executors.newSingleThreadScheduledExecutor().schedule(
        () -> this.registerEventMetadataSync(configuredEventMetadata),
        (ThreadLocalRandom.current().nextInt(3000, 4000)),
        TimeUnit.MILLISECONDS);
  }

  private void registerEventMetadataSync(EventMetadata configuredEventMetadata) {
    EventMetadata registeredEventMetadata = null;
    try {
      registeredEventMetadata =
          eventProviderRegistrationService.registerEventMetadata(configuredEventMetadata);
      this.addStatus(configuredEventMetadata.getEventCode(),
          new EventMetadataStatus(configuredEventMetadata, registeredEventMetadata));
      log.info("Adobe I/O Events Metadata Registration completed:"
          + " registered event metadata `{}`", registeredEventMetadata);
    } catch (Exception e) {
      this.addStatus(configuredEventMetadata.getEventCode(),
          new EventMetadataStatus(configuredEventMetadata, registeredEventMetadata, e));
      log.error("Adobe I/O Events Metadata Registration processing failed: `{}",
          e.getMessage(), e);
    }
  }

  protected synchronized void unbindEventMetadataSupplier(
      final EventMetadataSupplier eventMetadataSupplier) {
    // we don't want to delete registration when instances/pod are going down
    log.debug("unbinded Adobe I/O Events' Event Metadata : {} ",
        eventMetadataSupplier.getConfiguredEventMetadata());
  }

  private void addStatus(String eventCode, EventMetadataStatus eventMetadataStatus) {
    eventMetadataStatusByEventCode.put(eventCode, eventMetadataStatus);
  }

  @Override
  public Status getStatus() {
    Map<String, Object> details = new HashMap<>(1);
    try {
      if (eventMetadataStatusByEventCode.isEmpty()) {
        return new Status(Status.DOWN, null, "No event metadata configuration found (yet?)");
      } else {
        details.put("size", "" + eventMetadataStatusByEventCode.size());
        details.put("event_metadata", eventMetadataStatusByEventCode);
        boolean isUp = eventMetadataStatusByEventCode.values().stream()
            .allMatch(EventMetadataStatus::isUp);
        return new Status(isUp, details);
      }
    } catch (Exception e) {
      return new Status(Status.DOWN, details, e);
    }
  }

}
