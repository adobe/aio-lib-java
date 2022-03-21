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
package com.adobe.aio.aem.event.management.internal;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

import com.adobe.aio.aem.event.management.EventProviderConfigSupplier;
import com.adobe.aio.aem.event.management.EventProviderRegistrationService;
import com.adobe.aio.aem.event.management.ocd.ApiManagementConfig;
import com.adobe.aio.aem.status.Status;
import com.adobe.aio.aem.workspace.WorkspaceSupplier;
import com.adobe.aio.event.management.ProviderService;
import com.adobe.aio.event.management.model.EventMetadata;
import com.adobe.aio.event.management.model.Provider;
import com.adobe.aio.exception.AIOException;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Component(property = {
    "label = Adobe I/O Events' Provider Registration Service",
    "description = Adobe I/O Events' Provider Provider Registration Servic"})
@Designate(ocd = ApiManagementConfig.class)
public class EventProviderRegistrationServiceImpl implements EventProviderRegistrationService {

  private final Logger log = LoggerFactory.getLogger(getClass());

  @Reference
  private WorkspaceSupplier workspaceSupplier;
  
  @Reference
  private EventProviderConfigSupplier eventProviderConfigSupplier;

  private ProviderService providerService;
  private String providerId;
  private String apiManagementUrl;

  @Activate
  protected void activate(ApiManagementConfig config) {
    this.apiManagementUrl = config.aio_api_management_url();
    try {
      registerProvider();
    } catch (Exception e) {
      log.error("Adobe I/O Events Provider Registration Error: {}", e.getMessage(), e);
    }
  }

  @Override
  public Status getStatus() {
    Map<String, Object> details = new HashMap<>(1);
    try {
      details.put("workspace_status", workspaceSupplier.getStatus());
      details.put("provider_config_status", eventProviderConfigSupplier.getStatus());
      details.put("api_management_url", this.apiManagementUrl);
      //details.put("providers",providerService.getProviders());
      details.put("provider_already_registered", this.isProviderRegistered());
      details.put("registered_provider", this.getRegisteredProvider());
      return new Status(Status.UP, details);
    } catch (Exception e) {
      return new Status(Status.DOWN, details, e);
    }
  }

  @Override
  public EventMetadata registerEventMetadata(final EventMetadata eventMetadata) {
    return getProviderService().createEventMetadata(this.providerId, eventMetadata)
        .orElseThrow(() -> new AIOException("Error creating a new Adobe I/O Events Metadata, "
            + "your workspace or provider metadata are unknown to Adobe I/O Events."
            + " Please check your configurations."));
    //we may get 404 while POST-ing or PUT-ing when metadata are not found
  }

  private Provider registerProvider() {
    Optional<Provider> createdProvider = getProviderService().createOrUpdateProvider(
        eventProviderConfigSupplier.getProviderInputModel());
    if (createdProvider.isPresent()) {
      this.providerId = createdProvider.get().getId();
      return createdProvider.get();
    } else { //we may get 404 while POST-ing or PUT-ing when metadata are not found
      throw new AIOException("Error creating a new Adobe I/O Events Provider, "
          + "your workspace or provider metadata are unknown to Adobe I/O Events."
          + " Please check your configurations.");
    }
  }

  @Override
  public Provider getRegisteredProvider() {
    if (!isProviderRegistered()) {
      log.warn("The Adobe I/O Events Provider was not registered yet, trying registering it...");
      return registerProvider();
    } else {
      return getProviderService().findProviderById(this.providerId)
          .orElseThrow(() -> new AIOException("Error looking up the Adobe I/O Events Provider "
              + "previously successfully registered."));
    }
  }

  private boolean isProviderRegistered() {
    return !StringUtils.isEmpty(this.providerId);
  }


  private ProviderService getProviderService() {
    if (this.providerService == null) {
      this.providerService = ProviderService.builder()
          .workspace(workspaceSupplier.getWorkspace())
          .url(apiManagementUrl)
          .build();
    }
    return this.providerService;
  }


}
