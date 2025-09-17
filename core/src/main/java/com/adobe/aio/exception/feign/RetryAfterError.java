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
package com.adobe.aio.exception.feign;

import static java.time.format.DateTimeFormatter.RFC_1123_DATE_TIME;

import feign.FeignException;
import feign.Response;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RetryAfterError extends FeignException {

  private static final Logger logger = LoggerFactory.getLogger(RetryAfterError.class);
  private static final String ERROR_MESSAGE_TEMPLATE = "Rate limit exceeded. request-id: `%s`, retry-after: `%s` seconds. %s";
  private final String upStreamRequestId;
  private final String retryAfter;

  public RetryAfterError(Response response, FeignException exception, String requestId,
      String retryAfter) {
    super(response.status(),
        String.format(ERROR_MESSAGE_TEMPLATE, requestId, retryAfter, exception.getMessage()),
        response.request(), exception);
    this.upStreamRequestId = requestId;
    this.retryAfter = retryAfter;
  }

  public Optional<String> getUpStreamRequestId() {
    return Optional.ofNullable(upStreamRequestId);
  }
  /**
   * Get the retry-after value in seconds
   *
   * @return the retry-after value in seconds, or 0 if not available or invalid
   */
  public long getRetryAfterInSeconds() {
    if (retryAfter == null || retryAfter.trim().isEmpty()) {
      return 0L;
    }

    String trimmedRetryAfterHeaderValue = retryAfter.trim();

    // First, try to parse as a number of seconds (delay-seconds)
    try {
      return Long.parseLong(trimmedRetryAfterHeaderValue);
    } catch (NumberFormatException e) {
      // If not a number, try to parse as HTTP-date
      try {
        // Parse HTTP-date format (e.g., "Tue, 3 Jun 2008 11:05:30 GMT")
        Instant retryInstant = Instant.from(RFC_1123_DATE_TIME.parse(trimmedRetryAfterHeaderValue));
        Instant now = Instant.now();
        long secondsUntilRetry = retryInstant.getEpochSecond() - now.getEpochSecond();
        return Math.max(0L, secondsUntilRetry); // Ensure non-negative
      } catch (DateTimeParseException dateTimeParseException) {
        logger.warn("Invalid retry-after header value (neither delay-seconds nor HTTP-date): {}",
            retryAfter, dateTimeParseException);
        return 0L;
      }
    }
  }
}
