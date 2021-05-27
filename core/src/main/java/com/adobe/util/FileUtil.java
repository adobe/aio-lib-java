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
package com.adobe.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.Properties;
import org.apache.commons.lang3.StringUtils;

public class FileUtil {

  private FileUtil(){}

  public static Optional<Properties> readPropertiesFromFile(final String configFilePath) throws IOException {
    if (StringUtils.isEmpty(configFilePath)){
      return Optional.empty();
    }
    else {
      try (InputStream in = new FileInputStream(configFilePath)) {
        return Optional.of(read(in));
      }
    }
  }

  public static Properties readPropertiesFromClassPath(final String configClassPath) throws IOException {
    try (InputStream in = FileUtil.class.getClassLoader().getResourceAsStream(configClassPath) ) {
     return read(in);
    }
  }

  private static Properties read(InputStream in) throws IOException {
    Properties prop = new Properties();
    prop.load(in);
    in.close();
    return prop;
  }

}
