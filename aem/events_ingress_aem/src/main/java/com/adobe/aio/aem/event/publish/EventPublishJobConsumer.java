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
package com.adobe.aio.aem.event.publish;

import org.apache.sling.event.jobs.Job;
import org.apache.sling.event.jobs.consumer.JobConsumer;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(service = JobConsumer.class, immediate = true, property = {
    JobConsumer.PROPERTY_TOPICS + "=" + EventPublishJobConsumer.AIO_EVENTS_JOB_TOPIC,
    "label = Adobe I/O Events Publish Job Consumer",
    "description =Adobe I/O Events Publish Job Consumer"
})
public class EventPublishJobConsumer implements JobConsumer {

  public static final String AIO_EVENTS_JOB_TOPIC = "aio/events";
  public static final String AIO_EVENT_PROPERTY = "event";
  public static final String AIO_EVENT_CODE_PROPERTY = "event_code";

  private final Logger log = LoggerFactory.getLogger(this.getClass());
  private String lastErrorMessage;

  @Reference
  private EventPublishService eventPublishService;

  @Override
  public JobResult process(final Job job) {
    try {
      // This is the Job's process method where the work will be
      // Jobs status is persisted in the JCR under /var/eventing so the management
      // of Jobs is NOT a wholly "in-memory" operations.

      // If you have guaranteed VERY FAST processing, it may be better to tie into an event
      // For information on all the data tied to the Job object
      // > http://sling.apache.org/apidocs/sling7/org/apache/sling/event/jobs/Job.html
      String eventPayload = (String) job.getProperty(AIO_EVENT_PROPERTY);
      String eventCode = (String) job.getProperty(AIO_EVENT_CODE_PROPERTY);
      if (eventPayload == null || eventPayload.isEmpty()) {
        log.error(
            "Adobe I/O Events Publish Job Consumer `{}` is missing an event payload. Marking it as `CANCEL`",
            job);
        return JobResult.CANCEL;
      } else if (eventCode == null) {
        log.error(
            "Adobe I/O Events Publish Job Consumer `{}` is missing an event code. Marking it as `CANCEL`",
            job);
        return JobResult.CANCEL;
      }
      eventPublishService.publishEvent(eventPayload, eventCode);
      log.info("Adobe I/O Events Publish Job Consumer completed: published `{}` event: `{}`",
          eventCode,
          eventPayload);
      return JobResult.OK;
      /**
       * Return the proper JobResult based on the work done...
       *
       * > OK : Processed successfully
       * > FAILED: Processed unsuccessfully and reschedule
       * > CANCEL: Processed unsuccessfully and do NOT reschedule
       * > ASYNC: Process through the JobConsumer.AsyncHandler interface
       */
    } catch (RuntimeException e) {
      log.error("Adobe I/O Events Publish Job Consumer `{}` processing failed: `{}", job,
          e.getMessage(), e);
      if (lastErrorMessage == null || !lastErrorMessage.equals(e.getMessage())) {
        // to avoid flooding logs and job queues
        // see https://jira.corp.adobe.com/browse/GRANITE-27455
        setLastErrorMessage(e.getMessage());
        return JobResult.FAILED;
      } else {
        log.warn(
            "Adobe I/O Events Publish Job Consumer `{}` marked as `CANCEL`, as it looks like we are facing a non resolvable error: `{}`",
            job, e.getMessage());
        setLastErrorMessage(e.getMessage());
        return JobResult.CANCEL;
      }
    }
  }

  private synchronized void setLastErrorMessage(String lastErrorMessage) {
    this.lastErrorMessage = lastErrorMessage;
  }
}