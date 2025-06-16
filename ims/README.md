
# `aio-lib-java-ims`

`aio-lib-java-ims` is the Adobe I/O - Java SDK - IMS Library.

* wrapping http API endpoints exposed by [Adobe Identity Management System (IMS)](https://developer.adobe.com/developer-console/docs/guides/#!AdobeDocs/adobeio-auth/master/AuthenticationOverview/ServiceAccountIntegration.md) 
* providing an Authentication [Open Feign RequestInterceptor](https://github.com/OpenFeign/feign#request-interceptors) that can be leveraged to transparently add the authentication headers expected by many Adobe APIs, it will add
   * an `Authorization` header with a `Bearer` access token
      * renewing it only when expired or when not present in memory yet
   * a `x-api-key` header matching your token

## Test Drive

It only takes a few lines of code:

      Workspace workspace = WorkspaceUtil.getSystemWorkspaceBuilder().build(); // [1]
      ImsService imsService = ImsService.builder().workspace(workspace).build(); // [2]
      logger.info("accessToken: {}", imsService.getAccessToken()); // [3]

      // [1] build your `Workspace` (a Java POJO representation of your `Adobe Developer Console` Workspace)
      //     looking up other System Environment variables.
      //     Note that our fluent workspace and private Key builders offers many ways to have your workspace configured,
      //     we are showing here the most concise
      // [2] build the Ims Service wrapper and have it use this workspace context
      // [3] use this service to retrieve an OAuth access token

      // here is one way you can build the related Adobe IMS Auth Interceptor
      RequestInterceptor authInterceptor = AuthInterceptor.builder().workspace(workspace).build();

Have a look at our [ImsService `main()` Test Drive](./src/test/java/com/adobe/aio/ims/ImsServiceTestDrive.java)


## Configurations

Note that this library is built on top of `aio-lib-java-core` which holds a fluent workspace builder API that offers many ways to build your `Workspace` (a Java POJO representation of your `Adobe Developer Console` Workspace).
See more details on the [Workspace](../aio-lib-java-core/README.md#Workspace) configurations in the `aio-lib-java-core` [README](../core/README.md#Workspace)

It allows you to integrate with the server to server authentication credentials that Adobe supports.
* OAuth Server-to-Server credentials

### OAuth Server-to-Server credentials Configurations
The OAuth Server-to-Server credential relies on the OAuth 2.0 `client_credentials` grant type to generate access tokens. 
To generate an access token, your application can make a single HTTP request with your `client_id` and `client_secret` and `scopes`.

Browse our [OAuth authentication documentation](https://developer.adobe.com/developer-console/docs/guides/authentication/ServerToServerAuthentication/#oauth-server-to-server-credential) for more details, 
and to get you started quickly, have a look at our [sample oauth config file: `workspace.oauth.properties`](./src/test/resources/workspace.oauth.properties)



## Builds

This Library is build with [maven](https://maven.apache.org/) (it also runs the unit tests):

## Contributing

Contributions are welcomed! Read the [Contributing Guide](../.github/CONTRIBUTING.md) for more information.

## Licensing

This project is licensed under the Apache V2 License. See [LICENSE](../LICENSE.md) for more information.

  
