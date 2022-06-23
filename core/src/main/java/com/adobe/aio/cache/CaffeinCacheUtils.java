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
package com.adobe.aio.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CaffeinCacheUtils {

  private static final Logger logger = LoggerFactory.getLogger(CaffeinCacheUtils.class);

  private CaffeinCacheUtils() {
    throw new IllegalStateException("This class is not meant to be instantiated.");
  }

  public static <K, V> Cache<K, V> buildCacheWithExpiryAfterWrite(String cacheName,
      long expiryInMinutes, long maxEntryCount) {

    logger.info("Initializing cache: {} with expiry-after-write: {} minutes, maxEntryCount: {}",
        cacheName, expiryInMinutes, maxEntryCount);

    return Caffeine.newBuilder()
        .expireAfterWrite(expiryInMinutes, TimeUnit.MINUTES)
        .maximumSize(maxEntryCount)
        .build();
  }
}

