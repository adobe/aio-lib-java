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

import com.adobe.aio.aem.status.Status;
import com.adobe.aio.aem.status.StatusServlet;
import javax.servlet.Servlet;
import org.apache.sling.servlets.annotations.SlingServletPaths;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(immediate = true, service = Servlet.class)
@SlingServletPaths("/bin/aio/events/osgi_event_mapping.json")
public class OsgiEventMappingStatusServlet extends StatusServlet {

  @Reference
  private OsgiEventMappingRegistrationService osgiEventMappingStatusSupplier;

  @Override
  public Status getStatus() {
    return osgiEventMappingStatusSupplier.getStatus();
  }
}

