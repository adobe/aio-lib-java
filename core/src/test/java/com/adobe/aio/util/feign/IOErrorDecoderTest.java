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

import static com.adobe.aio.util.feign.FeignTestUtils.DEFAULT_REQUEST_ID;
import static com.adobe.aio.util.feign.FeignTestUtils.DEFAULT_RETRY_AFTER_SECONDS_STR;
import static com.adobe.aio.util.feign.FeignTestUtils.create429Response;
import static com.adobe.aio.util.feign.FeignTestUtils.create429ResponseWithoutRetryAfter;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.adobe.aio.exception.feign.IOUpstreamError;
import com.adobe.aio.exception.feign.RetryAfterError;
import feign.FeignException;
import feign.Response;
import org.junit.jupiter.api.Test;

class IOErrorDecoderTest {

  private static final String TEST_METHOD = "testMethod";
  private final IOErrorDecoder decoder = new IOErrorDecoder();

  @Test
  void testDecode429ResponseWithRetryAfter() {
    Response response = create429Response(DEFAULT_RETRY_AFTER_SECONDS_STR);
    Exception exception = decoder.decode(TEST_METHOD, response);

    // Should return a RetryAfterError
    assertInstanceOf(RetryAfterError.class, exception);
    RetryAfterError retryAfterError = (RetryAfterError) exception;

    assertRetryAfterErrorProperties(retryAfterError, 60L);
  }

  @Test
  void testDecode429ResponseWithoutRetryAfter() {
    Response response = create429ResponseWithoutRetryAfter();
    Exception exception = decoder.decode(TEST_METHOD, response);

    // Should still return a RetryAfterError
    assertInstanceOf(RetryAfterError.class, exception);
    RetryAfterError retryAfterError = (RetryAfterError) exception;

    assertEquals(429, retryAfterError.status());
    assertEquals(0L, retryAfterError.getRetryAfterInSeconds());
    assertRequestIdPresent(retryAfterError);
  }

  @Test
  void testDecode500Response() {
    Response response = FeignTestUtils.create500Response();
    Exception exception = decoder.decode(TEST_METHOD, response);

    // Should return an IOUpstreamError for 5xx responses
    assertInstanceOf(IOUpstreamError.class, exception);
    IOUpstreamError upstreamError = (IOUpstreamError) exception;

    assertEquals(500, upstreamError.status());
    assertRequestIdPresent(upstreamError);
  }

  @Test
  void testDecode400Response() {
    Response response = FeignTestUtils.create400Response();
    Exception exception = decoder.decode(TEST_METHOD, response);

    // Should return a regular FeignException for 4xx responses (except 429)
    assertInstanceOf(FeignException.class, exception);
    assertFalse(exception instanceof RetryAfterError);
    assertFalse(exception instanceof IOUpstreamError);

    assertEquals(400, ((FeignException) exception).status());
  }

  // Helper methods for assertions

  private void assertRetryAfterErrorProperties(RetryAfterError retryAfterError,
      long expectedSeconds) {
    assertEquals(429, retryAfterError.status());
    assertEquals(expectedSeconds, retryAfterError.getRetryAfterInSeconds());
    assertRequestIdPresent(retryAfterError);
  }

  private void assertRequestIdPresent(RetryAfterError retryAfterError) {
    assertTrue(retryAfterError.getUpStreamRequestId().isPresent());
    assertEquals(DEFAULT_REQUEST_ID, retryAfterError.getUpStreamRequestId().get());
  }

  private void assertRequestIdPresent(IOUpstreamError upstreamError) {
    assertTrue(upstreamError.getUpStreamRequestId().isPresent());
    assertEquals(DEFAULT_REQUEST_ID, upstreamError.getUpStreamRequestId().get());
  }
}
