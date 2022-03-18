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
package com.adobe.aio.aem.event.osgimapping.eventhandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.apache.sling.event.dea.DEAConstants;
import org.osgi.service.event.Event;

public class EventUtil {

  private EventUtil() {
  }


  public static Optional<Date> getEventPropertyAsDate(Event event, String propertyKey) {
    if (propertyKey == null || propertyKey.isEmpty()) {
      return Optional.empty();
    } else {
      Object datePropertyValue = event.getProperty(propertyKey);
      if (datePropertyValue == null) {
        return Optional.empty();
      } else if (datePropertyValue instanceof Date) {
        return Optional.of((Date) datePropertyValue);
      } else if (datePropertyValue instanceof Calendar) {
        Calendar calendar = (Calendar) datePropertyValue;
        return Optional.of(calendar.getTime());
      } else {
        return Optional.empty();
      }
    }
  }

  public static Optional<String> getEventPropertyAsString(Event event, String propertyKey) {
    if (event.getProperty(propertyKey) != null) {
      return Optional.of(event.getProperty(propertyKey).toString());
    } else {
      return Optional.empty();
    }
  }

  public static List<String> getEventPropertyAsListOfString(Event event, String propertyKey) {
    if (event.getProperty(propertyKey) == null) {
      return new ArrayList<String>();
    } else {
      String[] paths = (String[]) (event.getProperty(propertyKey));
      return Arrays.asList(paths);
    }
  }

  public static boolean isEventProperty(Event event, String key, String expectedValue) {
    return (event.getProperty(key) != null
        && event.getProperty(key).toString().equals(expectedValue));
  }

  public static boolean isEventLocal(Event event) {
    return (event.getProperty(DEAConstants.PROPERTY_APPLICATION) == null);
  }
}
