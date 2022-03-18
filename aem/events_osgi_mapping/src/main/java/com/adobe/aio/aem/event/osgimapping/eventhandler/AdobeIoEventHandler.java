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
package com.adobe.aio.aem.event.osgimapping.eventhandler;

import com.adobe.aio.aem.event.osgimapping.ocd.OsgiEventMappingConfig;
import com.adobe.aio.aem.event.publish.EventPublishJobConsumer;
import com.adobe.aio.aem.event.xdm.aem.XdmUtil;
import com.adobe.aio.aem.util.ResourceResolverWrapper;
import com.adobe.xdm.XdmObject;
import com.adobe.xdm.common.XdmEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.apache.commons.lang3.tuple.Triple;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.event.jobs.JobManager;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public abstract class AdobeIoEventHandler<T extends XdmObject> implements EventHandler {

  protected final Logger logger = LoggerFactory.getLogger(this.getClass());
  private final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
  private final JobManager jobManager;
  private final URL rootUrl;
  private final String imsOrgId;
  private final OsgiEventMappingConfig osgiEventMappingConfig;
  private final ResourceResolverWrapper resourceResolverWrapper;
  protected AdobeIoEventHandler(JobManager jobManager, URL rootUrl, String imsOrgId,
      OsgiEventMappingConfig osgiEventMappingConfig,
      ResourceResolverWrapper resourceResolverWrapper) {
    this.jobManager = jobManager;
    this.rootUrl = rootUrl;
    this.imsOrgId = imsOrgId;
    this.osgiEventMappingConfig = osgiEventMappingConfig;
    this.resourceResolverWrapper = resourceResolverWrapper;
  }

  /**
   * convert an OSGI event into a matching XDM event object(s)
   *
   * @param event                   the osgi event
   * @param resourceResolverWrapper can be used to access a resourceResolver
   * @return an non-empty list of XDM objects
   */
  abstract List<Triple<T, String, Date>> getXdmObjectActorDateTriples(Event event,
      ResourceResolverWrapper resourceResolverWrapper);

  public void handleEvent(final Event event) {
    try (resourceResolverWrapper) {
      if (!EventUtil.isEventLocal(event)) {
        //This should not happen as the event is supposed to be filtered upstream using the `osgiFilter` configuration
        logger.info("This Event `{}` is not local: Adobe I/O will ignore it, "
            + "please consider using the `osgiFilter` configuration for better performance", event);
        return;
      }
      List<XdmEvent> xdmEvents = getXdmEvents(event, resourceResolverWrapper);
      if (xdmEvents != null && !xdmEvents.isEmpty()) {
        for (XdmEvent xdmEvent : xdmEvents) {
          Map<String, Object> jobProperties = new HashMap();
          jobProperties.put(EventPublishJobConsumer.AIO_JOB_EVENT_CODE_PROPERTY,
              osgiEventMappingConfig.aio_event_code());
          jobProperties
              .put(EventPublishJobConsumer.AIO_JOB_EVENT_PROPERTY,
                  OBJECT_MAPPER.writeValueAsString(xdmEvent));
          jobManager.addJob(EventPublishJobConsumer.AIO_JOB_TOPIC, jobProperties);
          logger.debug("Adobe I/O Event Job {} added.", jobProperties);
        }
      } else {
        logger.debug("No Adobe I/O Event match, ignoring this Event {}", event);
      }
    } catch (Exception e) {
      logger.debug("Error `{}`: while processing (event: `{})", e.getMessage(), event, e);
      logger.error("Adobe I/O Event Handler processing failed (event: `{})"
          + " due to `{}`", event, e.getMessage());
    }
  }

  List<XdmEvent> getXdmEvents(Event event, ResourceResolverWrapper resourceResolverWrapper) {
    return getXdmObjectActorDateTriples(event, resourceResolverWrapper).stream()
        .map(triple ->
            XdmUtil.getXdmEvent(
                triple.getLeft(),
                XdmUtil.getAemUser(triple.getMiddle()),
                triple.getRight(),
                imsOrgId,
                rootUrl.toString(),
                osgiEventMappingConfig.aio_xdm_event_type()))
        .collect(Collectors.toList());
  }

  public <V> Optional<V> getXdmObjectFromResource(Resource resource,
      Function<Resource, Optional<V>> converter) {
    return XdmUtil.getXdmObjectFromResource(resource, this::isPathOfInterest, converter);
  }

  protected boolean isPathOfInterest(String path) {
    String observedPath = osgiEventMappingConfig.osgi_jcr_path_filter();
    return (null == observedPath || observedPath.isEmpty() || path.startsWith(observedPath));
  }

}