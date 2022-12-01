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
package com.adobe.aio.event.management.model;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum DeliveryType {
  WEBHOOK("webhook", true), JOURNAL("journal", false),
  WEBHOOK_BATCH("webhook_batch", true);

  private final boolean isWebhookDelivery;
  private final String friendlyName;

  private static final Logger logger = LoggerFactory.getLogger(DeliveryType.class);

  private static final Map<String, DeliveryType> DELIVERY_TYPE_FRIENDLY_NAME_MAP = new HashMap<>();

  static {
    Stream.of(DeliveryType.values())
                    .filter(deliveryType -> StringUtils.isNotBlank(deliveryType.getFriendlyName()))
                    .forEach(deliveryType -> DELIVERY_TYPE_FRIENDLY_NAME_MAP.put(deliveryType.getFriendlyName().toLowerCase(),
                                    deliveryType));
  }

  public static DeliveryType fromFriendlyName(String friendlyName) {
    if (!DELIVERY_TYPE_FRIENDLY_NAME_MAP.containsKey(friendlyName.toLowerCase())) {
      logger.error("`{}` is not a delivery type known to Adobe I/O Events", friendlyName);
      throw new IllegalArgumentException("Invalid delivery type code: " + friendlyName);
    } else {
      return DELIVERY_TYPE_FRIENDLY_NAME_MAP.get(friendlyName.toLowerCase());
    }
  }

  public boolean isWebhookDelivery() {
    return isWebhookDelivery;
  }

  public String getFriendlyName() {
    return friendlyName;
  }

  DeliveryType(String friendlyName, boolean isWebhookDelivery) {
    this.isWebhookDelivery = isWebhookDelivery;
    this.friendlyName = friendlyName;
  }
}
