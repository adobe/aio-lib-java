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

import java.security.PrivateKey;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

public class PrivateKeyBuilder {

  public static final String AIO_ENCODED_PKCS_8 = "aio_encoded_pkcs8";

  private Map<String, String> configMap;
  private String encodedPkcs8Key;

  public PrivateKeyBuilder() {
  }


  public PrivateKeyBuilder encodedPkcs8Key(String encodedPkcs8Key) {
    this.encodedPkcs8Key = encodedPkcs8Key;
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
      return null;
    }
  }


}
