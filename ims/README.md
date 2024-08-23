
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

It allows you to integrate with the two server to server authentication credentials that Adobe supports.
* OAuth Server-to-Server credentials
* Service Account (JWT) credentials (deprecated)

These credentials only differ in the way your application generates the access token, the rest of their functioning is similar.

### OAuth Server-to-Server credentials Configurations
The OAuth Server-to-Server credential relies on the OAuth 2.0 `client_credentials` grant type to generate access tokens. 
To generate an access token, your application can make a single HTTP request with your `client_id` and `client_secret` and `scopes`.

Browse our [OAuth authentication documentation](https://developer.adobe.com/developer-console/docs/guides/authentication/ServerToServerAuthentication/#oauth-server-to-server-credential) for more details, 
and to get you started quickly, have a look at our [sample oauth config file](./src/test/resources/workspace.oauth.properties)


### Service Account (JWT) credential (deprecated)
A Service Account connection allows your application to call Adobe services on behalf of 
the application itself or on behalf of an enterprise organization.

For this type of connection, you will create a JSON Web Token (JWT) that encapsulates 
your credentials and begin each API session by exchanging the JWT for an access token.

The JWT encodes all of the identity and security information required to obtain an access 
token and must be signed with the private key that is associated with a public key certificate specified on your integration.

Browse our [JWT authentication documentation](https://developer.adobe.com/developer-console/docs/guides/authentication/JWT/) for more details,
and to get you started quickly, look at our [sample jwt config file](./src/test/resources/workspace.properties)

#### Create and configure your public and private key
As introduced above the authentication flow signs the JWT request and therefore requires private-public keys configurations
, therefore you will need to

* create this  RSA private/public certificate pair, using openssl:
     `openssl req -x509 -sha256 -nodes -days 365 -newkey rsa:2048 -keyout private.key -out certificate_pub.crt`
* upload the public key in your Adobe Developer Workspace, see our [JWT authentication documentation](https://developer.adobe.com/developer-console/docs/guides/authentication/JWT/)
* configure this library (and its [`PrivateKeyBuilder`](./src/main/java/com/adobe/util/PrivateKeyBuilder.java)) with your privateKey using a base 64 encoded pcks8 key
  * convert your private key to a PKCS8 format, use the following command:
    `openssl pkcs8 -topk8 -inform PEM -outform DER -in private.key -nocrypt > private.pkcs8.key`
  * base 64 encode it (and stuff it in a `private.pkcs8.key.base64` file), use the following command:
     `base64 -i private.pkcs8.key -o private.pkcs8.key.base64`
* set your workspace `aio_encoded_pkcs8` properties value using the string you generated with the above command


## Builds

This Library is build with [maven](https://maven.apache.org/) (it also runs the unit tests):

## Contributing

Contributions are welcomed! Read the [Contributing Guide](../.github/CONTRIBUTING.md) for more information.

## Licensing

This project is licensed under the Apache V2 License. See [LICENSE](../LICENSE.md) for more information.

  
