# `com.adobe.aio.aem.core`

`com.adobe.aio.aem.core` is a Java OSGI bundle hosting OSGI Components
wrapping [`aio-lib-java-core`](../../core) and [`aio-lib-java-ims`](../../ims)

## Expected `Workspace` osgi configuration

This bundle expects a [`Workspace Configuration`](src/main/java/com/adobe/aio/aem/workspace/ocd/WorkspaceConfig.java)
defined as an OSGI configuration.  
* for more details about the `Worskspace` configuration: check our [`aio-lib-java-core` documentation](../../core/README.md).
* for more details about the `aio.encoded.pkcs8` private key configuration: check our [`aio-lib-java-ims` documentation](../../ims/README.md)

Please [choose the appropriate OSGi configuration value types](https://experienceleague.adobe.com/docs/experience-manager-cloud-service/content/implementing/deploying/configuring-osgi.html%3Flang%3Den#how-to-choose-the-appropriate-osgi-configuration-value-type), 
use environment variables for the following:
* `aio.consumer.org.id` 
* `aio.ims.org.id`
* `aio.meta.scopes`
* `aio.project.id`
* `aio.workspace.id`
* `aio.api.key`
* `aio.credential.id`
* `aio.technical.account.id`

Use secret environment variables for the following:
* `aio.client.secret`
* `aio.encoded.pkcs8`

## Status Check

This bundle comes with a status servlet: 
 from [/bin/aio/workspace.json](http://localhost:4502/bin/aio/workspace.json)
you can `GET` the status of your workspace configuration.

If valid, the status json payload should like that :

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


