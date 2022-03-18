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
package com.adobe.aio.aem.event.publish.internal;

import static com.adobe.aio.aem.event.management.EventMetadataSupplier.PING_EVENT_CODE;

import com.adobe.aio.aem.auth.JWTAuthInterceptorSupplier;
import com.adobe.aio.aem.event.management.EventProviderRegistrationService;
import com.adobe.aio.aem.event.publish.EventPublishService;
import com.adobe.aio.aem.event.publish.ocd.ApiPublishingConfig;
import com.adobe.aio.aem.status.Status;
import com.adobe.aio.event.publish.PublishService;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Component(service = EventPublishService.class, property = {
    "label = Adobe I/O Events' Event Publish Service",
    "description = Adobe I/O Events' Event Publish Service"})
@Designate(ocd = ApiPublishingConfig.class)
public class EventPublishServiceImpl implements EventPublishService {

  private final Logger log = LoggerFactory.getLogger(getClass());
  @Reference
  JWTAuthInterceptorSupplier jwtAuthInterceptorSupplier;
  @Reference
  private EventProviderRegistrationService eventProviderRegistrationService;
  private String publishUrl;

  @Activate
  @Modified
  protected void activate(ApiPublishingConfig configuration) {
    log.info("activating");
    this.publishUrl = configuration.aio_publish_url();
  }

  @Override
  public Status getStatus() {
    Map<String, Object> details = new HashMap<>(1);
    details.put("aio_ping_published", false);
    try {
      details.put("aio_publish_url", publishUrl);
      details.put("aio_event_provider_status", eventProviderRegistrationService.getStatus());
      details.put("aio_jwt_status", jwtAuthInterceptorSupplier.getStatus());
      this.publishEvent(
          "{\"publisher\":\"AEM status check\","
              + "\"time\":\"" + Instant.now().toEpochMilli() + "\"}",
          PING_EVENT_CODE);
      details.put("aio_ping_published", true);
      return new Status(Status.UP, details);
    } catch (Exception e) {
      return new Status(Status.DOWN, details, e);
    }
  }

  @Override
  public void publishEvent(String eventJsonPayload, String adobeIoEventCode) {
    PublishService publishService = PublishService.builder()
        .authInterceptor(jwtAuthInterceptorSupplier.getJWTAuthInterceptor())
        .url(publishUrl)
        .build();
    publishService.publishRawEvent(
        eventProviderRegistrationService.getRegisteredProvider().getId()
        , adobeIoEventCode, eventJsonPayload);

  }

}


