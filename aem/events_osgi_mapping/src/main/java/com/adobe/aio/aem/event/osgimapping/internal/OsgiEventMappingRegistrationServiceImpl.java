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

import com.adobe.aio.aem.event.management.EventMetadataRegistrationService;
import com.adobe.aio.aem.event.management.EventProviderConfigSupplier;
import com.adobe.aio.aem.event.management.EventProviderRegistrationService;
import com.adobe.aio.aem.event.osgimapping.OsgiEventMappingStatus;
import com.adobe.aio.aem.event.osgimapping.OsgiEventMappingRegistrationService;
import com.adobe.aio.aem.event.osgimapping.OsgiEventMappingSupplier;
import com.adobe.aio.aem.event.osgimapping.eventhandler.AdobeIOEventHandlerFactory;
import com.adobe.aio.aem.event.osgimapping.eventhandler.AdobeIoEventHandler;
import com.adobe.aio.aem.event.osgimapping.eventhandler.OsgiEventMapping;
import com.adobe.aio.aem.status.Status;
import com.adobe.aio.aem.util.ResourceResolverWrapperFactory;
import com.adobe.aio.aem.workspace.WorkspaceSupplier;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import org.apache.sling.event.jobs.JobManager;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(service = OsgiEventMappingRegistrationService.class,
    property = {
        "label = Adobe I/O Events' Osgi Event Mapping Status Supplier Service",
        "description = Adobe I/O Events'  Osgi Event Mapping Status Supplier Service"
    }
)
public class OsgiEventMappingRegistrationServiceImpl implements OsgiEventMappingRegistrationService {

  private final Logger log = LoggerFactory.getLogger(getClass());
  private final Map<String, OsgiEventMappingStatus> osgiEventMappingStatusByEventCode = new ConcurrentHashMap<>();

  /**
   * In theory the annotation could be placed on the bind Method: we could make this List
   * non-volatile and the bind and unbind method non synchronized.
   */
  @Reference(
      service = OsgiEventMappingSupplier.class,
      policy = ReferencePolicy.DYNAMIC,
      cardinality = ReferenceCardinality.MULTIPLE,
      bind = "bindEventMetadataSupplier",
      unbind = "unbindEventMetadataSupplier")
  private volatile List<OsgiEventMappingSupplier> osgiEventMappingSuppliers;

  @Reference
  private EventMetadataRegistrationService eventMetadataStatusSupplier;

  @Reference
  private WorkspaceSupplier workspaceSupplier;

  @Reference
  private EventProviderRegistrationService eventProviderRegistrationService;

  @Reference
  private EventProviderConfigSupplier eventProviderConfigSupplier;

  @Reference
  private ResourceResolverWrapperFactory resourceResolverWrapperFactory;

  @Reference
  private JobManager jobManager;

  private BundleContext bundleContext;

  @Activate
  protected void activate(BundleContext context, Map<String, Object> config) {
    log.info("activating");
    this.bundleContext = context;
  }

  protected synchronized void bindEventMetadataSupplier(
      final OsgiEventMappingSupplier osgiEventMappingSupplier) {
    eventMetadataStatusSupplier.registerEventMetadata(
            osgiEventMappingSupplier.getConfiguredEventMetadata());
    // making this async and with delay in order to avoid workspace config resolution issue
    // it looks like bind is called before activation
    Executors.newSingleThreadScheduledExecutor().schedule(
        () -> this.registerSlingEventHandler(osgiEventMappingSupplier.getOsgiEventMapping()),
        (ThreadLocalRandom.current().nextInt(3000, 4000)),
        TimeUnit.MILLISECONDS);

    log.debug("binding Adobe I/O Events' Osgi Event Mapping : {} ",
        osgiEventMappingSupplier.getConfiguredEventMetadata());
  }

  protected synchronized void unbindEventMetadataSupplier(
      final OsgiEventMappingSupplier osgiEventMappingSupplier) {
    // we don't want to delete registration when instances/pod are going down
    log.debug("unbinding Adobe I/O Events' Osgi Event Mapping : {} ",
        osgiEventMappingSupplier.getConfiguredEventMetadata());
  }

  private void registerSlingEventHandler(OsgiEventMapping osgiEventMapping) {
    try {
      // we don't want to register sling event handlers if the config is invalid
      workspaceSupplier.getWorkspace().validateAll();
      // we don't want to register sling event handlers if the provider can't be registered
      eventProviderRegistrationService.getRegisteredProvider();

      AdobeIoEventHandler eventHandler = getEventHandler(osgiEventMapping);
      Dictionary props = new Hashtable();
      String[] eventTopics = {osgiEventMapping.getOsgiTopic()};
      props.put(EventConstants.EVENT_TOPIC, eventTopics);
      props.put("aio.event.code", osgiEventMapping.getEventCode());
      props.put("aio.event.handler", osgiEventMapping.getEventHandlerType());
      props.put("aio.event.path.filter", osgiEventMapping.getPathFilter());
      props.put("aio.event.xdm.type", osgiEventMapping.getXdmEventType());
      String osgiFilter = osgiEventMapping.getOsgiFilter();
      if (null != osgiFilter && !osgiFilter.isEmpty()) {
        props.put(EventConstants.EVENT_FILTER, osgiFilter);
      }
      ServiceRegistration<EventHandler> serviceRegistration = bundleContext
          .registerService(EventHandler.class.getName(), eventHandler, props);

      log.info("Registered a new Adobe I/O Events' OSGI Event Handler ({}) "
              + "with Adobe I/O Event Metadata: {}", eventHandler.getClass().getName(),
          osgiEventMapping);
      this.addStatus(osgiEventMapping.getEventCode(),
          new OsgiEventMappingStatus(osgiEventMapping, null));
    } catch (Exception e) {
      log.error("Adobe I/O Events' OSGI Handler Registration failed: `{}", e.getMessage(), e);
      this.addStatus(osgiEventMapping.getEventCode(), new OsgiEventMappingStatus(null, e));
    }
  }

  private AdobeIoEventHandler getEventHandler(OsgiEventMapping osgiEventMapping) {
    return AdobeIOEventHandlerFactory.getEventHandler(
        jobManager, eventProviderConfigSupplier.getRootUrl(),
        workspaceSupplier.getWorkspace().getImsOrgId(),
        osgiEventMapping,
        resourceResolverWrapperFactory.getWrapper());
  }

  private void addStatus(String eventCode, OsgiEventMappingStatus eventMetadataStatus) {
    osgiEventMappingStatusByEventCode.put(eventCode, eventMetadataStatus);
  }

  @Override
  public Status getStatus() {
    Map<String, Object> details = new HashMap<>(1);
    try {
      if (osgiEventMappingStatusByEventCode.isEmpty()) {
        return new Status(Status.DOWN, null, "No Osgi Event Mapping configuration found (yet?)");
      } else {
        details.put("size", "" + osgiEventMappingStatusByEventCode.size());
        details.put("osgi_event_mappings", osgiEventMappingStatusByEventCode);
        boolean isUp = osgiEventMappingStatusByEventCode.values().stream()
            .allMatch(OsgiEventMappingStatus::isUp);
        return new Status(isUp, details);
      }
    } catch (Exception e) {
      return new Status(Status.DOWN, details, e);
    }
  }

}