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
package com.adobe;

import static com.adobe.util.FileUtil.getMapFromProperties;
import static com.adobe.util.FileUtil.readPropertiesFromClassPath;
import static com.adobe.util.FileUtil.readPropertiesFromFile;

import com.adobe.util.Constants;
import java.io.IOException;
import java.security.PrivateKey;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;

public class Workspace {

  static final String IMS_URL = "ims_url";
  static final String IMS_ORG_ID = "ims_org_id";
  static final String CONSUMER_ORG_ID = "consumer_org_id";
  static final String API_KEY = "api_key";
  static final String CREDENTIAL_ID = "credential_id";
  static final String CLIENT_SECRET = "client_secret";
  static final String TECHNICAL_ACCOUNT_ID = "technical_account_id";
  static final String META_SCOPES = "meta_scopes";

  private final String imsUrl;
  private final String imsOrgId;
  private final String consumerOrgId;
  private final String apiKey;
  private final String credentialId;
  private final String clientSecret;
  private final String technicalAccountId;
  private final Set<String> metascopes;
  private final PrivateKey privateKey;

  private Workspace(final String imsUrl, final String imsOrgId, final String consumerOrgId,
      final String apiKey, final String credentialId, final String clientSecret,
      final String technicalAccountId,
      final Set<String> metascopes, final PrivateKey privateKey) {
    this.imsUrl = StringUtils.isEmpty(imsUrl) ? Constants.IMS_URL : imsUrl;
    this.imsOrgId = imsOrgId;
    this.consumerOrgId = consumerOrgId;
    this.apiKey = apiKey;
    this.credentialId = credentialId;
    this.clientSecret = clientSecret;
    this.technicalAccountId = technicalAccountId;
    this.metascopes = metascopes;
    this.privateKey = privateKey;
  }

  public void validateJwtCredentialConfig() {
    if (StringUtils.isEmpty(apiKey)) {
      throw new IllegalArgumentException("Your `Worskpace` is missing an apiKey");
    }
    if (StringUtils.isEmpty(clientSecret)) {
      throw new IllegalArgumentException("Your `Worskpace` is missing a clientSecret");
    }
    if (StringUtils.isEmpty(imsOrgId)) {
      throw new IllegalArgumentException("Your `Worskpace` is missing an imsOrgId");
    }
    if (StringUtils.isEmpty(technicalAccountId)) {
      throw new IllegalArgumentException("Your `Worskpace` is missing a technicalAccountId");
    }
    if (metascopes.isEmpty()) {
      throw new IllegalArgumentException("Your `Worskpace` should contain at least one metascope");
    }
    if (privateKey == null) {
      throw new IllegalArgumentException("Your `Worskpace` should contain a privateKey");
    }
  }

  public String getImsUrl() {
    return imsUrl;
  }

  public String getImsOrgId() {
    return imsOrgId;
  }

  public String getConsumerOrgId() {
    return consumerOrgId;
  }

  public String getApiKey() {
    return apiKey;
  }

  public String getCredentialId() {
    return credentialId;
  }

  public String getClientSecret() {
    return clientSecret;
  }

  public String getTechnicalAccountId() {
    return technicalAccountId;
  }

  public Set<String> getMetascopes() {
    return metascopes;
  }

  public PrivateKey getPrivateKey() {
    return privateKey;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Workspace workspace = (Workspace) o;
    return Objects.equals(imsUrl, workspace.imsUrl) &&
        Objects.equals(imsOrgId, workspace.imsOrgId) &&
        Objects.equals(consumerOrgId, workspace.consumerOrgId) &&
        Objects.equals(apiKey, workspace.apiKey) &&
        Objects.equals(credentialId, workspace.credentialId) &&
        Objects.equals(clientSecret, workspace.clientSecret) &&
        Objects.equals(technicalAccountId, workspace.technicalAccountId) &&
        Objects.equals(metascopes, workspace.metascopes) &&
        Objects.equals(privateKey, workspace.privateKey);
  }

  @Override
  public int hashCode() {
    return Objects
        .hash(imsUrl, imsOrgId, consumerOrgId, apiKey, credentialId, clientSecret,
            technicalAccountId, metascopes,
            privateKey);
  }

  @Override
  public String toString() {
    return "Workspace{" +
        "imsUrl='" + imsUrl + '\'' +
        ", imsOrgId='" + imsOrgId + '\'' +
        ", consumerOrgId='" + consumerOrgId + '\'' +
        ", apiKey='" + apiKey + '\'' +
        ", credentialId='" + credentialId + '\'' +
        ", clientSecret='" + clientSecret + '\'' +
        ", technicalAccountId='" + technicalAccountId + '\'' +
        ", metascopes=" + metascopes +
        '}';
  }

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {

    private String imsUrl;
    private String imsOrgId;
    private String consumerOrgId;
    private String apiKey;
    private String credentialId;
    private String clientSecret;
    private String technicalAccountId;
    private final Set<String> metascopes = new HashSet<>();
    private PrivateKey privateKey;

    private Map<String, String> workspaceProperties;

    private Builder() {
    }

    public Builder imsUrl(String imsUrl) {
      this.imsUrl = imsUrl;
      return this;
    }

    public Builder imsOrgId(String imsOrgId) {
      this.imsOrgId = imsOrgId;
      return this;
    }

    public Builder consumerOrgId(String consumerOrgId) {
      this.consumerOrgId = consumerOrgId;
      return this;
    }

    public Builder apiKey(String apiKey) {
      this.apiKey = apiKey;
      return this;
    }

    public Builder credentialId(String credentialId) {
      this.credentialId = credentialId;
      return this;
    }

    public Builder clientSecret(String clientSecret) {
      this.clientSecret = clientSecret;
      return this;
    }

    public Builder technicalAccountId(String technicalAccountId) {
      this.technicalAccountId = technicalAccountId;
      return this;
    }

    public Builder addMetascope(String metascope) {
      this.metascopes.add(metascope);
      return this;
    }

    public Builder privateKey(PrivateKey privateKey) {
      this.privateKey = privateKey;
      return this;
    }

    public Builder configMap(Map<String, String> configMap) {
      this
          .imsUrl(configMap.get(IMS_URL))
          .imsOrgId(configMap.get(IMS_ORG_ID))
          .consumerOrgId(configMap.get(CONSUMER_ORG_ID))
          .apiKey(configMap.get(API_KEY))
          .credentialId(configMap.get(CREDENTIAL_ID))
          .clientSecret(configMap.get(CLIENT_SECRET))
          .technicalAccountId(configMap.get(TECHNICAL_ACCOUNT_ID));
      if (!StringUtils.isEmpty(configMap.get(META_SCOPES))) {
        String[] metascopeArray = configMap.get(META_SCOPES).split(",");
        for (String metascope : metascopeArray) {
          this.addMetascope(metascope);
        }
      }
      return this;
    }

    public Builder systemEnv() {
      return configMap(System.getenv());
    }

    public Builder propertiesPath(String propertiesPath) throws IOException {
      return properties(
          readPropertiesFromFile(propertiesPath)
              .orElse(readPropertiesFromClassPath(propertiesPath)));
    }

    public Builder properties(Properties properties) throws IOException {
      return configMap(getMapFromProperties(properties));
    }

    public Workspace build() {
      return new Workspace(imsUrl, imsOrgId, consumerOrgId,
          apiKey, credentialId, clientSecret, technicalAccountId,
          metascopes, privateKey);
    }
  }
}
