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

import com.adobe.aio.auth.Context;
import com.adobe.aio.auth.JwtContext;
import com.adobe.aio.auth.OAuthContext;
import com.adobe.aio.ims.util.PrivateKeyBuilder;
import com.adobe.aio.workspace.Workspace;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.PrivateKey;
import java.util.*;

import static com.adobe.aio.auth.Context.CLIENT_SECRET;
import static com.adobe.aio.auth.JwtContext.*;
import static com.adobe.aio.auth.OAuthContext.SCOPES;
import static com.adobe.aio.util.FileUtil.getMap;
import static com.adobe.aio.workspace.Workspace.*;

public class WorkspaceUtil {

    public static final String API_URL = "aio_api_url";
    public static final String PUBLISH_URL = "aio_publish_url";

    /**
     * Default workspace configuration file class path
     * WARNING: don't push back this file to github as it contains many secrets.
     * We do provide a sample properties files in the
     * <code>./src/test/resources</code> folder
     */
    public static final String DEFAULT_TEST_PROPERTIES = "workspace.jwt.secret.properties";

    private static final Logger logger = LoggerFactory.getLogger(WorkspaceUtil.class);

    private WorkspaceUtil() {
    }

    /**
     * Loads Workspace from either one and only one of the following
     * sources, probing them first to check that all the required properties are given,
     * in order:
     * <ol>
     *   <li>System Properties</li>
     *   <li>Environment Variables</li>
     *   <li>classpath:{@link WorkspaceUtil#DEFAULT_TEST_PROPERTIES}</li>
     * </ol>
     *
     * @return a Workspace.Builder loaded with the provided config
     */
    public static Workspace.Builder getSystemWorkspaceBuilder() {
        return getWorkspaceBuilder(getSystemWorkspaceConfig(DEFAULT_TEST_PROPERTIES));
    }

    public static Workspace.Builder getWorkspaceBuilder(Map<String, String> configMap) {
        Workspace.Builder builder =
                Workspace.builder().imsUrl(configMap.get(IMS_URL))
                        .imsOrgId(configMap.get(IMS_ORG_ID))
                        .apiKey(configMap.get(API_KEY))
                        .consumerOrgId(configMap.get(CONSUMER_ORG_ID))
                        .projectId(configMap.get(PROJECT_ID))
                        .workspaceId(configMap.get(WORKSPACE_ID));
        builder.authContext(getAuthContext(configMap));
        return builder;
    }

    public static boolean isOAuthConfig(Map<String, String> configMap) {
        return configMap.containsKey(SCOPES);
    }

    public static Context getAuthContext(Map<String, String> configMap) {
        if (isOAuthConfig(configMap)) {
            return getOAuthContextBuilder(configMap).build();
        } else {
            return getJwtContextBuilder(configMap).build();
        }
    }

    public static OAuthContext.Builder getOAuthContextBuilder(Map<String, String> configMap) {
        OAuthContext.Builder builder = new OAuthContext.Builder();
        builder.clientSecret(configMap.get(CLIENT_SECRET));
        if (!StringUtils.isEmpty(configMap.get(SCOPES))) {
            Arrays.stream(configMap.get(SCOPES).split(",")).forEach(builder::addScope);
        }
        return builder;
    }


    public static JwtContext.Builder getJwtContextBuilder(Map<String, String> configMap) {
        JwtContext.Builder builder = new JwtContext.Builder()
                .credentialId(configMap.get(CREDENTIAL_ID))
                .clientSecret(configMap.get(CLIENT_SECRET))
                .technicalAccountId(configMap.get(TECHNICAL_ACCOUNT_ID));
        if (!StringUtils.isEmpty(configMap.get(META_SCOPES))) {
            String[] metascopeArray = configMap.get(META_SCOPES).split(",");
            for (String metascope : metascopeArray) {
                builder.addMetascope(metascope);
            }
        }
        getPrivateKey(configMap).ifPresent(builder::privateKey);
        return builder;
    }

    public static Optional<PrivateKey> getPrivateKey(Map<String, String> configMap) {
        String encodedPkcs8Key = configMap.get(PrivateKeyBuilder.AIO_ENCODED_PKCS_8);
        if (encodedPkcs8Key != null) {
            logger.debug("loading test JWT Private Key from JVM System Properties");
            try {
                return Optional.of(new PrivateKeyBuilder().encodedPkcs8Key(encodedPkcs8Key).build());
            } catch (Exception e) {
                logger.error("Error {} loading test Private Key from configMap", e.getMessage());
                return Optional.empty();
            }
        } else {
            return Optional.empty();
        }
    }

    public static String getSystemProperty(String key) {
        return getSystemProperty(key, DEFAULT_TEST_PROPERTIES);
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
     * @param key               the property name
     * @param propertyClassPath the classpath of the property file
     * @return the value of the property
     */
    public static String getSystemProperty(String key, String propertyClassPath) {
        return getSystemProperties(Arrays.asList(key), propertyClassPath).get(key);
    }

    public static Map<String, String> getSystemWorkspaceConfig(String propertiesClassPath) {
        return getSystemProperties(Arrays.asList(API_KEY,
                Workspace.WORKSPACE_ID,
                CLIENT_SECRET,
                Workspace.CONSUMER_ORG_ID,
                IMS_ORG_ID,
                Workspace.PROJECT_ID), propertiesClassPath);
    }

    /**
     * Loads configurations from either one and only one of the following
     * sources, probing them first to check that all the required properties are given,
     * in order:
     * <ol>
     *   <li>JVM System Properties</li>
     *   <li>Environment Variables</li>
     *   <li>the provided propertiesClassPath</li>
     * </ol>
     *
     * @param propertiesClassPath the classpath of the properties file
     * @param keys                the list of keys to look up
     * @return Map of all the properties
     */
    private static Map<String, String> getSystemProperties(List<String> keys, String propertiesClassPath) {
        if (StringUtils.isNoneBlank(keys.stream().map(System::getProperty).toArray(String[]::new))) {
            logger.debug("loading `{}` from JVM System Properties", keys);
            return getMap(System.getProperties());
        }
        if (StringUtils.isNoneBlank(keys.stream().map(System::getenv).toArray(String[]::new))) {
            logger.debug("loading `{}` from Environment Variables", keys);
            return System.getenv();
        } else {
            if (WorkspaceUtil.class.getClassLoader().getResourceAsStream(propertiesClassPath) == null) {
                logger.error("No system configuration found for keys `{}`, no properties file either at `{}`",
                        keys, propertiesClassPath);
                return Collections.emptyMap();
            } else {
                logger.debug("loading `{}` from classpath `{}`", keys, propertiesClassPath);
                Map<String, String> map = getMap(propertiesClassPath);
                if (StringUtils.isNoneBlank(keys.stream().map(map::get).toArray(String[]::new))) {
                    return map;
                } else {
                    logger.error("Missing configurations: keys: `{}` classpath: `{}`", keys, propertiesClassPath);
                    return Collections.emptyMap();
                }
            }
        }
    }


}
