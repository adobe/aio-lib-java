# `aio-aem-core`

`aio-aem-core` is a Java OSGI bundle hosting OSGI Components
wrapping [`aio-lib-java-core`](../../core) and [`aio-lib-java-ims`](../../ims)

It hosts the services to 
* get the Adobe Developer Console Workspace
* get Access token (from JWT exchange token flow) from Adobe Identity Management System (IMS)


## `Workspace` Configuration

This bundle expects your Adobe Developer Console [`Workspace` Configuration](src/main/java/com/adobe/aio/aem/workspace/ocd/WorkspaceConfig.java)
defined as an OSGI configuration. Its [WorkspaceSupplier](https://github.com/adobe/aio-lib-java/blob/main/aem/core_aem/src/main/java/com/adobe/aio/aem/workspace/internal/WorkspaceSupplierImpl.java)
service looks up the following OSGI configuration keys:

* `aio.project.id` your Adobe Developer Console project id (`project.id`)
* `aio.consumer.org.id`  your Adobe Developer Console consumer orgnaization id (`project.org.id`)
* `aio.ims.org.id` your Adobe Developer Console IMS Organization ID (`project.org.ims_org_id`)
* `aio.workspace.id` your Adobe Developer Console workspace Id (`project.workspace.id`)
* `aio.credential.id` your Adobe Developer Console jwt credential id (`project.workspace.details.credentials[i].id`)
* `aio.api.key` your Adobe Developer Console jwt credential API Key (or Client ID) (`project.workspace.details.credentials[i].jwt.client_id`)
* `aio.client.secret` your Adobe Developer Console jwt credential client secret (`project.workspace.details.credentials[i].jwt.client_secret`)
* `aio.meta.scopes` a comma separated list of metascopes associated with your API, see your Adobe Developer Console jwt credential metascopes (`project.workspace.details.credentials[i].jwt.meta_scopes`)
* `aio.technical.account.id` your Adobe Developer Console jwt credential technical account id (`project.workspace.details.credentials[i].jwt.technical_account_id`)
* `aio.encoded.pkcs8` your private key (in a base64 encoded pkcs8 format) see our [aio-lib-java ims public/private key management guide](https://github.com/adobe/aio-lib-java/tree/main/ims#option-2-use-a-base-64-encoded-pcks8-key).


### `aio.encoded.pkcs8` 

`aio.encoded.pkcs8` configuration value is associated with your Adobe Developer Console private key.
It is a string: your private key in a pkcs8 format, base64 encoded, here is how to generate it:

First, convert your private key to a PKCS8 format, use the following command:

    openssl pkcs8 -topk8 -inform PEM -outform DER -in private.key -nocrypt > private.pkcs8.key

Then, base 64 encode it, use the following command:

    base64 private.pkcs8.key 

For more details check our [`aio-lib-java-ims` documentation](../../ims/README.md)

### `on premise` AEM configuration:
When running AEM on premise:
* Open the Web Console, or select the **Tools** icon, then select **Operations** and **Web Console**.
* Scroll down the list to find **Adobe I/O Events' Workspace Configuration**, update all the values mentioned above, and select **Save** when done.

### AEM as a cloud service configuration:
When running on AEM as a cloud service, you'll have to use Cloud Manager to deploy these configurations,
[choose the appropriate OSGi configuration value types](https://experienceleague.adobe.com/docs/experience-manager-cloud-service/content/implementing/deploying/configuring-osgi.html%3Flang%3Den#how-to-choose-the-appropriate-osgi-configuration-value-type), 
mix environment variables, and secret environments variables (for `aio.client.secret` and `aio.encoded.pkcs8`).


## Status Check

This bundle comes with a status servlet: 
 from [/bin/aio/workspace.json](http://localhost:4502/bin/aio/workspace.json)
you can `GET` the status of your workspace configuration.

The response json payload should like this:

    {
        "status": "up",
        "details": {
            "workspace": {
                "imsUrl": "https://ims-na1.adobelogin.com",
                "imsOrgId": "...@AdobeOrg",
                "apiKey": "...",
                "credentialId": "...",
                "technicalAccountId": "...@techacct.adobe.com",
                "metascopes": [
                "...",
                "/s/ent_adobeio_sdk"
                ],
                "consumerOrgId": "...",
                "projectId": "...",
                "workspaceId": "...",
                "projectUrl": "https://developer.adobe.com/console/projects/.../.../overview",
                "clientSecretDefined": true,
                "privateKeyDefined": true
                }
        },
        "error": null
    }

## Builds

This Library is build with [maven](https://maven.apache.org/)

### Contributing

Contributions are welcomed! Read the [Contributing Guide](../.github/CONTRIBUTING.md) for more
information.

### Licensing

This project is licensed under the Apache V2 License. See [LICENSE](../LICENSE.md) for more
information.


