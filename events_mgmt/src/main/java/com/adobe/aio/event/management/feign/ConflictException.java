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
package com.adobe.aio.event.management.feign;

import feign.FeignException;
import feign.Response;
import java.util.Collection;

public class ConflictException extends FeignException {

  public static final String X_CONFLICTING_ID = "x-conflicting-id";
  private final String conflictingId;

  public ConflictException(Response response, FeignException exception) {
    super(response.status(), exception.getMessage(), response.request(), exception);
    Collection<String> conflictingIdHeader = response.headers().get(X_CONFLICTING_ID);
    conflictingId  = conflictingIdHeader!=null ? conflictingIdHeader.stream().findFirst().orElse(null) : null;
  }

  public String getConflictingId() {
    return conflictingId;
  }
}
