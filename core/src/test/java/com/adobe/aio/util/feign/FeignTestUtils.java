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

import feign.Request;
import feign.Response;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility class for creating test objects in Feign-related tests
 */
public class FeignTestUtils {

  private static final String DEFAULT_URL = "http://test.com";
  public static final String DEFAULT_REQUEST_ID = "test-request-id";
  public static final String DEFAULT_RETRY_AFTER_SECONDS_STR = "60";

  private FeignTestUtils() {
    // Utility class - prevent instantiation
    throw new IllegalStateException("Utility class must not be instantiated");
  }

  /**
   * Creates a basic HTTP request for testing
   */
  public static Request createBasicRequest() {
    return Request.create(Request.HttpMethod.GET, DEFAULT_URL, new HashMap<>(), null, null, null);
  }

  /**
   * Creates a response builder with common defaults
   */
  public static Response.Builder createResponseBuilder(int status, String reason) {
    return Response.builder().status(status).reason(reason).request(createBasicRequest());
  }

  /**
   * Creates a response with retry-after and request-id headers
   */
  public static Response createResponseWithHeaders(int status, String reason, String retryAfter,
      String requestId) {
    Map<String, java.util.Collection<String>> headers = new HashMap<>();
    if (retryAfter != null) {
      headers.put("retry-after", Collections.singletonList(retryAfter));
    }
    if (requestId != null) {
      headers.put("x-request-id", Collections.singletonList(requestId));
    }

    return createResponseBuilder(status, reason).headers(headers).build();
  }

  /**
   * Creates a 429 response with retry-after header
   */
  public static Response create429Response(String retryAfter) {
    return createResponseWithHeaders(429, "Too Many Requests", retryAfter, DEFAULT_REQUEST_ID);
  }

  /**
   * Creates a 429 response without retry-after header
   */
  public static Response create429ResponseWithoutRetryAfter() {
    return createResponseWithHeaders(429, "Too Many Requests", null, DEFAULT_REQUEST_ID);
  }

  /**
   * Creates a 500 response with request-id header
   */
  public static Response create500Response() {
    return createResponseWithHeaders(500, "Internal Server Error", null, DEFAULT_REQUEST_ID);
  }

  /**
   * Creates a 400 response without special headers
   */
  public static Response create400Response() {
    return createResponseBuilder(400, "Bad Request").headers(new HashMap<>()).build();
  }

  /**
   * Creates a response with empty headers
   */
  public static Response createResponseWithEmptyHeaders(int status, String reason) {
    return createResponseBuilder(status, reason).headers(new HashMap<>()).build();
  }
}
