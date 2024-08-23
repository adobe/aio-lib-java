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
package com.adobe.aio.util;

import com.adobe.aio.exception.AIOException;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class FileUtil {

  private FileUtil() {
  }

  public static Map<String, String> getMap(final String propertiesClassPath) {
    return getMap(getProperties(propertiesClassPath));
  }

  public static Map<String, String> getMap(final Properties properties) {
    Map<String, String> map = new HashMap<>();
    for (final String name : properties.stringPropertyNames()) {
      map.put(name, properties.getProperty(name));
    }
    return map;
  }

  public static Properties getProperties(final String propertiesClassPath) {
    try (InputStream in = FileUtil.class.getClassLoader().getResourceAsStream(propertiesClassPath)) {
      return read(in);
    } catch (Exception e) {
      throw new AIOException("Unable to load your Properties from class path " + propertiesClassPath, e);
    }
  }

  private static Properties read(InputStream in) throws IOException {
    if (in == null) {
      throw new AIOException("InputStream cannot be null");
    }
    Properties prop = new Properties();
    prop.load(in);
    in.close();
    return prop;
  }

}
