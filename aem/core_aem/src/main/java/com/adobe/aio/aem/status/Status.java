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
package com.adobe.aio.aem.status;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Status {

  public static final String UP = "up";
  public static final String DOWN = "down";

  @JsonProperty("status")
  private String status;
  @JsonProperty("details")
  private Object details;
  @JsonProperty("error")
  private String error;
  @JsonProperty("version")
  private String version;

  public Status(boolean status, Object details) {
    this(status ? UP : DOWN, details);
  }

  public Status(Object details, Throwable error) {
    this(error == null ? UP : DOWN, details, error);
  }

  public Status(String status, Object details) {
    this.status = status;
    this.details = details;
    this.version = org.osgi.framework.FrameworkUtil.getBundle(getClass()).getVersion().toString();
  }

  public Status(String status, Object details, String error) {
    this.status = status;
    this.details = details;
    this.error = error;
    this.version = org.osgi.framework.FrameworkUtil.getBundle(getClass()).getVersion().toString();
  }

  public Status(String status, Object details, Throwable error) {
    this(status, details, error != null ?
        error.getClass().getSimpleName() + ": " + error.getMessage() : null);
  }

  public String getStatus() {
    return status;
  }

  public Object getDetails() {
    return details;
  }

  public String getError() {
    return error;
  }

  @Override
  public String toString() {
    return "HC{" +
        "status='" + status + '\'' +
        ", details=" + details +
        ", error='" + error + '\'' +
        '}';
  }
}
