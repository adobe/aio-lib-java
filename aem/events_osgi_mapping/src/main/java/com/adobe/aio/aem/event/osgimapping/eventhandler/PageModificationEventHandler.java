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
import com.adobe.aio.aem.event.xdm.aem.XdmUtil;
import com.adobe.aio.aem.util.ResourceResolverWrapper;
import com.adobe.aio.exception.AIOException;
import com.adobe.xdm.content.Page;
import com.day.cq.wcm.api.PageEvent;
import com.day.cq.wcm.api.PageModification;
import com.day.cq.wcm.api.PageModification.ModificationType;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import org.apache.commons.lang3.tuple.Triple;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.event.jobs.JobManager;
import org.osgi.service.event.Event;

public class PageModificationEventHandler extends
    AdobeIoEventHandler<com.adobe.aio.aem.event.xdm.aem.PageModification> {

  private final ModificationType modificationType;

  public PageModificationEventHandler(ModificationType modificationType,
      JobManager jobManager, URL rootUrl, String imsOrgId,
      OsgiEventMappingConfig osgiEventMappingConfig,
      ResourceResolverWrapper resourceResolverWrapper) {
    super(jobManager, rootUrl, imsOrgId,
        osgiEventMappingConfig, resourceResolverWrapper);
    this.modificationType = modificationType;
  }

  @Override
  public List<Triple<com.adobe.aio.aem.event.xdm.aem.PageModification, String, Date>> getXdmObjectActorDateTriples(
      Event event, ResourceResolverWrapper resourceResolverWrapper) {
    List<Triple<com.adobe.aio.aem.event.xdm.aem.PageModification, String, Date>> triples = new ArrayList<>();
    Iterator<PageModification> pageModificationIterator = getPageModifications(event);
    while (pageModificationIterator.hasNext()) {
      PageModification pageModification = pageModificationIterator.next();
      if (pageModification.getType().equals(modificationType)) {
        Optional<com.adobe.aio.aem.event.xdm.aem.PageModification> xdmPageModification =
            com.adobe.aio.aem.event.xdm.aem.PageModification
                .of(getXdmPage(pageModification, resourceResolverWrapper).orElse(null),
                    pageModification);
        if (xdmPageModification.isPresent()) {
          triples
              .add(Triple.of(xdmPageModification.get(), pageModification.getUserId(),
                  pageModification.getModificationDate()));
        } else {
          logger
              .debug("Ignoring event `{}` as its Page Modification is not of interest: `{}`.",
                  event, pageModification);
        }
      } else {
        logger.debug("Ignoring event `{}` as its Page Modification is not of interest: `{}`.",
            event, pageModification);
      }
    }
    return triples;
  }

  private Optional<Page> getXdmPage(PageModification pageModification,
      ResourceResolverWrapper resourceResolverWrapper) {
    if (pageModification.getType().equals(ModificationType.MOVED)) {
      // if MOVED we'll get the page info at its new location
      Resource res = resourceResolverWrapper.getResolver()
          .getResource(pageModification.getDestination());
      return getXdmObjectFromResource(res, XdmUtil::getPage);
    } else if (pageModification.getType().equals(ModificationType.DELETED)) {
      // if DELETED we'll construct the page just from its former path
      return XdmUtil.getDeletedPage(pageModification.getPath(), this::isPathOfInterest);
    } else {
      Resource res = resourceResolverWrapper.getResolver().getResource(pageModification.getPath());
      return getXdmObjectFromResource(res, XdmUtil::getPage);
    }
  }

  private Iterator<PageModification> getPageModifications(Event event) {
    PageEvent pageEvent = PageEvent.fromEvent(event);
    if (pageEvent != null) {
      if (pageEvent.isLocal()) {
        return pageEvent.getModifications();
      } else {
        logger.debug("Ignoring non-local/cluster PageModification event");
        return Collections.emptyIterator();
      }
    } else {
      throw new AIOException("This Event " +
          event + "should be a PageEvent but is not... wrong topic ?");
    }
  }

}