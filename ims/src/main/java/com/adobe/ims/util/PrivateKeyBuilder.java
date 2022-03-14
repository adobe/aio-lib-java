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
package com.adobe.ims.util;

import static com.adobe.util.FileUtil.getMapFromProperties;
import static com.adobe.util.FileUtil.readPropertiesFromClassPath;
import static com.adobe.util.FileUtil.readPropertiesFromFile;

import java.io.IOException;
import java.security.PrivateKey;
import java.util.Map;
import java.util.Properties;
import org.apache.commons.lang3.StringUtils;

public class PrivateKeyBuilder {

  private static final String ENCODE_PKCS8_KEY = "aio_encoded_pkcs8";
  private static final String PKCS8_FILE_PATH = "aio_pkcs8_file_path";

  private static final String PKCS12_FILE_PATH = "aio_pkcs12_file_path";
  private static final String PKCS12_PASSWORD = "aio_pkcs12_password";
  private static final String PKCS12_ALIAS = "aio_pkcs12_alias";

  private Map<String, String> configMap;
  private String encodePkcs8Key;

  public PrivateKeyBuilder() {
  }

  public PrivateKeyBuilder systemEnv() {
    this.configMap = System.getenv();
    return this;
  }

  /**
   * will first look on the file system, if not found, in the classpath
   */
  public PrivateKeyBuilder configPath(String configPath) throws IOException {
    this.configMap = getMapFromProperties(
        readPropertiesFromFile(configPath)
            .orElse(readPropertiesFromClassPath(configPath)));
    return this;
  }

  public PrivateKeyBuilder properties(Properties properties) {
    this.configMap = getMapFromProperties(properties);
    return this;
  }

  public PrivateKeyBuilder encodePkcs8Key(String encodePkcs8Key) {
    this.encodePkcs8Key = encodePkcs8Key;
    return this;
  }

  public PrivateKey build() {
    if (!StringUtils.isEmpty(encodePkcs8Key)) {
      try {
        return KeyStoreUtil.getPrivateKeyFromEncodedPkcs8(encodePkcs8Key);
      } catch (Exception e) {
        throw new IllegalArgumentException(
            "AIO Invalid encoded pkcs8 Private Key configuration. "
                + "" + e.getMessage(), e);
      }
    } else {
      return getPrivateKey(this.configMap);
    }
  }

  private static PrivateKey getPrivateKey(final Map<String, String> imsConfig) {
    try {
      if (imsConfig.containsKey(ENCODE_PKCS8_KEY)) {
        return KeyStoreUtil.getPrivateKeyFromEncodedPkcs8(imsConfig.get(ENCODE_PKCS8_KEY));
      } else if (imsConfig.containsKey(PKCS8_FILE_PATH)) {
        return KeyStoreUtil.getPrivateKeyFromPkcs8File(imsConfig.get(PKCS8_FILE_PATH));
      } else if (imsConfig.containsKey(PKCS12_FILE_PATH) && imsConfig.containsKey(PKCS12_PASSWORD)
          && imsConfig.containsKey(PKCS12_ALIAS)) {
        return KeyStoreUtil.getPrivateKeyFromPkcs12File(
            imsConfig.get(PKCS12_FILE_PATH), imsConfig.get(PKCS12_ALIAS),
            imsConfig.get(PKCS12_PASSWORD));
      } else {
        throw new IllegalArgumentException(
            "AIO is missing a valid (pkcs8 or pkcs12) Private Key configuration");
      }
    } catch (Exception e) {
      throw new IllegalArgumentException(
          "AIO holds an invalid (pkcs8 or pkcs12) Private Key configuration. "
              + "" + e.getMessage(), e);
    }
  }


}
