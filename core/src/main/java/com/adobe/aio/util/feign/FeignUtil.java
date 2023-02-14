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

import com.adobe.aio.util.JacksonUtil;
import feign.Feign;
import feign.Logger.Level;
import feign.Request;
import feign.RetryableException;
import feign.Retryer;
import feign.form.FormEncoder;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import feign.optionals.OptionalDecoder;
import feign.slf4j.Slf4jLogger;
import io.github.resilience4j.core.IntervalFunction;
import io.github.resilience4j.feign.FeignDecorators;
import io.github.resilience4j.feign.Resilience4jFeign;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

public class FeignUtil {

  public static final int DEFAULT_CONNECT_TIMEOUT_IN_SECONDS = 10;
  public static final int DEFAULT_READ_TIMEOUT_IN_SECONDS = 60;
  private static final long DEFAULT_RETRY_INITIAL_INTERVAL_IN_MILLISECONDS = 2000;

  private FeignUtil() {
  }

  /**
   * @return the base Feign builder we want to reuse across the sdk with jackson decoder, logger and
   * our global read and time out options
   */
  public static Feign.Builder getBaseBuilder() {
    return getBaseBuilder(DEFAULT_RETRY_INITIAL_INTERVAL_IN_MILLISECONDS);
  }

  public static Feign.Builder getBaseBuilder(long initialIntervalMillis) {
    final IntervalFunction exponentialBackoff = IntervalFunction
        .ofExponentialBackoff(initialIntervalMillis, 2.0);
    FeignDecorators decorators = FeignDecorators.builder()
        .withRetry(Retry.of("default", RetryConfig.custom()
            .maxAttempts(3)
            .retryOnException((t) -> t instanceof RetryableException)
            .intervalBiFunction((attempt, result) -> result
                  .bimap(
                      e -> {
                        if (e instanceof RetryableException) {
                          RetryableException ex = (RetryableException) e;
                          if (ex.retryAfter() != null) {
                            long interval = System.currentTimeMillis() - ex.retryAfter().getTime();
                            return Math.max(exponentialBackoff.apply(attempt), interval);
                          }
                        }
                        return exponentialBackoff.apply(attempt);
                      },
                      r -> exponentialBackoff.apply(attempt)
                  )
                  .fold(Function.identity(), Function.identity())
            )
            .build())
        ).build();
    return Resilience4jFeign.builder(decorators)
        .logger(new Slf4jLogger())
        //.logLevel(Level.BASIC)
        .logLevel(Level.NONE)
        .retryer(Retryer.NEVER_RETRY)
        //.logLevel(Level.FULL) // use this instead when debugging
        .decode404()
        .errorDecoder(new IOErrorDecoder())
        .options(new Request.Options(DEFAULT_CONNECT_TIMEOUT_IN_SECONDS, TimeUnit.SECONDS,
            DEFAULT_READ_TIMEOUT_IN_SECONDS, TimeUnit.SECONDS, true));
  }

  /**
   * @return a Feign builder we want to reuse across the sdk with jackson encoder and decoder,
   * logger and our global read and time out options
   */
  public static Feign.Builder getDefaultBuilder() {
    return getDefaultBuilder(DEFAULT_RETRY_INITIAL_INTERVAL_IN_MILLISECONDS);
  }

  public static Feign.Builder getDefaultBuilder(long initialIntervalMillis) {
    return getBaseBuilder(initialIntervalMillis)
        .decoder(new OptionalDecoder(new JacksonDecoder(JacksonUtil.DEFAULT_OBJECT_MAPPER)))
        .encoder(new JacksonEncoder(JacksonUtil.DEFAULT_OBJECT_MAPPER));

  }

  /**
   * @return a Feign builder we want to reuse across the sdk with jackson decoder, logger and our
   * global read and time out options, and form encoder
   */
  public static Feign.Builder getBuilderWithFormEncoder() {
    return getBaseBuilder(DEFAULT_RETRY_INITIAL_INTERVAL_IN_MILLISECONDS);
  }

  public static Feign.Builder getBuilderWithFormEncoder(long initialIntervalMillis) {
    return getBaseBuilder(initialIntervalMillis)
        .decoder(new OptionalDecoder(new JacksonDecoder(JacksonUtil.DEFAULT_OBJECT_MAPPER)))
        .encoder(new FormEncoder());
  }

}
