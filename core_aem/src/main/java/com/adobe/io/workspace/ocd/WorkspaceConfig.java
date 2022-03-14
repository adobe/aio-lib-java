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
package com.adobe.io.workspace.ocd;

import com.adobe.Workspace;
import com.adobe.ims.util.PrivateKeyBuilder;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name = "Adobe I/O Events' Workspace Configuration",
    description = "Adobe I/O Events' Workspace Configuration")
public @interface WorkspaceConfig {

  @AttributeDefinition(name = "IMS URL",
      description = "Adobe IMS URL: prod: https://ims-na1.adobelogin.com | stage: https://ims-na1-stg1.adobelogin.com")
  String aio_ims_url() default "https://ims-na1.adobelogin.com";

  @AttributeDefinition(name = "Meta Scopes",
      description = "Comma separated list of metascopes associated with your API (`/s/event_receiver_api,/s/ent_adobeio_sdk` for instance) (project.workspace.details.credentials.jwt.meta_scopes)",
      cardinality = 10)
  String aio_meta_scopes() default "$[env:"+ Workspace.META_SCOPES +";default=/s/event_receiver_api,/s/ent_adobeio_sdk]";

  @AttributeDefinition(name = "IMS ORG ID",
      description = "Adobe IMS Organization ID as shown in your Adobe Developer Console workspace (project.org.ims_org_id)")
  String aio_ims_org_id() default "$[env:"+ Workspace.IMS_ORG_ID +"]";

  @AttributeDefinition(name = "Consumer ORG ID",
      description = "Adobe I/O Consumer Organization ID as shown in your Adobe Developer Console workspace (project.org.id)")
  String aio_consumer_org_id() default "$[env:"+ Workspace.CONSUMER_ORG_ID +"]";

  @AttributeDefinition(name = "Project ID",
      description = "Adobe I/O Project ID as shown in your Adobe Developer Console workspace (project.id)")
  String aio_project_id() default "$[env:"+ Workspace.PROJECT_ID +"]";

  @AttributeDefinition(name = "Project Workspace ID",
      description = "Adobe I/O Workspace ID as shown in your Adobe Developer Console workspace (project.workspace.id)")
  String aio_workspace_id()  default "$[env:"+ Workspace.WORKSPACE_ID +"]";

  @AttributeDefinition(name = "API Key (Client ID)",
      description = "Adobe I/O API Key (Client ID) as shown in in your Adobe Developer Console workspace (project.workspace.details.credentials.jwt.client_id)")
  String aio_api_key() default "$[env:"+ Workspace.API_KEY +"]";

  @AttributeDefinition(name = "Credential ID",
      description = "Adobe I/O Credential ID as shown in your Adobe Developer Console workspace (project.workspace.details.credentials.id)")
  String aio_credential_id() default "$[env:"+ Workspace.CREDENTIAL_ID +"]";

  @AttributeDefinition(name = "Technical Account ID",
      description = "Technical account ID as shown in your Adobe Developer Console workspace (project.workspace.details.credentials.jwt.technical_account_id)")
  String aio_technical_account_id() default "$[env:"+ Workspace.TECHNICAL_ACCOUNT_ID +"]";

  @AttributeDefinition(name = "Client Secret",
      description = "Adobe I/O Client Secret as shown in your Adobe Developer Console workspace (project.workspace.details.credentials.jwt.client_secret)")
  String aio_client_secret() default "$[secret:"+ Workspace.CLIENT_SECRET +"]";

  @AttributeDefinition(name = "Private Key",
      description = "Base64 encoded pkcs8 Private Key.")
  String aio_encoded_pkcs8() default "$[secret:"+ PrivateKeyBuilder.AIO_ENCODED_PKCS_8 +"]";

}
