# `aio-lib-java-core`

`aio-lib-java-core` is the Adobe I/O  - Java SDK - Core Library. 
This Java library holds various core utilities used across the other modules.
It also holds the core `Adobe Developer Console` Workspace Builder.

## Workspace

This library holds a [`Workspace`](./src/main/java/com/adobe/aio/workspace/Workspace.java) POJO modeling
your [Adobe Developer Console Project Workspace](https://www.adobe.io/apis/experienceplatform/console/docs.html#!AdobeDocs/adobeio-console/master/projects.md),

To get you started quickly use a `.properties` file,
* see our sample config files:
   * [workspace.oauth.properties](./src/test/resources/workspace.oauth.properties)
* download your `project` configurations file from your Adobe Developer Console Project overview page
* map your `project` configurations with this properties

For now, you do have a bit of copy and paste to do, but we have a plan to streamline the process:
* https://github.com/adobe/aio-lib-java/issues/5

The `Workspace` POJO holds your Adobe Developer Console Project configurations
  (download your `project` configurations file from your Adobe Developer Console Project overview page):
* `aio_project_id`  your Adobe Developer Console project id (`project.id`)
* `aio_consumer_org_id`  your Adobe Developer Console consumer orgnaization id (`project.org.id`)
* `aio_ims_org_id` your Adobe Developer Console IMS Organization ID (`project.org.ims_org_id`)
* `aio_workspace_id` your Adobe Developer Console workspace Id (`project.workspace.id`)
* `aio_credential_id` your Adobe Developer Console credential id (`project.workspace.details.credentials[i].id`)
  * this is optional, but it might be handy to have it in your `Workspace` POJO, to avoid confusion when you have multiple credentials, and to eventually in some Adobe API calls

### Workspace Authentication Context
The `Workspace` POJO must also hold your Adobe Developer Auth configurations, pick one of the following authentication methods (see [aio-lib-java-ims](../ims/README.md) docs for more details):

#### OAuth2 authentication
For [OAuth2 authentication](https://developer.adobe.com/developer-console/docs/guides/authentication/ServerToServerAuthentication/#oauth-server-to-server-credential), you will need to provide the following properties:
* `aio_api_key` your Adobe Developer Console OAuth Client ID (`project.workspace.details.credentials[i].oauth_server_to_server.client_id`)
* `aio_client_secret` one your Adobe Developer Console OAuth Client Secrets (`project.workspace.details.credentials[i].oauth_server_to_server.client_secret`)
* `aio_oauth_scopes` a comma separated list of OAuth scopes associated with your API, see your Adobe Developer Console OAuth scopes (`project.workspace.details.credentials[i].oauth_server_to_server.scopes`)


## Builds

This Library is build with [maven](https://maven.apache.org/) (it also runs the unit tests):

### Contributing

Contributions are welcomed! Read the [Contributing Guide](../.github/CONTRIBUTING.md) for more information.

### Licensing

This project is licensed under the Apache V2 License. See [LICENSE](../LICENSE.md) for more information.
  

  
