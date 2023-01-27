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
package com.adobe.aio.event.webhook.cache;

import com.adobe.aio.event.webhook.model.CacheableObject;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class CacheServiceImpl implements CacheService {

  private static final Logger logger = LoggerFactory.getLogger(CacheServiceImpl.class);
  private static final int DEFAULT_TTL_IN_MINUTES = 1440;
  private static final Date CURRENT_SYSTEM_DATE = new Date();

  Map<String, Object> cacheMap;

  @Nullable
  @Override
  public String get(@Nonnull String key) {
    CacheableObject obj = (CacheableObject) cacheMap.get(key);
    if (obj != null) {
      if (isExpired(obj)) {
        logger.debug("public key object in cache is expired..invalidating entry for key {}", key);
        cacheMap.remove(key);
        return null;
      } else {
        return obj.getValue();
      }
    }
    return null;
  }

  @Override
  public void put(@Nonnull String key, @Nonnull Object value) {
    putWithExpiry(key, value, DEFAULT_TTL_IN_MINUTES);
  }

  public void putWithExpiry(@Nonnull String key, @Nonnull Object value, int ttlInMinutes) {
    CacheableObject cacheableObject = new CacheableObject(key, (String) value,
        getExpirationDate(ttlInMinutes));
    cacheMap.put(key, cacheableObject);
  }

  @Override
  public boolean isExpired(CacheableObject cacheableObject) {
    return CURRENT_SYSTEM_DATE.after(cacheableObject.getPubKeyExpiryDate());
  }

  private CacheServiceImpl initialiseCacheMap() {
    this.cacheMap = new HashMap<>();
    return this;
  }

  public static CacheBuilder cacheBuilder() {
    return new CacheBuilder();
  }

  public static class CacheBuilder {
    public CacheServiceImpl buildCache() {
      return new CacheServiceImpl()
          .initialiseCacheMap();
    }
  }

  private Date getExpirationDate(int minutesToLive) {
    Date expirationDate = new java.util.Date();
    java.util.Calendar cal = java.util.Calendar.getInstance();
    cal.setTime(expirationDate);
    cal.add(cal.MINUTE, minutesToLive);
    expirationDate = cal.getTime();
    return expirationDate;
  }
}
