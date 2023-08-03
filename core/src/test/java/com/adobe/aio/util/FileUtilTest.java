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

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class FileUtilTest {

  private static final String KEY = "key";
  private static final String VALUE = "value";
  private static final String TEST_PROPERTIES_FILE = "test.properties";

  private Properties getTestProperties() {
    Properties properties = new Properties();
    properties.put(KEY, VALUE);
    return properties;
  }

  @Test
  public void testGetMapFromProperties() {
    Map<String, String> map = new HashMap<>();
    map.put(KEY, VALUE);
    assertEquals(map, FileUtil.getMapFromProperties(getTestProperties()));
  }

  @Test
  public void testReadPropertiesFromFile() {
    assertFalse(FileUtil.readPropertiesFromFile("").isPresent());
    assertFalse(FileUtil.readPropertiesFromFile(null).isPresent());
  }

  @Test
  public void testReadPropertiesFromClassPath() {
    assertEquals(getTestProperties(), FileUtil.readPropertiesFromClassPath(TEST_PROPERTIES_FILE));
  }

}
