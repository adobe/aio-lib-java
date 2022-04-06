# `aio-aem-core`

`aio-aem-core` is a Java OSGI bundle hosting OSGI Components
wrapping [`aio-lib-java-core`](../../core) and [`aio-lib-java-ims`](../../ims)

It hosts the services to 
* get the Adobe Developer Console Workspace
* get Access token (from JWT exchange token flow) from Adobe Identity Management System (IMS)


## `Workspace` Configuration

This bundle expects a Adobe Developer Console [`Workspace` Configuration](src/main/java/com/adobe/aio/aem/workspace/ocd/WorkspaceConfig.java)
defined as an OSGI configuration, with have the following values defined:
* `aio.consumer.org.id`
* `aio.ims.org.id`
* `aio.meta.scopes`
* `aio.project.id`
* `aio.workspace.id`
* `aio.api.key`
* `aio.credential.id`
* `aio.technical.account.id`
* `aio.client.secret`
* `aio.encoded.pkcs8`

For more details about the above, check our [`aio-lib-java-core` documentation](../../core/README.md).
as for the `aio.encoded.pkcs8` private key configuration, please check our [`aio-lib-java-ims` documentation](../../ims/README.md)

For `on premise` version of AEM: 
* Open the Web Console, or select the **Tools** icon, then select **Operations** and **Web Console**.
* Scroll down the list to find **Adobe I/O Events' Workspace Configuration**, update all the values mentioned above, and select **Save** when done.

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


