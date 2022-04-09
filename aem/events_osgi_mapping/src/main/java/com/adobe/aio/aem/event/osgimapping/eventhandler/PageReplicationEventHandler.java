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
import com.adobe.xdm.content.Page;
import java.net.URL;
import java.util.Date;
import java.util.List;
import org.apache.commons.lang3.tuple.Triple;
import org.apache.sling.event.jobs.JobManager;
import org.osgi.service.event.Event;


public class PageReplicationEventHandler extends ReplicationEventHandler<Page> {

  public PageReplicationEventHandler(JobManager jobManager, URL rootUrl, String imsOrgId,
      OsgiEventMapping osgiEventMapping,
      ResourceResolverWrapperFactory resourceResolverWrapperFactory) {
    super(jobManager, rootUrl, imsOrgId,
        osgiEventMapping, resourceResolverWrapperFactory);
  }

  @Override
  public List<Triple<Page, String, Date>> getXdmObjectActorDateTriples(Event event,
      ResourceResolverWrapper resourceResolverWrapper) {
    return this.getXdmObjectActorDateTriples(event, XdmUtil::getPage, XdmUtil::getDeletedPage,
        resourceResolverWrapper);
  }

}