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

import com.adobe.aio.aem.event.xdm.aem.XdmUtil;
import com.adobe.aio.aem.util.ResourceResolverWrapper;
import com.adobe.aio.aem.util.ResourceResolverWrapperFactory;
import com.adobe.xdm.XdmObject;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import org.apache.commons.lang3.tuple.Triple;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.event.jobs.JobManager;
import org.osgi.service.event.Event;

public abstract class ReplicationEventHandler<T extends XdmObject> extends AdobeIoEventHandler<T> {

  private static final String PATHS_PROPERTY_KEY = "paths";
  private static final String USER_ID_PROPERTY_KEY = "userId";
  private static final String DATE_PROPERTY_KEY = "modificationDate";
  private static final String TYPE = "type";
  private static final String DELETE_TYPE = "DELETE";

  public ReplicationEventHandler(JobManager jobManager, URL rootUrl, String imsOrgId,
      OsgiEventMapping osgiEventMapping,
      ResourceResolverWrapperFactory resourceResolverWrapperFactory) {
    super(jobManager, rootUrl, imsOrgId,
        osgiEventMapping, resourceResolverWrapperFactory);
  }

  public List<Triple<T, String, Date>> getXdmObjectActorDateTriples(Event event,
      Function<Resource, Optional<T>> getXdmObjectFromResource,
      BiFunction<String, Predicate<String>, Optional<T>> getDeletedXdmObjectFromPath,
      ResourceResolverWrapper resourceResolverWrapper) {
    List<String> paths = EventUtil
        .getEventPropertyAsListOfString(event, PATHS_PROPERTY_KEY);
    if (paths.isEmpty()) {
      logger.error("The caught event {} had invalid `{}` property. Ignoring this Event", event,
          PATHS_PROPERTY_KEY);
      return new ArrayList<>();
    } else {
      List<Triple<T, String, Date>> result = new ArrayList<>();
      for (String path : paths) {
        Resource resource = resourceResolverWrapper.getResolver().getResource(path);
        Optional<T> xdmObject = getXdmObjectFromResource(resource, getXdmObjectFromResource);
        if (!xdmObject.isPresent() && EventUtil.isEventProperty(event, TYPE, DELETE_TYPE)) {
          xdmObject = getDeletedXdmObjectFromPath.apply(path,
              this::isPathOfInterest);
        }
        if (xdmObject.isPresent()) {
          result.add(Triple.of(xdmObject.get(),
              EventUtil.getEventPropertyAsString(event, USER_ID_PROPERTY_KEY)
                  .orElse(XdmUtil.NON_AVAILABLE),
              EventUtil.getEventPropertyAsDate(event, DATE_PROPERTY_KEY)
                  .orElse(new Date())));
        } else {
          logger.debug("Ignoring this event `{}` for path `{}, as it is not of interest.", event,
              path);
        }
      }
      return result;
    }
  }

}