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
package com.adobe.ims;

import static com.adobe.util.FileUtil.getMapFromProperties;
import static com.adobe.util.FileUtil.readPropertiesFromClassPath;
import static com.adobe.util.FileUtil.readPropertiesFromFile;

import com.adobe.util.KeyStoreUtil;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.io.IOException;
import java.security.PrivateKey;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class JwtTokenBuilder {

  private final Map<String, Object> claims;
  private final PrivateKey privateKey;
  private final String apiKey;
  private final String imsUrl;
  private final String clientSecret;

  private static final String ISS = "iss";
  private static final String EXP = "exp";
  private static final String SUB = "sub";
  private static final String AUD = "aud";
  private static final String IAT = "iat";

  private static final String AUD_SUFFIX = "/c/";

  private static final String IMS_URL = "ims_url";
  private static final String META_SCOPES = "meta_scopes";
  private static final String ORG_ID = "ims_organization_id";
  private static final String TECH_ID = "technical_account_id";
  private static final String API_KEY = "api_key";
  private static final String CLIENT_SECRET = "client_secret";

  private static final String ENCODE_PKCS8_KEY = "encoded_pkcs8";
  private static final String PKCS8_FILE_PATH = "pkcs8_file_path";

  private static final String PKCS12_FILE_PATH = "pkcs12_file_path";
  private static final String PKCS12_PASSWORD = "pkcs12_password";
  private static final String PKCS12_ALIAS = "pkcs12_alias";

  /**
   * @param configPath will be used first to look on the file system and if not found in the
   *                       class path
   * @return a valid JwtTokenBuilder
   * @throws IOException if the file is not found
   */
  public static JwtTokenBuilder build(final String configPath) throws IOException {
    return build(readPropertiesFromFile(configPath)
        .orElse(readPropertiesFromClassPath(configPath)));
  }

  public static JwtTokenBuilder build(final Properties prop) {
    return build(getMapFromProperties(prop));
  }

  /**
   * @return a JwtTokenBuilder build using System.getenv();
   */
  public static JwtTokenBuilder build() {
    return build(System.getenv());
  }

  public static JwtTokenBuilder build(final Map<String, String> imsConfig) {
    return new JwtTokenBuilder(imsConfig);
  }

  private JwtTokenBuilder(final Map<String, String> imsConfig) {
    Arrays.asList(IMS_URL, API_KEY, CLIENT_SECRET, ORG_ID, TECH_ID, META_SCOPES).stream().
        forEach(expectedEntry -> validate(imsConfig, expectedEntry));
    this.claims = getClaims(imsConfig);
    this.privateKey = getPrivateKey(imsConfig);
    this.imsUrl = imsConfig.get(IMS_URL);
    this.apiKey = imsConfig.get(API_KEY);
    this.clientSecret = imsConfig.get(CLIENT_SECRET);
  }

  private static void validate(final Map<String, String> imsConfig, String key) {
    if (!imsConfig.containsKey(key)) {
      throw new IllegalArgumentException("JwtTokenBuilder configuration is missing " + key);
    }
  }

  private static Map<String, Object> getClaims(final Map<String, String> imsConfig) {
    Map<String, Object> claims = new HashMap<String, Object>();
    claims.put(ISS, imsConfig.get(ORG_ID));
    claims.put(SUB, imsConfig.get(TECH_ID));
    claims.put(AUD, imsConfig.get(IMS_URL) + AUD_SUFFIX + imsConfig.get(API_KEY));

    String[] metascopes = imsConfig.get(META_SCOPES).split(",");
    for (String metascope : metascopes) {
      claims.put(imsConfig.get(IMS_URL) + metascope, true);
    }

    long iat = System.currentTimeMillis() / 1000L;
    claims.put(IAT, iat);
    claims.put(EXP, iat + 180L);
    return claims;
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
            "JwtTokenBuilder is missing a valid (pkcs8 or pkcs12) Private Key configuration");
      }
    } catch (Exception e) {
      throw new RuntimeException(
          "Invalid JwtTokenBuilder (pkcs8 or pkcs12) Private Key configuration. "
              + "" + e.getMessage(), e);
    }
  }

  public String getJwtToken() {
    String jwt = Jwts.builder().setClaims(claims).signWith(SignatureAlgorithm.RS256, privateKey)
        .compact();
    return jwt;
  }

  public String getApiKey() {
    return apiKey;
  }

  public String getImsUrl() {
    return imsUrl;
  }

  public String getClientSecret() {
    return clientSecret;
  }

}