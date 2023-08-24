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
package com.adobe.aio.auth;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;

import static com.adobe.aio.util.FileUtil.*;

/**
 * Represents an OAuth Client Credentials authentication context.
 * Reference:  <a href="https://aaronparecki.com/oauth-2-simplified/#client-credentials">OAuth Client Credentials</a>
 */
public class OAuthContext implements Context {
  /**
   * Property name for looking up Authentication Client Secret in various contexts.
   * Reference: <a href="https://developer.adobe.com/developer-console/docs/guides/authentication/ServerToServerAuthentication/#server-to-server-credential-types">AIO Developer Documentation</a>
   */
  public static final String CLIENT_SECRET = "aio_client_secret";
  /**
   * Property name for looking up Authentication Scopes in various contexts.
   * Reference: <a href="https://developer.adobe.com/developer-console/docs/guides/authentication/ServerToServerAuthentication/#server-to-server-credential-types">AIO Developer Documentation</a>
   */
  public static final String SCOPES = "aio_oauth_scopes";

  private final String clientSecret;
  private final Set<String> scopes;

  public OAuthContext(final String clientSecret, final Set<String> scopes) {
    this.clientSecret = clientSecret;
    this.scopes = scopes;

  }

  public static Builder builder() {
    return new Builder();
  }

  @Override
  public void validate() {
    if (StringUtils.isBlank(clientSecret)) {
      throw new IllegalStateException("Your `OAuthContext` is missing a clientSecret.");
    }
    if (scopes.isEmpty()) {
      throw new IllegalStateException("Your `OAuthContext` is missing a scope.");
    }
  }

  @JsonIgnore
  public String getClientSecret() {
    return clientSecret;
  }

  public Set<String> getScopes() {
    return scopes;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    OAuthContext that = (OAuthContext) o;

    if (!Objects.equals(clientSecret, that.clientSecret)) return false;
    return Objects.equals(scopes, that.scopes);
  }

  @Override
  public int hashCode() {
    int result = clientSecret != null ? clientSecret.hashCode() : 0;
    result = 31 * result + (scopes != null ? scopes.hashCode() : 0);
    return result;
  }

  @Override
  public String toString() {
    return "OAuthContext{" + "scopes=" + scopes + '}';
  }

  public static class Builder {
    private String clientSecret;
    private final Set<String> scopes = new HashSet<>();

    public Builder clientSecret(final String clientSecret) {
      this.clientSecret = clientSecret;
      return this;
    }

    public Builder addScope(final String scope) {
      if (StringUtils.isBlank(scope)) {
        return this;
      }
      this.scopes.add(scope.trim());
      return this;
    }

    public Builder configMap(final Map<String, String> configMap) {
      this.clientSecret(configMap.get(CLIENT_SECRET));
      if (!StringUtils.isEmpty(configMap.get(SCOPES))) {
        Arrays.stream(configMap.get(SCOPES).split(",")).forEach(this::addScope);
      }
      return this;
    }

    public Builder systemEvn() {
      return configMap(System.getenv());
    }

    public Builder propertiesPath(final String propertiesPath) {
      return properties(readPropertiesFromFile(propertiesPath).orElse(readPropertiesFromClassPath(propertiesPath)));
    }

    public Builder properties(final Properties properties) {
      return configMap(getMapFromProperties(properties));
    }

    public OAuthContext build() {
      return new OAuthContext(clientSecret, scopes);
    }
  }
}
