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

import com.adobe.aio.ims.util.PrivateKeyBuilder;
import com.adobe.aio.workspace.Workspace;
import java.security.PrivateKey;
import java.util.Properties;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WorkspaceUtil {

  public static final String API_URL = "aio_api_url";
  public static final String PUBLISH_URL = "aio_publish_url";
  public static final String DEFAULT_TEST_PROPERTIES = "workspace.secret.properties";
  private static final Logger logger = LoggerFactory.getLogger(WorkspaceUtil.class);

  private WorkspaceUtil() {
  }

  /**
   * Loads configurations for a Workspace from either one and only one of the following
   * sources, probing them first to check that all the required properties are given,
   * in order:
   * <ol>
   *   <li>System Properties</li>
   *   <li>Environment Variables</li>
   *   <li>classpath:{@link WorkspaceUtil#DEFAULT_TEST_PROPERTIES}</li>
   * </ol>
   * @return a Workspace.Builder loaded with the provided config
   */
  public static Workspace.Builder getSystemWorkspaceBuilder() {
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
      logger.debug("loading test Workspace from JVM System Properties");
      PrivateKey privateKey = new PrivateKeyBuilder().encodedPkcs8Key(
          System.getProperty(PrivateKeyBuilder.AIO_ENCODED_PKCS_8)).build();
      return Workspace.builder()
          .properties(System.getProperties())
          .privateKey(privateKey);
    } else if (StringUtils.isNoneBlank(
        System.getenv(PrivateKeyBuilder.AIO_ENCODED_PKCS_8),
        System.getenv(Workspace.API_KEY),
        System.getenv(Workspace.WORKSPACE_ID),
        System.getenv(Workspace.CLIENT_SECRET),
        System.getenv(Workspace.CONSUMER_ORG_ID),
        System.getenv(Workspace.CREDENTIAL_ID),
        System.getenv(Workspace.IMS_ORG_ID),
        System.getenv(Workspace.META_SCOPES),
        System.getenv(Workspace.PROJECT_ID),
        System.getenv(Workspace.TECHNICAL_ACCOUNT_ID))) {
      logger.debug("loading test Workspace from JVM System Properties");
      PrivateKey privateKey =
          new PrivateKeyBuilder()
              .encodedPkcs8Key(System.getenv(PrivateKeyBuilder.AIO_ENCODED_PKCS_8))
              .build();
      return Workspace.builder().systemEnv().privateKey(privateKey);
    } else {
      /**
       * WARNING: don't push back your workspace secrets to github
       */
      logger.debug("loading test Workspace from classpath {}", DEFAULT_TEST_PROPERTIES);
      return getWorkspaceBuilder(DEFAULT_TEST_PROPERTIES);
    }
  }

  public static String getSystemProperty(String key) {
    return getSystemProperty(key,DEFAULT_TEST_PROPERTIES);
  }

  /**
   * Loads a property from either one of the following sources, probing it first to
   * check that the required property is given, in order:
   * <ol>
   *   <li>System Properties</li>
   *   <li>Environment Variables</li>
   *   <li>classpath:{@code propertyClassPath}</li>
   * </ol>
   *
   * @param key the property name
   * @param propertyClassPath the classpath of the property file
   * @return the value of the property
   */
  public static String getSystemProperty(String key, String propertyClassPath) {
    if (StringUtils.isNotBlank(System.getProperty(key))) {
      logger.debug("loading property `{}`from JVM System Properties", key);
      return System.getProperty(key);
    } if (StringUtils.isNotBlank(System.getenv(key))) {
      logger.debug("loading property `{}` from Environment Variables", key);
      return System.getenv(key);
    } else {
      logger.debug("loading property `{}` from classpath `{}`", key, propertyClassPath);
      return FileUtil.readPropertiesFromClassPath(propertyClassPath).getProperty(key);
    }
  }

  private static Workspace.Builder getWorkspaceBuilder(String propertyFileClassPath) {
    Properties prop = FileUtil.readPropertiesFromClassPath(propertyFileClassPath);
    PrivateKey privateKey = new PrivateKeyBuilder().properties(prop).build();
    return Workspace.builder()
        .properties(prop)
        .privateKey(privateKey);
  }

}
