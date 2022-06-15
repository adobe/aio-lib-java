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

import com.adobe.aio.exception.AIOException;
import com.adobe.aio.util.FileUtil;
import com.adobe.aio.workspace.Workspace;
import java.io.IOException;
import java.security.PrivateKey;
import java.util.Properties;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestUtil {

  public static final String API_URL = "aio_api_url";
  public static final String PUBLISH_URL = "aio_publish_url";
  public static final String DEFAULT_TEST_PROPERTIES = "workspace.secret.properties";
  private static final Logger logger = LoggerFactory.getLogger(TestUtil.class);

  private TestUtil() {
  }

  public static Workspace.Builder getTestWorkspaceBuilder() {
    if (StringUtils.isNoneBlank(
        System.getProperty(PrivateKeyBuilder.AIO_ENCODED_PKCS_8),
        System.getProperty(Workspace.API_KEY),
        System.getProperty(Workspace.WORKSPACE_ID),
        System.getProperty(Workspace.CLIENT_SECRET),
        System.getProperty(Workspace.CONSUMER_ORG_ID),
        System.getProperty(Workspace.CREDENTIAL_ID),
        System.getProperty(Workspace.IMS_ORG_ID),
        System.getProperty(Workspace.META_SCOPES),
        System.getProperty(Workspace.PROJECT_ID),
        System.getProperty(Workspace.TECHNICAL_ACCOUNT_ID))) {
      return getSystemPropertiesWorkspaceBuilder();
    } else {
      /**
       * WARNING: don't push back your workspace secrets to github
       */
      return getTestWorkspaceBuilder(DEFAULT_TEST_PROPERTIES);
    }
  }

  public static Workspace.Builder getSystemPropertiesWorkspaceBuilder() {
    logger.debug("loading test Workspace from JVM System Properties");
    PrivateKey privateKey = new PrivateKeyBuilder().encodedPkcs8Key(
        System.getProperty(PrivateKeyBuilder.AIO_ENCODED_PKCS_8)).build();
    return Workspace.builder()
        .properties(System.getProperties())
        .privateKey(privateKey);
  }

  private static Workspace.Builder getTestWorkspaceBuilder(String propertyFileClassPath) {
    try {
      logger.debug("loading test Workspace from classpath {}", propertyFileClassPath);
      Properties prop = FileUtil.readPropertiesFromClassPath(propertyFileClassPath);
      PrivateKey privateKey = new PrivateKeyBuilder().properties(prop).build();
      return Workspace.builder()
          .properties(prop)
          .privateKey(privateKey);
    } catch (IOException e) {
      throw new AIOException(
          "Unable to load your Workspace from class path " + propertyFileClassPath,
          e);
    }
  }

  public static String getTestProperty(String key) {
    try {
      String value = System.getProperty(key);
      if (StringUtils.isBlank(value)) {
        value = FileUtil.readPropertiesFromClassPath(DEFAULT_TEST_PROPERTIES).getProperty(key);
      }
      return value;
    } catch (IOException e) {
      throw new AIOException(
          "Unable to load Property: " + key
              + "from class path Properties: " + DEFAULT_TEST_PROPERTIES, e);
    }
  }

}
