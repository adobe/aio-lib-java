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

import java.util.Objects;

public class CacheableObject {

  private String key;
  private String value;
  private int expiryInMinutes;

  public CacheableObject(String key, String value, int expiryInMinutes) {
    this.key = key;
    this.value = value;
    this.expiryInMinutes = expiryInMinutes;
  }

  public String getKey() {
    return key;
  }

  public String getValue() {
    return value;
  }

  public int getExpiryInMinutes() {
    return expiryInMinutes;
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
    return expiryInMinutes == that.expiryInMinutes && Objects.equals(key, that.key)
        && Objects.equals(value, that.value);
  }

  @Override
  public int hashCode() {
    return Objects.hash(key, value, expiryInMinutes);
  }

  @Override
  public String toString() {
    return "CacheableObject{" +
        "key='" + key + '\'' +
        ", value='" + value + '\'' +
        ", expiryInMinutes=" + expiryInMinutes +
        '}';
  }
}
