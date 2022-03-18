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

import com.adobe.aio.aem.event.management.EventProviderConfigSupplier;
import com.adobe.aio.aem.event.management.EventProviderRegistrationService;
import com.adobe.aio.aem.event.osgimapping.OsgiEventMappingSupplier;
import com.adobe.aio.aem.event.osgimapping.eventhandler.AdobeIOEventHandlerFactory;
import com.adobe.aio.aem.event.osgimapping.eventhandler.AdobeIoEventHandler;
import com.adobe.aio.aem.event.osgimapping.ocd.OsgiEventMappingConfig;
import com.adobe.aio.aem.util.ResourceResolverWrapperFactory;
import com.adobe.aio.aem.workspace.WorkspaceSupplier;
import com.adobe.aio.event.management.model.EventMetadata;
import org.apache.sling.event.jobs.JobManager;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(service = OsgiEventMappingSupplier.class, property = {
    "label = Adobe I/O Events' Osgi Event Mapping Supplier Service",
    "description = Adobe I/O Events' Osgi Event Mapping Supplier Service"
}
)
@Designate(ocd = OsgiEventMappingConfig.class, factory = true)
public class OsgiEventMappingSupplierImpl implements OsgiEventMappingSupplier {

  private final Logger log = LoggerFactory.getLogger(getClass());

  @Reference
  EventProviderRegistrationService eventProviderRegistrationService;

  @Reference
  private JobManager jobManager;

  @Reference
  private EventProviderConfigSupplier eventProviderConfigSupplier;

  @Reference
  private WorkspaceSupplier workspaceSupplier;

  @Reference
  private ResourceResolverWrapperFactory resourceResolverWrapperFactory;

  private EventMetadata configuredEventMetadata;
  private EventMetadata registeredEventMetadata;
  private AdobeIoEventHandler adobeIoEventHandler;
  private Throwable error;

  @Activate
  protected void activate(OsgiEventMappingConfig osgiEventMappingConfig) {
    try {
      configuredEventMetadata = EventMetadata.builder()
          .description(osgiEventMappingConfig.aio_event_description())
          .label(osgiEventMappingConfig.aio_event_label())
          .eventCode(osgiEventMappingConfig.aio_event_code())
          .build();
      registeredEventMetadata = eventProviderRegistrationService.registerEventMetadata(
          configuredEventMetadata);
      adobeIoEventHandler = AdobeIOEventHandlerFactory.getEventHandler(
          jobManager, eventProviderConfigSupplier.getRootUrl(),
          workspaceSupplier.getWorkspace().getImsOrgId(),
          osgiEventMappingConfig,
          resourceResolverWrapperFactory.getWrapper());
    } catch (Exception e) {
      log.error("Adobe I/O Events' Osgi Event Mapping Supplier Service Activation Error: {}",
          e.getMessage(), e);
      this.error = e;
    }
  }

  @Override
  public EventMetadata getRegisteredEventMetadata() {
    return this.registeredEventMetadata;
  }

  @Override
  public EventMetadata getConfiguredEventMetadata() {
    return this.configuredEventMetadata;
  }

  @Override
  public Throwable getError() {
    return null;
  }

  public AdobeIoEventHandler getOsgiEventHandler() {
    return adobeIoEventHandler;
  }

}
