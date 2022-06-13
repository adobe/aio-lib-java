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
import java.util.Map;
import java.util.Properties;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestUtil {

  public static final String DEFAULT_TEST_PROPERTIES = "workspace.secret.properties";
  private static final Logger logger = LoggerFactory.getLogger(TestUtil.class);

  private TestUtil() {
  }

  public static Workspace getDefaultTestWorkspace() {
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
      return getSystemPropertiesWorkspace();
    } else {
      /**
       * WARNING: don't push back your workspace secrets to github
       */
      return getTestWorkspace(DEFAULT_TEST_PROPERTIES);
    }
  }

  public static Workspace getSystemPropertiesWorkspace() {
    logger.info("loading test Workspace from JVM System Properties");
    PrivateKey privateKey = new PrivateKeyBuilder().encodedPkcs8Key(System.getProperty(PrivateKeyBuilder.AIO_ENCODED_PKCS_8)).build();
    return Workspace.builder()
        .properties(System.getProperties())
        .privateKey(privateKey)
        .build();
  }

  public static Workspace getTestWorkspace(String propertyFileClassPath) {
    try {
      logger.info("loading test Workspace from classpath {}",propertyFileClassPath);
      Properties prop = FileUtil.readPropertiesFromClassPath(DEFAULT_TEST_PROPERTIES);
      PrivateKey privateKey = new PrivateKeyBuilder().properties(prop).build();
      return Workspace.builder()
          .properties(prop)
          .privateKey(privateKey)
          .build();
    } catch (IOException e) {
      throw new AIOException(
          "Unable to load your Workspace from class path " + propertyFileClassPath,
          e);
    }
  }

}
