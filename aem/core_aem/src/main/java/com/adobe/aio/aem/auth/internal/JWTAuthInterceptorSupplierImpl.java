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
package com.adobe.aio.aem.auth.internal;

import com.adobe.aio.aem.auth.JWTAuthInterceptorSupplier;
import com.adobe.aio.aem.status.Status;
import com.adobe.aio.aem.workspace.WorkspaceSupplier;
import com.adobe.aio.ims.feign.JWTAuthInterceptor;
import java.util.HashMap;
import java.util.Map;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(immediate = true, service = JWTAuthInterceptorSupplier.class)
public class JWTAuthInterceptorSupplierImpl implements JWTAuthInterceptorSupplier {

  @Reference
  WorkspaceSupplier workspaceSupplier;

  private JWTAuthInterceptor jwtAuthInterceptor;

  @Activate
  protected void activate() {
    this.jwtAuthInterceptor = JWTAuthInterceptor.builder()
        .workspace(workspaceSupplier.getWorkspace()).build();
  }

  @Override
  public Status getStatus() {
    Map<String, Object> details = new HashMap<>(1);
    details.put("workspace_status", workspaceSupplier.getStatus());
    try {
      return new Status(jwtAuthInterceptor.isUp(), details);
    } catch (Exception e) {
      return new Status(Status.DOWN, details, e);
    }
  }

  @Override
  public JWTAuthInterceptor getJWTAuthInterceptor() {
    return this.jwtAuthInterceptor;
  }

}
