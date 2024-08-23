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
package com.adobe.aio.aem.workspace.internal;

import com.adobe.aio.aem.status.Status;
import com.adobe.aio.aem.workspace.WorkspaceSupplier;
import com.adobe.aio.aem.workspace.ocd.WorkspaceConfig;
import com.adobe.aio.auth.Context;
import com.adobe.aio.auth.JwtContext;
import com.adobe.aio.auth.OAuthContext;
import com.adobe.aio.ims.util.PrivateKeyBuilder;
import com.adobe.aio.util.WorkspaceUtil;
import com.adobe.aio.workspace.Workspace;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(immediate = true, service = WorkspaceSupplier.class,
    property = {"label = Adobe I/O Events' Workspace Supplier",
        "description = Adobe I/O Events' Workspace Supplier"})
@Designate(ocd = WorkspaceConfig.class)
public class WorkspaceSupplierImpl implements WorkspaceSupplier {

  private final Logger log = LoggerFactory.getLogger(WorkspaceSupplierImpl.class);

  private WorkspaceConfig workspaceConfig;

  @Activate
  @Modified
  protected void activate(final WorkspaceConfig config) {
    log.debug("activating...");
    this.workspaceConfig = config;
  }

  @Override
  public Status getStatus() {
    Map<String, Object> details = new HashMap<>();
    try {
      Workspace workspace = getWorkspace();
      details.put("workspace", workspace);
      workspace.validateAll();
      return new Status(Status.UP, details);
    } catch (IllegalArgumentException e) {
      return new Status(Status.DOWN, details, e);
    }
  }

  /**
   * Returns the Workspace object built from the WorkspaceConfig ocd
   *
   * @return Workspace object
   * @see Workspace
   */
  @Override
  public Workspace getWorkspace() {
    return WorkspaceUtil.getWorkspaceBuilder(getAuthConfigMap(workspaceConfig)).build();
  }

  private Map<String, String> getAuthConfigMap(
      WorkspaceConfig config) {
    Map<String, String> map = new HashMap<String, String>();
    putIfNotBlank(map, Workspace.API_KEY, config.aio_api_key());
    putIfNotBlank(map, Workspace.CONSUMER_ORG_ID, config.aio_consumer_org_id());
    putIfNotBlank(map, Workspace.IMS_ORG_ID, config.aio_ims_org_id());
    putIfNotBlank(map, Workspace.IMS_URL, config.aio_ims_url());
    putIfNotBlank(map, Workspace.PROJECT_ID, config.aio_project_id());
    putIfNotBlank(map, Workspace.WORKSPACE_ID, config.aio_workspace_id());

    putIfNotBlank(map, Context.CLIENT_SECRET, config.aio_client_secret());

    putIfNotBlank(map, JwtContext.CREDENTIAL_ID, config.aio_credential_id());
    putIfNotBlank(map, JwtContext.TECHNICAL_ACCOUNT_ID, config.aio_technical_account_id());
    putIfNotBlank(map, JwtContext.META_SCOPES, config.aio_meta_scopes());
    putIfNotBlank(map, PrivateKeyBuilder.AIO_ENCODED_PKCS_8, config.aio_encoded_pkcs8());

    putIfNotBlank(map, OAuthContext.SCOPES, config.aio_oauth_scopes());
    return map;
  }

  private void putIfNotBlank(Map<String, String> map, String key, String value) {
    if (StringUtils.isNotBlank(value)) {
      map.put(key, value);
    }
  }

}
