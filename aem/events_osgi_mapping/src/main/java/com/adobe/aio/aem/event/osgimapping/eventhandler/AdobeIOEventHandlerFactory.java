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
import com.adobe.aio.aem.util.ResourceResolverWrapper;
import com.adobe.aio.exception.AIOException;
import java.net.URL;
import org.apache.sling.event.jobs.JobManager;

public class AdobeIOEventHandlerFactory {

  private AdobeIOEventHandlerFactory() {
  }

  public static AdobeIoEventHandler getEventHandler(
      JobManager jobManager,
      URL rootUrl,
      String imsOrgId,
      OsgiEventMapping osgiEventMapping,
      ResourceResolverWrapper resourceResolverWrapper) {
    /*
     * CloudManager won't let us use dynamic class loading:
     * hence all these ifs and elses below
     */
    String eventHandlerClass = osgiEventMapping.getEventHandlerType();
    if (eventHandlerClass.equals(OsgiEventMappingConfig.OSGI_EVENT_HANDLER_CLASS_NAME_DEFAULT)) {
      return new OsgiEventHandler(jobManager, rootUrl,
          imsOrgId,
          osgiEventMapping,
          resourceResolverWrapper
      );
    } else if (eventHandlerClass.equals(PageReplicationEventHandler.class.getCanonicalName())) {
      return new PageReplicationEventHandler(jobManager, rootUrl,
          imsOrgId,
          osgiEventMapping,
          resourceResolverWrapper
      );
    } else if (eventHandlerClass.equals(DamEventHandler.class.getCanonicalName())) {
      return new DamEventHandler(jobManager, rootUrl,
          imsOrgId,
          osgiEventMapping,
          resourceResolverWrapper
      );
    } else if (eventHandlerClass.equals(AssetReplicationEventHandler.class.getCanonicalName())) {
      return new AssetReplicationEventHandler(jobManager, rootUrl,
          imsOrgId,
          osgiEventMapping,
          resourceResolverWrapper
      );
    } else if (eventHandlerClass.equals(ResourceEventHandler.class.getCanonicalName())) {
      return new ResourceEventHandler(jobManager, rootUrl,
          imsOrgId,
          osgiEventMapping,
          resourceResolverWrapper
      );
    } else if (eventHandlerClass.equals(PageCreatedEventHandler.class.getCanonicalName())) {
      return new PageCreatedEventHandler(jobManager, rootUrl,
          imsOrgId,
          osgiEventMapping,
          resourceResolverWrapper
      );
    } else if (eventHandlerClass.equals(ResourceEventHandler.class.getCanonicalName())) {
      return new ResourceEventHandler(jobManager, rootUrl,
          imsOrgId,
          osgiEventMapping,
          resourceResolverWrapper
      );
    } else if (eventHandlerClass.equals(PageCreatedEventHandler.class.getCanonicalName())) {
      return new PageCreatedEventHandler(jobManager, rootUrl,
          imsOrgId,
          osgiEventMapping,
          resourceResolverWrapper
      );
    } else if (eventHandlerClass.equals(PageDeletedEventHandler.class.getCanonicalName())) {
      return new PageDeletedEventHandler(jobManager, rootUrl,
          imsOrgId,
          osgiEventMapping,
          resourceResolverWrapper
      );
    } else if (eventHandlerClass.equals(PageVersionedEventHandler.class.getCanonicalName())) {
      return new PageVersionedEventHandler(jobManager, rootUrl,
          imsOrgId,
          osgiEventMapping,
          resourceResolverWrapper
      );
    } else if (eventHandlerClass.equals(PageMovedEventHandler.class.getCanonicalName())) {
      return new PageMovedEventHandler(jobManager, rootUrl,
          imsOrgId,
          osgiEventMapping,
          resourceResolverWrapper
      );
    } else if (eventHandlerClass.equals(PageUpdatedEventHandler.class.getCanonicalName())) {
      return new PageUpdatedEventHandler(jobManager, rootUrl,
          imsOrgId,
          osgiEventMapping,
          resourceResolverWrapper
      );
    } else {
      throw new AIOException(
          "Unreferenced/unimplemented OSGI event Handler value: " + eventHandlerClass);
    }
  }


}
