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

import static com.adobe.aio.util.feign.FeignTestUtils.DEFAULT_RETRY_AFTER_SECONDS_STR;
import static com.adobe.aio.util.feign.FeignTestUtils.create429Response;
import static com.adobe.aio.util.feign.FeignTestUtils.createResponseWithEmptyHeaders;
import static java.time.format.DateTimeFormatter.RFC_1123_DATE_TIME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import feign.FeignException;
import feign.Response;
import java.time.ZonedDateTime;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class RetryAfterErrorTest {

  private static final String TEST_REQUEST_ID = "test-request-id";
  private static final String TEST_METHOD = "testMethod";
  private static final Logger logger = LoggerFactory.getLogger(RetryAfterErrorTest.class);

  @Test
  void testRetryAfterErrorCreation() {
    Response response = create429Response(DEFAULT_RETRY_AFTER_SECONDS_STR);
    RetryAfterError retryAfterError = createRetryAfterError(response, DEFAULT_RETRY_AFTER_SECONDS_STR);

    // Test basic properties
    assertBasicProperties(retryAfterError);
    
    // Test retry-after functionality (delay-seconds format)
    assertEquals(60L, retryAfterError.getRetryAfterInSeconds());
    
    // Test request ID
    assertRequestIdProperties(retryAfterError);
  }

  @Test
  void testRetryAfterErrorWithoutRetryAfter() {
    Response response = createResponseWithEmptyHeaders(429, "Too Many Requests");
    RetryAfterError retryAfterError = createRetryAfterError(response, null);

    // Test retry-after functionality when not available
    assertEquals(0L, retryAfterError.getRetryAfterInSeconds());
  }

  @Test
  void testRetryAfterErrorWithInvalidRetryAfter() {
    Response response = createResponseWithEmptyHeaders(429, "Too Many Requests");
    RetryAfterError retryAfterError = createRetryAfterError(response, "invalid");

    // Test retry-after functionality with invalid value
    assertEquals(0L, retryAfterError.getRetryAfterInSeconds()); // Should return 0 for invalid values
  }

  @Test
  void testRetryAfterErrorWithHttpDate() {
    Response response = createResponseWithEmptyHeaders(429, "Too Many Requests");
    String httpDate = "Fri, 31 Dec 1999 23:59:59 GMT";
    RetryAfterError retryAfterError = createRetryAfterError(response, httpDate);

    // Test with HTTP-date format (past date)
    assertEquals(0L, retryAfterError.getRetryAfterInSeconds()); // Past date should return 0
  }

  @Test
  void testRetryAfterErrorWithFutureHttpDate() {
    Response response = createResponseWithEmptyHeaders(429, "Too Many Requests");
    // Add a day to get a future date
    ZonedDateTime futureDate = ZonedDateTime.now().plusDays(1);

    // Format using RFC 1123 formatter
    String formattedFutureDate = futureDate.format(RFC_1123_DATE_TIME);
    logger.info("Future date: {}", formattedFutureDate);

    RetryAfterError retryAfterError = createRetryAfterError(response, formattedFutureDate);

    // Should return a positive number of seconds until the future date
    long retrySeconds = retryAfterError.getRetryAfterInSeconds();
    assertTrue(retrySeconds > 0, "Should return positive seconds for future date");
    assertEquals(24 * 60 * 60, retrySeconds);
  }

  // Helper methods for creating test objects and assertions

  private RetryAfterError createRetryAfterError(Response response, String retryAfter) {
    FeignException originalException = FeignException.errorStatus(TEST_METHOD, response);
    return new RetryAfterError(response, originalException, TEST_REQUEST_ID, retryAfter);
  }

  private void assertBasicProperties(RetryAfterError retryAfterError) {
    assertEquals(429, retryAfterError.status());
    assertTrue(retryAfterError.getMessage().contains("Rate limit exceeded"));
    assertTrue(retryAfterError.getMessage().contains(TEST_REQUEST_ID));
    assertTrue(retryAfterError.getMessage().contains(DEFAULT_RETRY_AFTER_SECONDS_STR));
  }

  private void assertRequestIdProperties(RetryAfterError retryAfterError) {
    assertTrue(retryAfterError.getUpStreamRequestId().isPresent());
    assertEquals(RetryAfterErrorTest.TEST_REQUEST_ID, retryAfterError.getUpStreamRequestId().get());
  }
}
