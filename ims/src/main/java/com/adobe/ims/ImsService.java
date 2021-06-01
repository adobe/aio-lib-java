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


import com.adobe.Workspace;
import com.adobe.ims.model.AccessToken;

public interface ImsService {

  AccessToken getJwtExchangeAccessToken();

  static Builder builder(){
    return new Builder();
  }

  class Builder {
    private Workspace workspace;

    public Builder(){
    }

    public Builder workspace(Workspace workspace){
      this.workspace = workspace;
      return this;
    }

    public ImsService build(){
      return new ImsServiceImpl(this.workspace);
    }
  }
}
