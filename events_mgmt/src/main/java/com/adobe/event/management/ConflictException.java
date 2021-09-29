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
package com.adobe.event.management;

import static com.adobe.util.JacksonUtil.DEFAULT_OBJECT_MAPPER;

import com.adobe.event.management.model.ErrorResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import feign.FeignException;
import feign.Response;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConflictException extends FeignException {

  private final Logger logger = LoggerFactory.getLogger(this.getClass());
  private final String conflictingId;

  public ConflictException(Response response, FeignException exception) {
    super(response.status(), exception.getMessage(), response.request(), exception);
    conflictingId  = getConflictingId(exception.contentUTF8());
  }

  public Optional<String> getConflictingId(){
   return Optional.ofNullable(conflictingId);
  }

  private String getConflictingId(String body){
    try {
      String conflictingId = DEFAULT_OBJECT_MAPPER.readValue(body, ErrorResponse.class).getMessage();
      if (!StringUtils.isEmpty(conflictingId) && !conflictingId.contains(" ")) {
        return conflictingId;
      } else {
        logger.warn("The Conflict/409 Error response does not hold a valid conflicting id: `{}`",conflictingId);
        return null;
      }
    } catch (JsonProcessingException e) {
      e.printStackTrace();
      logger.warn("The Conflict/409 Error response is not of the expected format",e.getMessage());
      return null;
    }
  }
}
