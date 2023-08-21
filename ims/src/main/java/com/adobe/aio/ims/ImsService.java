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
package com.adobe.aio.ims;

import com.adobe.aio.ims.feign.FeignImsService;
import com.adobe.aio.workspace.Workspace;
import com.adobe.aio.ims.model.AccessToken;

public interface ImsService {

  /**
   * Returns an {@link AccessToken} that can be used for other AIO API Calls.
   *
   * @deprecated this will be removed in v2.0
   * @return AccessToken a valid API authentication token
   */
  @Deprecated()
  AccessToken getJwtExchangeAccessToken();

  /**
   * Checks that the access token is still valid.
   *
   * @deprecated this will be removed in v2.0
   * @param accessToken the token to check
   * @return true if the provided access token is still valid, false otherwise
   */
  @Deprecated()
  boolean validateAccessToken(String accessToken);

  AccessToken getOAuthAccessToken();

  static Builder builder() {
    return new Builder();
  }

  class Builder {

    private Workspace workspace;

    private Builder() {
    }

    /**
     * Add the workspace context to this builder.
     *
     * @param workspace the workspace context
     * @return this builder
     */
    public Builder workspace(Workspace workspace) {
      this.workspace = workspace;
      return this;
    }

    /**
     * Builds an IMS Service instance.
     *
     * @return a configured IMS Service
     * @throws IllegalStateException if the Workspace authentication context is not valid.
     */
    public ImsService build() throws IllegalStateException {
      this.workspace.getAuthContext().validate();
      return new FeignImsService(this.workspace);
    }
  }
}
