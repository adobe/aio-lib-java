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
package com.adobe.aio.util.feign;

import com.adobe.aio.exception.feign.IOUpstreamError;
import feign.FeignException;
import feign.Response;
import feign.codec.ErrorDecoder;
import java.util.Collection;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IOErrorDecoder implements ErrorDecoder {

  public static final String REQUEST_ID = "x-request-id";

  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  @Override
  public Exception decode(String methodKey, Response response) {
    FeignException exception = FeignException.errorStatus(methodKey, response);
    logger.warn("Upstream response error ({},{})", response.status(), exception.contentUTF8());
    return (response.status()>=400) ?
        new IOUpstreamError(response, exception, getRequestId(response.headers())) : exception;
  }

  private String getRequestId(Map<String, Collection<String>> headers){
    try {
      return headers.get(REQUEST_ID).iterator().next();
    } catch (Exception e) {
      logger.warn("The upstream Error response does not hold any {} header",REQUEST_ID, e.getMessage());
      return "NA";
    }
  }
}
