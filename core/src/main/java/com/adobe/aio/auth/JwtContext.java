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

import java.security.PrivateKey;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * JWT Authentication context.
 */
public class JwtContext implements Context {

  private static final Logger logger = LoggerFactory.getLogger(JwtContext.class);
  public static final String CREDENTIAL_ID = "aio_credential_id";
  public static final String TECHNICAL_ACCOUNT_ID = "aio_technical_account_id";
  public static final String META_SCOPES = "aio_meta_scopes";

  private final String credentialId;
  private final String technicalAccountId;
  private final Set<String> metascopes;
  private final String clientSecret;
  private final PrivateKey privateKey;

  public JwtContext(final String credentialId, final String clientSecret, final String technicalAccountId,
                    final Set<String> metascopes, final PrivateKey privateKey) {
    this.credentialId = credentialId;
    this.clientSecret = clientSecret;
    this.technicalAccountId = technicalAccountId;
    this.metascopes = metascopes;
    this.privateKey = privateKey;
  }

  public static Builder builder() {
    return new Builder();
  }

  public void validate() {
    if (StringUtils.isEmpty(clientSecret)) {
      throw new IllegalStateException("Your `JwtContext` is missing a clientSecret");
    }
    if (StringUtils.isEmpty(technicalAccountId)) {
      throw new IllegalStateException("Your `JwtContext` is missing a technicalAccountId");
    }
    if (metascopes.isEmpty()) {
      throw new IllegalStateException("Your `JwtContext` is missing a metascope");
    }
    if (privateKey == null) {
      throw new IllegalStateException("Your `JwtContext` is missing a privateKey");
    }
  }

  public String getCredentialId() {
    return credentialId;
  }

  public String getTechnicalAccountId() {
    return technicalAccountId;
  }

  public Set<String> getMetascopes() {
    return metascopes;
  }

  // we want to avoid serializing this secret
  @JsonIgnore
  public String getClientSecret() {
    return clientSecret;
  }

  public boolean isClientSecretDefined() {
    return !StringUtils.isEmpty(this.clientSecret);
  }

  // we want to avoid serializing this secret
  @JsonIgnore
  public PrivateKey getPrivateKey() {
    return privateKey;
  }

  public boolean isPrivateKeyDefined() {
    return (this.privateKey != null);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    JwtContext that = (JwtContext) o;

    if (!Objects.equals(credentialId, that.credentialId)) return false;
    if (!Objects.equals(technicalAccountId, that.technicalAccountId))
      return false;
    if (!Objects.equals(metascopes, that.metascopes)) return false;
    if (!Objects.equals(clientSecret, that.clientSecret)) return false;
    return Objects.equals(privateKey, that.privateKey);
  }

  @Override
  public int hashCode() {
    int result = credentialId != null ? credentialId.hashCode() : 0;
    result = 31 * result + (technicalAccountId != null ? technicalAccountId.hashCode() : 0);
    result = 31 * result + (metascopes != null ? metascopes.hashCode() : 0);
    result = 31 * result + (clientSecret != null ? clientSecret.hashCode() : 0);
    result = 31 * result + (privateKey != null ? privateKey.hashCode() : 0);
    return result;
  }

  @Override
  public String toString() {
    return "JwtContext{" +
        "credentialId='" + credentialId + '\'' +
        ", technicalAccountId='" + technicalAccountId + '\'' +
        ", metascopes=" + metascopes +
        '}';
  }

  public static class Builder {

    private String credentialId;
    private String clientSecret;
    private String technicalAccountId;
    private PrivateKey privateKey;
    private final Set<String> metascopes = new HashSet<>();

    public Builder credentialId(final String credentialId) {
      this.credentialId = credentialId;
      return this;
    }

    public Builder clientSecret(final String clientSecret) {
      this.clientSecret = clientSecret;
      return this;
    }

    public Builder technicalAccountId(final String technicalAccountId) {
      this.technicalAccountId = technicalAccountId;
      return this;
    }

    public Builder addMetascope(final String metascope) {
      this.metascopes.add(metascope);
      return this;
    }

    public Builder privateKey(final PrivateKey privateKey) {
      this.privateKey = privateKey;
      return this;
    }

    public JwtContext build() {
      return new JwtContext(credentialId, clientSecret, technicalAccountId, metascopes, privateKey);
    }
  }
}
