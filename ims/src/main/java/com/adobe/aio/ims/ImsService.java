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
   * Looking up the contextual Workspace, it will use
   * the OAuth authentication context to fetch a valid access token.
   * @return AccessToken a valid API authentication token
   */
  AccessToken getAccessToken();

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
      if (workspace == null) {
        throw new IllegalStateException("Workspace is required to build ImsService");
      }
      workspace.validateAll();
      return new FeignImsService(this.workspace);
    }
  }
}
