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
package com.adobe.aio.feign;

import org.apache.commons.lang3.StringUtils;

import com.adobe.aio.workspace.Workspace;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import static com.adobe.aio.util.Constants.*;

public class AIOHeaderInterceptor  implements RequestInterceptor {

  private final String apiKey;
  private final String imsOrgId;

  private AIOHeaderInterceptor(final String apiKey, final String imsOrgId) {
    this.apiKey = apiKey;
    this.imsOrgId = imsOrgId;
  }
  
  @Override
  public void apply(RequestTemplate requestTemplate) {
    applyApiKey(requestTemplate);
    applyImsOrg(requestTemplate);
  }


  private void applyApiKey(RequestTemplate requestTemplate) {
    if (!requestTemplate.headers().containsKey(API_KEY_HEADER) && !StringUtils.isEmpty(apiKey)) {
      requestTemplate.header(API_KEY_HEADER, apiKey);
    }
  }

  private void applyImsOrg(RequestTemplate requestTemplate) {
    if (!requestTemplate.headers().containsKey(IMS_ORG_HEADER) && !StringUtils.isEmpty(imsOrgId)) {
      requestTemplate.header(IMS_ORG_HEADER, imsOrgId);
    }
  }
  
  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {

    private String apiKey;
    private String imsOrgId;

    private Builder() {
    }

    public Builder workspace(Workspace workspace) {
      this.apiKey = workspace.getApiKey();
      this.imsOrgId = workspace.getImsOrgId();
      return this;
    }

    public Builder apiKey(String apiKey) {
        this.apiKey = apiKey;
        return this;
    }

    public Builder imsOrgId(String imsOrgId) {
        this.imsOrgId = imsOrgId;
        return this;
    }

    public AIOHeaderInterceptor build() {
      return new AIOHeaderInterceptor(apiKey, imsOrgId);
    }
  }

}
