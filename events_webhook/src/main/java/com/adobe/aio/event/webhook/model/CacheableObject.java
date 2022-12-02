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
package com.adobe.aio.event.webhook.model;

import java.util.Date;
import java.util.Objects;

public class CacheableObject {

  private String key;
  private String value;
  private Date pubKeyExpiryDate;

  public CacheableObject(String key, String value, Date expiryInMinutes) {
    this.key = key;
    this.value = value;
    this.pubKeyExpiryDate = expiryInMinutes;
  }

  public String getKey() {
    return key;
  }

  public String getValue() {
    return value;
  }

  public Date getPubKeyExpiryDate() {
    return pubKeyExpiryDate;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CacheableObject that = (CacheableObject) o;
    return Objects.equals(key, that.key) && Objects.equals(value, that.value)
        && Objects.equals(pubKeyExpiryDate, that.pubKeyExpiryDate);
  }

  @Override
  public int hashCode() {
    return Objects.hash(key, value, pubKeyExpiryDate);
  }

  @Override
  public String toString() {
    return "CacheableObject{" +
        "key='" + key + '\'' +
        ", value='" + value + '\'' +
        ", pubKeyExpiryDate=" + pubKeyExpiryDate +
        '}';
  }
}
