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
package com.adobe.workspace.internal;

import com.adobe.Workspace;
import com.adobe.workspace.WorkspaceConfig;
import com.adobe.exception.AIORuntimeException;
import com.adobe.ims.util.PrivateKeyBuilder;
import java.security.PrivateKey;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(immediate = true, service = WorkspaceConfig.class,
    property = {"label = Adobe I/O Events' Workspace Supplier",
        "description = Adobe I/O Events' Workspace Supplier"})
@Designate(ocd = com.adobe.workspace.ocd.WorkspaceConfig.class)
public class WorkspaceConfigImpl implements WorkspaceConfig {

  private final Logger log = LoggerFactory.getLogger(WorkspaceConfigImpl.class);

  private com.adobe.workspace.ocd.WorkspaceConfig workspaceConfig;

  @Activate
  @Modified
  protected void activate(final com.adobe.workspace.ocd.WorkspaceConfig config) {
    log.debug("activating...");
    this.workspaceConfig = config;
  }

  @Override
  public String getWorkspaceConfig() {
    return "{" +
        "aio_consumer_org_id=`" + workspaceConfig.aio_consumer_org_id() + "`, " +
        "aio_ims_org_id=`" + workspaceConfig.aio_ims_org_id() + "`, " +

        "aio_project_id=`" + workspaceConfig.aio_project_id() + "`, " +
        "aio_workspace_id=`" + workspaceConfig.aio_workspace_id() + "`, " +
        "aio_api_key=`" + workspaceConfig.aio_api_key() + "`, " +
        "aio_credential_id=`" + workspaceConfig.aio_credential_id() + "`, " +
        "aio_technical_account_id=`" + workspaceConfig.aio_technical_account_id() + "`, " +

        "aio_ims_url=`" + workspaceConfig.aio_ims_url() + "`, " +
        "aio_meta_scopes=`" + Arrays.toString(workspaceConfig.aio_meta_scopes()) + "`, " +

        "aio_private_key defined=`" + !StringUtils.isEmpty(workspaceConfig.aio_private_key())
        + "`, " +
        "aio_client_secret defined=`" + !StringUtils.isEmpty(workspaceConfig.aio_client_secret())
        + "`" +
        '}';
  }

  /**
   * Returns the Workspace object built from this WorkspaceConfiguration
   *
   * @return Workspace object
   * @see Workspace
   */
  @Override
  public Workspace getWorkspace() {
    try {
      if (!workspaceConfig.aio_private_key().isEmpty()) {
        PrivateKey privateKey = new PrivateKeyBuilder()
            .encodePkcs8Key(workspaceConfig.aio_private_key()).build();
        if (privateKey != null) {
          return Workspace.builder()
              .configMap(getAuthConfigMap(workspaceConfig))
              .privateKey(privateKey).build();
        } else {
          throw new AIORuntimeException
              ("Adobe I/O Events' Workspace Configuration `Private Key` unsupported format.");
        }
      } else {
        throw new AIORuntimeException
            ("Adobe I/O Events' Workspace Configuration is missing a `Private Key`.");
      }
    } catch (Exception e) {
      throw new AIORuntimeException(e.getMessage(), e);
    }
  }

  private Map<String, String> getAuthConfigMap(
      com.adobe.workspace.ocd.WorkspaceConfig config) {
    Map<String, String> map = new HashMap<String, String>();
    map.put(Workspace.API_KEY, config.aio_api_key());
    map.put(Workspace.CLIENT_SECRET, config.aio_client_secret());
    map.put(Workspace.CONSUMER_ORG_ID, config.aio_consumer_org_id());
    map.put(Workspace.CREDENTIAL_ID, config.aio_credential_id());
    map.put(Workspace.IMS_ORG_ID, config.aio_ims_org_id());
    map.put(Workspace.IMS_URL, config.aio_ims_url());
    map.put(Workspace.PROJECT_ID, config.aio_project_id());
    map.put(Workspace.TECHNICAL_ACCOUNT_ID, config.aio_technical_account_id());
    map.put(Workspace.WORKSPACE_ID, config.aio_workspace_id());
    String metaScopes = StringUtils.join(config.aio_meta_scopes(), ',');
    map.put(Workspace.META_SCOPES, metaScopes);
    return map;
  }


}
