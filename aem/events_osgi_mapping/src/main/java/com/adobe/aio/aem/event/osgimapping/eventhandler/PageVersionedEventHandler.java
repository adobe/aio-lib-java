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

import com.adobe.aio.aem.util.ResourceResolverWrapper;
import com.day.cq.wcm.api.PageModification.ModificationType;
import java.net.URL;
import org.apache.sling.event.jobs.JobManager;

public class PageVersionedEventHandler extends PageModificationEventHandler {

  public PageVersionedEventHandler(JobManager jobManager, URL rootUrl, String imsOrgId,
      OsgiEventMapping osgiEventMapping,
      ResourceResolverWrapper resourceResolverWrapper) {
    super(ModificationType.VERSION_CREATED, jobManager, rootUrl,
        imsOrgId, osgiEventMapping, resourceResolverWrapper);
  }

}