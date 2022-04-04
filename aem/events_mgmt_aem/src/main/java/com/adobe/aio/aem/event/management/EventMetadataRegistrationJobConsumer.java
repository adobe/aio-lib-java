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
package com.adobe.aio.aem.event.management;

import com.adobe.aio.event.management.model.EventMetadata;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.sling.event.jobs.Job;
import org.apache.sling.event.jobs.consumer.JobConsumer;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(service = JobConsumer.class, immediate = true, property = {
    JobConsumer.PROPERTY_TOPICS + "="
        + EventMetadataRegistrationJobConsumer.AIO_CONFIG_EVENT_METADA_JOB_TOPIC,
    "label = Adobe I/O Events Metadata Registration Job Consumer",
    "description = Adobe I/O Events Metadata Registration Job Consumer"
})
public class EventMetadataRegistrationJobConsumer implements JobConsumer {

  public static final String AIO_CONFIG_EVENT_METADA_JOB_TOPIC = "aio/config/event_metadata";
  public static final String AIO_EVENT_CODE_PROPERTY = "event_code";
  public static final String AIO_EVENT_METADATA_PROPERTY = "event_metadata";
  public static final String AIO_ERROR_PROPERTY = "error";

  private final Logger log = LoggerFactory.getLogger(this.getClass());
  @Reference
  EventProviderRegistrationService eventProviderRegistrationService;
  @Reference
  EventMetadataStatusSupplier eventMetadataStatusSupplier;
  private String lastErrorMessage;

  @Activate
  protected void activate() {
    log.info("activating");
    eventMetadataStatusSupplier.setJobConsumerReady(true);
  }

  @Override
  public JobResult process(final Job job) {
    if (job.getProperty(AIO_EVENT_CODE_PROPERTY) != null) {
      String eventCode = (String) job.getProperty(AIO_EVENT_CODE_PROPERTY);
      if (job.getProperty(AIO_ERROR_PROPERTY) != null) {
        String error = (String) job.getProperty(AIO_ERROR_PROPERTY);
        eventMetadataStatusSupplier.addStatus(eventCode,
            new EventMetadataStatus(null, null, error));
        log.error("Adobe I/O Events Metadata Registration will not occur:"
            + " due to a event metadata configuration error`{}`", error);
        return JobResult.OK;
      } else {
        EventMetadata configuredEventMetadata = null;
        EventMetadata registeredEventMetadata = null;
        try {
          configuredEventMetadata = new ObjectMapper().readValue
              ((String) job.getProperty(AIO_EVENT_METADATA_PROPERTY), EventMetadata.class);
          registeredEventMetadata =
              eventProviderRegistrationService.registerEventMetadata(configuredEventMetadata);
          eventMetadataStatusSupplier.addStatus(eventCode,
              new EventMetadataStatus(configuredEventMetadata, registeredEventMetadata));
          log.info("Adobe I/O Events Metadata Registration Job Consumer completed:"
              + " registered event metadata `{}`", registeredEventMetadata);
          return JobResult.OK;
        } catch (Exception e) {
          log.error("Adobe I/O Events Metadata Registration Job Consumer `{}` processing failed: `{}",
              job,
              e.getMessage(), e);
          eventMetadataStatusSupplier.addStatus(eventCode,
              new EventMetadataStatus(configuredEventMetadata, registeredEventMetadata, e));
          return JobResult.FAILED;
        }
      }
    } else {
      log.error("Adobe I/O Events Metadata Registration Job Consumer `{}` is missing a `{}", job,
          AIO_EVENT_CODE_PROPERTY);
      return JobResult.CANCEL;
    }
  }

}