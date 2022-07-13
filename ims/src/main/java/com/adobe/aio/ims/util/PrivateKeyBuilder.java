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
package com.adobe.aio.ims.util;

import static com.adobe.aio.util.FileUtil.getMapFromProperties;
import static com.adobe.aio.util.FileUtil.readPropertiesFromClassPath;
import static com.adobe.aio.util.FileUtil.readPropertiesFromFile;

import java.io.IOException;
import java.security.PrivateKey;
import java.util.Map;
import java.util.Properties;
import org.apache.commons.lang3.StringUtils;

public class PrivateKeyBuilder {

  public static final String AIO_ENCODED_PKCS_8 = "aio_encoded_pkcs8";
  private static final String AIO_PKCS_8_FILE_PATH = "aio_pkcs8_file_path";

  private static final String AIO_PKCS_12_FILE_PATH = "aio_pkcs12_file_path";
  private static final String AIO_PKCS_12_PASSWORD = "aio_pkcs12_password";
  private static final String AIO_PKCS_12_ALIAS = "aio_pkcs12_alias";

  private Map<String, String> configMap;
  private String encodedPkcs8Key;

  public PrivateKeyBuilder() {
  }

  public PrivateKeyBuilder systemEnv() {
    this.configMap = System.getenv();
    return this;
  }

  /**
   * @param configPath: will first look on the file system, if not found, in the classpath
   * @return a PrivateKeyBuilder loaded with the provided config
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

  public PrivateKeyBuilder encodedPkcs8Key(String encodePkcs8Key) {
    this.encodedPkcs8Key = encodePkcs8Key;
    return this;
  }

  public PrivateKey build() {
    if (!StringUtils.isEmpty(encodedPkcs8Key)) {
      try {
        return KeyStoreUtil.getPrivateKeyFromEncodedPkcs8(encodedPkcs8Key);
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
      if (imsConfig.containsKey(AIO_ENCODED_PKCS_8)) {
        return KeyStoreUtil.getPrivateKeyFromEncodedPkcs8(imsConfig.get(AIO_ENCODED_PKCS_8));
      } else if (imsConfig.containsKey(AIO_PKCS_8_FILE_PATH)) {
        return KeyStoreUtil.getPrivateKeyFromPkcs8File(imsConfig.get(AIO_PKCS_8_FILE_PATH));
      } else if (imsConfig.containsKey(AIO_PKCS_12_FILE_PATH) && imsConfig.containsKey(
          AIO_PKCS_12_PASSWORD)
          && imsConfig.containsKey(AIO_PKCS_12_ALIAS)) {
        return KeyStoreUtil.getPrivateKeyFromPkcs12File(
            imsConfig.get(AIO_PKCS_12_FILE_PATH), imsConfig.get(AIO_PKCS_12_ALIAS),
            imsConfig.get(AIO_PKCS_12_PASSWORD));
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
