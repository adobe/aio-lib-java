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

public class EventMetadataStatus {

  private final EventMetadata configuredEventMetadata;
  private final EventMetadata registeredEventMetadata;
  private final String error;

  public EventMetadataStatus(final EventMetadata configured, final EventMetadata registered) {
    this(configured, registered, (String) null);
  }

  public EventMetadataStatus(final EventMetadata configured, final EventMetadata registered, final Throwable error) {
    this(configured, registered, error!=null ? error.getClass().getSimpleName() + ":" + error.getMessage() : null);
  }

  public EventMetadataStatus(final EventMetadata configured, final EventMetadata registered, final String error) {
    this.configuredEventMetadata = configured;
    this.registeredEventMetadata = registered;
    this.error = error;
  }

  public EventMetadata getConfiguredEventMetadata() {
    return configuredEventMetadata;
  }

  public boolean isUp() {
    return (error == null && configuredEventMetadata != null && registeredEventMetadata != null);
  }

  public EventMetadata getRegisteredEventMetadata() {
    return registeredEventMetadata;
  }

  public String getError() {
    return error;
  }
}
