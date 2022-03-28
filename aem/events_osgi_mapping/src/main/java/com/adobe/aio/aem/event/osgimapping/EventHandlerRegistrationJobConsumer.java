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
package com.adobe.aio.aem.event.osgimapping;

import com.adobe.aio.aem.event.management.EventMetadataRegistrationJobConsumer;
import com.adobe.aio.aem.event.management.EventProviderConfigSupplier;
import com.adobe.aio.aem.event.osgimapping.eventhandler.AdobeIOEventHandlerFactory;
import com.adobe.aio.aem.event.osgimapping.eventhandler.AdobeIoEventHandler;
import com.adobe.aio.aem.event.osgimapping.eventhandler.OsgiEventMapping;
import com.adobe.aio.aem.util.ResourceResolverWrapperFactory;
import com.adobe.aio.aem.workspace.WorkspaceSupplier;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Map;
import org.apache.sling.event.jobs.Job;
import org.apache.sling.event.jobs.JobManager;
import org.apache.sling.event.jobs.consumer.JobConsumer;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(service = JobConsumer.class, immediate = true, property = {
    JobConsumer.PROPERTY_TOPICS + "="
        + EventHandlerRegistrationJobConsumer.AIO_CONFIG_OSGI_EVENT_METADA_JOB_TOPIC,
    "label = Adobe I/O Events Handler Registration Job Consumer",
    "description = Adobe I/O Events Handler Registration Job Consumer"
})
public class EventHandlerRegistrationJobConsumer implements JobConsumer {

  public static final String AIO_CONFIG_OSGI_EVENT_METADA_JOB_TOPIC = "aio/config/osgi_event_metadata";
  public static final String AIO_EVENT_CODE_PROPERTY = EventMetadataRegistrationJobConsumer.AIO_EVENT_CODE_PROPERTY;
  public static final String AIO_OSGI_EVENT_MAPPING_PROPERTY = "osgi_event_mapping";
  public static final String AIO_ERROR_PROPERTY = "error";

  private final Logger log = LoggerFactory.getLogger(this.getClass());

  @Reference
  OsgiEventMappingStatusSupplier osgiEventMappingStatusSupplier;

  @Reference
  private JobManager jobManager;

  @Reference
  private EventProviderConfigSupplier eventProviderConfigSupplier;

  @Reference
  private WorkspaceSupplier workspaceSupplier;

  @Reference
  private ResourceResolverWrapperFactory resourceResolverWrapperFactory;

  private BundleContext bundleContext;

  @Activate
  protected void activate(BundleContext context, Map<String, Object> config) {
    log.info("activating");
    this.bundleContext = context;
  }

  @Override
  public JobResult process(final Job job) {
    if (job.getProperty(AIO_EVENT_CODE_PROPERTY) != null) {
      String eventCode = (String) job.getProperty(AIO_EVENT_CODE_PROPERTY);

      if (job.getProperty(AIO_OSGI_EVENT_MAPPING_PROPERTY) != null) {
        try {
          OsgiEventMapping osgiEventMapping = new ObjectMapper().readValue
              ((String) job.getProperty(AIO_OSGI_EVENT_MAPPING_PROPERTY), OsgiEventMapping.class);
          this.registerEventHandler(osgiEventMapping);
          osgiEventMappingStatusSupplier.addStatus(eventCode,
              new OsgiEventMappingStatus(osgiEventMapping, null));
          return JobResult.OK;
        } catch (Exception e) {
          log.error("Adobe I/O Events Handler Registration Job Consumer `{}`"
              + " processing failed: `{}", job, e.getMessage(), e);
          osgiEventMappingStatusSupplier.addStatus(eventCode, new OsgiEventMappingStatus(null, e));
          return JobResult.CANCEL;
        }
      } else {
        log.error("Adobe I/O Events Handler Registration Job Consumer `{}` is missing a `{}", job,
            AIO_OSGI_EVENT_MAPPING_PROPERTY);
        return JobResult.CANCEL;
      }
    } else {
      log.error("Adobe I/O Events Handler Registration Job Consumer `{}` is missing a `{}", job,
          AIO_EVENT_CODE_PROPERTY);
      return JobResult.CANCEL;
    }
  }

  public AdobeIoEventHandler getEventHanlder(OsgiEventMapping osgiEventMapping) {
    return AdobeIOEventHandlerFactory.getEventHandler(
        jobManager, eventProviderConfigSupplier.getRootUrl(),
        workspaceSupplier.getWorkspace().getImsOrgId(),
        osgiEventMapping,
        resourceResolverWrapperFactory.getWrapper());
  }

  public void registerEventHandler(OsgiEventMapping osgiEventMapping) {
    AdobeIoEventHandler eventHandler = getEventHanlder(osgiEventMapping);
    Dictionary props = new Hashtable();
    String[] eventTopics = {osgiEventMapping.getOsgiTopic()};
    props.put(EventConstants.EVENT_TOPIC, eventTopics);
    props.put("aio.event.code",osgiEventMapping.getEventCode());
    props.put("aio.event.handler",osgiEventMapping.getEventHandlerType());
    props.put("aio.event.path.filter",osgiEventMapping.getPathFilter());
    props.put("aio.event.xdm.type",osgiEventMapping.getXdmEventType());
    String osgiFilter = osgiEventMapping.getOsgiFilter();
    if (null != osgiFilter && !osgiFilter.isEmpty()) {
      props.put(EventConstants.EVENT_FILTER, osgiFilter);
    }
    ServiceRegistration<EventHandler> serviceRegistration = bundleContext
        .registerService(EventHandler.class.getName(), eventHandler, props);
    log.info("Registered a new Adobe I/O Events' OSGI Event Handler ({}) "
            + "with Adobe I/O Event Metadata: {}", eventHandler.getClass().getName(),
        osgiEventMapping);
  }

}