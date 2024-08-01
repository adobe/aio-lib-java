/*
 * Copyright 2023 Adobe. All rights reserved.
 * This file is licensed to you under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License. You may obtain a copy
 * of the License at http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under
 * the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR REPRESENTATIONS
 * OF ANY KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package com.adobe.aio.workspace;

import static com.adobe.aio.util.FileUtil.getMapFromProperties;
import static com.adobe.aio.util.FileUtil.readPropertiesFromClassPath;
import static com.adobe.aio.util.FileUtil.readPropertiesFromFile;

import com.adobe.aio.auth.Context;
import com.adobe.aio.auth.JwtContext;
import com.adobe.aio.auth.OAuthContext;
import com.adobe.aio.util.Constants;
import java.security.PrivateKey;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import org.apache.commons.lang3.StringUtils;

public class Workspace {

  public static final String IMS_URL = "aio_ims_url";
  public static final String IMS_ORG_ID = "aio_ims_org_id";
  public static final String CONSUMER_ORG_ID = "aio_consumer_org_id";
  public static final String PROJECT_ID = "aio_project_id";
  public static final String WORKSPACE_ID = "aio_workspace_id";
  public static final String API_KEY = "aio_api_key";
  /**
   * @deprecated This will be removed in v2.0 of the library.
   */
  @Deprecated
  public static final String CREDENTIAL_ID = "aio_credential_id";
  /**
   * @deprecated This will be removed in v2.0 of the library.
   */
  @Deprecated
  public static final String CLIENT_SECRET = "aio_client_secret";
  /**
   * @deprecated This will be removed in v2.0 of the library.
   */
  @Deprecated
  public static final String TECHNICAL_ACCOUNT_ID = "aio_technical_account_id";
  /**
   * @deprecated This will be removed in v2.0 of the library.
   */
  @Deprecated
  public static final String META_SCOPES = "aio_meta_scopes";

  public static final String OAUTH_SCOPES = "aio_oauth_scopes";

  // workspace context related:
  private final String imsUrl;
  private final String imsOrgId;
  private final String apiKey;
  private final String consumerOrgId;
  private final String projectId;
  private final String workspaceId;
  private final Context authContext;

  private static boolean oAuthContext = false;

  private Workspace(final String imsUrl, final String imsOrgId, final String apiKey,
                    final String consumerOrgId, final String projectId, final String workspaceId,
                    Context authContext) {
    this.imsUrl = StringUtils.isEmpty(imsUrl) ? Constants.IMS_URL : imsUrl;
    this.imsOrgId = imsOrgId;

    this.apiKey = apiKey;
    this.consumerOrgId = consumerOrgId;
    this.projectId = projectId;
    this.workspaceId = workspaceId;
    this.authContext = authContext;
  }

  public static Builder builder() {
    return new Builder();
  }

  public void validateAll() {
    authContext.validate();
    validateWorkspaceContext();
  }

  /**
   * Validates that this workspace context is populated.
   *
   * @throws IllegalStateException if any properties are not specified.
   */
  public void validateWorkspaceContext() throws IllegalStateException {
    if (StringUtils.isEmpty(imsOrgId)) {
      throw new IllegalStateException("Your `Workspace` is missing an imsOrgId");
    }
    if (StringUtils.isEmpty(this.getConsumerOrgId())) {
      throw new IllegalStateException("Your `Workspace` is missing a consumerOrgId");
    }
    if (StringUtils.isEmpty(apiKey)) {
      throw new IllegalStateException("Your `Workspace` is missing an apiKey");
    }
    if (StringUtils.isEmpty(this.getProjectId())) {
      throw new IllegalStateException("Your `Workspace` is missing a projectId");
    }
    if (StringUtils.isEmpty(this.getWorkspaceId())) {
      throw new IllegalStateException("Your `Workspace` is missing a workspaceId");
    }
  }

  public String getProjectUrl() {
    if (!StringUtils.isEmpty(this.getConsumerOrgId()) && !StringUtils.isEmpty(
        this.getProjectId())) {
      return "https://developer.adobe.com/console/projects/" + this.getConsumerOrgId() +
          "/" + this.getProjectId() + "/overview";
    } else {
      return null;
    }
  }

  public String getImsUrl() {
    return imsUrl;
  }

  public String getImsOrgId() {
    return imsOrgId;
  }

  public String getApiKey() {
    return apiKey;
  }

  public String getConsumerOrgId() {
    return consumerOrgId;
  }

  public String getProjectId() {
    return projectId;
  }

  public String getWorkspaceId() {
    return workspaceId;
  }

  public Context getAuthContext() {
    return authContext;
  }

  /**
   * @deprecated This will be removed in v2.0 of the library.
   */
  @Deprecated
  public String getCredentialId() {
    if (authContext instanceof JwtContext) {
      return ((JwtContext) authContext).getCredentialId();
    } else {
      return null;
    }
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    Workspace workspace = (Workspace) o;
    return Objects.equals(imsUrl, workspace.imsUrl) && Objects.equals(imsOrgId, workspace.imsOrgId)
        && Objects.equals(apiKey, workspace.apiKey) && Objects.equals(consumerOrgId,
        workspace.consumerOrgId) && Objects.equals(projectId, workspace.projectId)
        && Objects.equals(workspaceId, workspace.workspaceId)
        && Objects.equals(authContext, workspace.authContext);
  }

  @Override
  public int hashCode() {
    return Objects.hash(imsUrl, imsOrgId, apiKey, consumerOrgId, projectId, workspaceId, authContext);
  }

  @Override
  public String toString() {
    return "Workspace{" +
        "imsUrl='" + imsUrl + '\'' +
        ", imsOrgId='" + imsOrgId + '\'' +
        ", apiKey='" + apiKey + '\'' +
        ", consumerOrgId='" + consumerOrgId + '\'' +
        ", projectId='" + projectId + '\'' +
        ", workspaceId='" + workspaceId + '\'' +
        ", authContext=" + authContext +
        '}';
  }

  public static class Builder {

    private String imsUrl;
    private String imsOrgId;
    private String apiKey;
    private String consumerOrgId;
    private String projectId;
    private String workspaceId;

    private Map<String, String> workspaceProperties;

    private JwtContext.Builder jwtbuilder;
    private Context authContext;
    private OAuthContext.Builder oAuthBuilder;

    private Builder() {
    }

    public Builder imsUrl(final String imsUrl) {
      this.imsUrl = imsUrl;
      return this;
    }

    public Builder imsOrgId(final String imsOrgId) {
      this.imsOrgId = imsOrgId;
      return this;
    }

    public Builder apiKey(final String apiKey) {
      this.apiKey = apiKey;
      return this;
    }

    public Builder consumerOrgId(final String consumerOrgId) {
      this.consumerOrgId = consumerOrgId;
      return this;
    }

    public Builder projectId(final String projectId) {
      this.projectId = projectId;
      return this;
    }

    public Builder workspaceId(final String workspaceId) {
      this.workspaceId = workspaceId;
      return this;
    }

    public Builder authContext(final Context authContext) {
      this.authContext = authContext;
      return this;
    }

    public Builder credentialId(final String credentialId) {
      if (jwtbuilder == null) {
        jwtbuilder = JwtContext.builder();
      }
      jwtbuilder.credentialId(credentialId);
      return this;
    }

    public Builder clientSecret(final String clientSecret) {
      if (jwtbuilder == null) {
        jwtbuilder = JwtContext.builder();
      }
      jwtbuilder.clientSecret(clientSecret);
      if(oAuthBuilder == null){
        oAuthBuilder = OAuthContext.builder();
      }
      oAuthBuilder.clientSecret(clientSecret);
      return this;
    }

    public Builder technicalAccountId(final String technicalAccountId) {
      if (jwtbuilder == null) {
        jwtbuilder = JwtContext.builder();
      }
      jwtbuilder.technicalAccountId(technicalAccountId);
      return this;
    }

    public Builder addMetascope(final String metascope) {
      if (jwtbuilder == null) {
        jwtbuilder = JwtContext.builder();
      }
      jwtbuilder.addMetascope(metascope);
      if(oAuthBuilder == null){
        oAuthBuilder = OAuthContext.builder();
      }
      oAuthBuilder.addScope(metascope);
      return this;
    }

    public Builder privateKey(final PrivateKey privateKey) {
      if (jwtbuilder == null) {
        jwtbuilder = JwtContext.builder();
      }
      jwtbuilder.privateKey(privateKey);
      return this;
    }

    public Builder configMap(final Map<String, String> configMap) {
      this
          .imsUrl(configMap.get(IMS_URL))
          .imsOrgId(configMap.get(IMS_ORG_ID))
          .apiKey(configMap.get(API_KEY))
          .consumerOrgId(configMap.get(CONSUMER_ORG_ID))
          .projectId(configMap.get(PROJECT_ID))
          .workspaceId(configMap.get(WORKSPACE_ID));
      // For backwards compatibility - should this be kept?
      if(StringUtils.isBlank(configMap.get(OAUTH_SCOPES))) {
        jwtbuilder = JwtContext.builder();
        jwtbuilder.configMap(configMap);
        oAuthContext = false;
      } else {
        oAuthBuilder = OAuthContext.builder();
        oAuthBuilder.configMap(configMap);
        oAuthContext = true;
      }
      return this;
    }

    public Builder systemEnv() {
      return configMap(System.getenv());
    }

    public Builder propertiesPath(final String propertiesPath) {
      return properties(
          readPropertiesFromFile(propertiesPath)
              .orElse(readPropertiesFromClassPath(propertiesPath)));
    }

    public Builder properties(final Properties properties) {
      return configMap(getMapFromProperties(properties));
    }

    public Workspace build() {
      if (authContext != null) {
        return new Workspace(imsUrl, imsOrgId, apiKey, consumerOrgId, projectId, workspaceId, authContext);
      }
      if(oAuthBuilder == null) {
        oAuthBuilder = OAuthContext.builder();
      }
      if (jwtbuilder == null) {
        jwtbuilder = JwtContext.builder();
      }
      if(oAuthContext){
        return new Workspace(imsUrl, imsOrgId, apiKey, consumerOrgId, projectId, workspaceId, oAuthBuilder.build());
      } else {
        return new Workspace(imsUrl, imsOrgId, apiKey, consumerOrgId, projectId, workspaceId, jwtbuilder.build());
      }
    }
  }
}
