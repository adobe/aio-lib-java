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

import static com.adobe.util.FileUtil.readPropertiesFromClassPath;
import static com.adobe.util.FileUtil.readPropertiesFromFile;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

public class JwtTokenBuilder {

  private final Map<String, Object> claims = new HashMap<String, Object>();
  private final PrivateKey key;
  private final Properties prop;

  private static final String ISS = "iss";
  private static final String EXP = "exp";
  private static final String SUB = "sub";
  private static final String AUD = "aud";
  private static final String IAT = "iat";

  private static final String AUD_SUFFIX = "/c/";

  private static final String IMS_URL = "imsUrl";
  private static final String META_SCOPES = "metascopes";
  private static final String ORG_ID = "organizationId";
  private static final String TECH_ID = "technicalAccountId";
  private static final String API_KEY = "apiKey";
  private static final String CLIENT_SECRET = "clientSecret";

  private static final String KEYSTORE_TYPE = "PKCS12";

  private static final String P12_FILE_PATH = "p12FilePath";
  private static final String P12_PASSWORD = "p12Password";
  private static final String P12_ALIAS = "p12Alias";

  private static final String DEFAULT_PROPERTIES = "ims.secret.properties";

  public static JwtTokenBuilder build(final String configFilePath)
      throws IOException, KeyStoreException, CertificateException, NoSuchAlgorithmException, UnrecoverableKeyException {
    return build(readPropertiesFromFile(configFilePath).orElse(readPropertiesFromClassPath(DEFAULT_PROPERTIES)));
  }

  public static JwtTokenBuilder build(final Properties prop)
      throws UnrecoverableKeyException, CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException {
    //todo validate prop
    return new JwtTokenBuilder(prop);
  }

  private JwtTokenBuilder(final Properties prop)
      throws IOException, KeyStoreException, CertificateException, NoSuchAlgorithmException, UnrecoverableKeyException {
    this.prop = prop;
    claims.put(ISS, prop.getProperty(ORG_ID));
    claims.put(SUB, prop.getProperty(TECH_ID));
    claims.put(AUD, prop.getProperty(IMS_URL) + AUD_SUFFIX + prop.getProperty(API_KEY));

    String[] metascopes = prop.get(META_SCOPES).toString().split(",");
    for (String metascope : metascopes) {
      claims.put(prop.getProperty(IMS_URL) + metascope, true);
    }

    long iat = System.currentTimeMillis() / 1000L;
    claims.put(IAT, iat);
    claims.put(EXP, iat + 180L);

    KeyStore keystore = KeyStore.getInstance(KEYSTORE_TYPE);
    String password = prop.getProperty(P12_PASSWORD);
    File p12File = new File(prop.getProperty(P12_FILE_PATH));

    keystore.load(new FileInputStream(p12File), password.toCharArray());
    this.key = (PrivateKey) keystore.getKey(prop.getProperty(P12_ALIAS), password.toCharArray());
  }

  public String getJwtToken() {
    String jwt = Jwts.builder().setClaims(claims).signWith(SignatureAlgorithm.RS256, key).compact();
    return jwt;
  }

  public String getApiKey() {
    return prop.getProperty(API_KEY);
  }

  public String getImsUrl() {
    return prop.getProperty(IMS_URL);
  }

  public String getClientSecret() {
    return prop.getProperty(CLIENT_SECRET);
  }


}