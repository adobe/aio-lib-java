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
import com.adobe.xdm.assets.Asset;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.apache.commons.lang3.tuple.Triple;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.event.jobs.JobManager;
import org.osgi.service.event.Event;


public class DamEventHandler extends AdobeIoEventHandler<Asset> {

  private static final String USER_ID_PROPERTY_KEY = "userId";
  private static final String DATE_PROPERTY_KEY = "date";
  private static final String ASSET_PATH_PROPERTY_KEY = "assetPath";

  public DamEventHandler(JobManager jobManager, URL rootUrl, String imsOrgId,
      OsgiEventMapping osgiEventMapping,
      ResourceResolverWrapperFactory resourceResolverWrapperFactory) {
    super(jobManager, rootUrl, imsOrgId,
        osgiEventMapping, resourceResolverWrapperFactory);
  }

  @Override
  public List<Triple<Asset, String, Date>> getXdmObjectActorDateTriples(Event event,
      ResourceResolverWrapper resourceResolverWrapper) {
    Optional<String> path = EventUtil.getEventPropertyAsString(event, ASSET_PATH_PROPERTY_KEY);
    if (path.isPresent() && isPathOfInterest(path.get())) {
      Resource resource = resourceResolverWrapper.getResolver().getResource(path.get());
      Optional<Asset> asset = getXdmObjectFromResource(resource,
          XdmUtil::getAsset);
      if (asset.isPresent()) {
        return Arrays.asList(
            Triple.of(asset.get(),
                EventUtil.getEventPropertyAsString(event, USER_ID_PROPERTY_KEY)
                    .orElse(XdmUtil.NON_AVAILABLE),
                EventUtil.getEventPropertyAsDate(event, DATE_PROPERTY_KEY)
                    .orElse(new Date())));
      }
    }
    logger.info("The Adobe I/O Event payload could not be computed from the osgi event {}. "
        + "This event will be ignored", event);
    return new ArrayList<>();
  }


}