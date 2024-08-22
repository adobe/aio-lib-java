
# `aio-lib-java-ims`

`aio-lib-java-ims` is an Adobe I/O - Java SDK - IMS Library.


Note that this library is built on top of `aio-lib-java-core`, It holds a fluent workspace builder API that offers many ways to build your `Workspace` (a Java POJO representation of your `Adobe Developer Console` Workspace).
See more details on the [Workspace](../aio-lib-java-core/README.md#Workspace) configurations in the `aio-lib-java-core` [README](../aio-lib-java-core/README.md#Workspace)

As for this `aio-lib-java-ims` Java library, it wraps http API endpoints exposed by
[Adobe Identity Management System (IMS)](https://developer.adobe.com/developer-console/docs/guides/#!AdobeDocs/adobeio-auth/master/AuthenticationOverview/ServiceAccountIntegration.md)

It allows you to integrate with the two server to server authentication credentials that Adobe supports. These credentials only differ in the way your application generates the access token, the rest of their functioning is similar.
* OAuth Server-to-Server credential
* Service Account (JWT) credential (deprecated)

## OAuth Server-to-Server credential

The OAuth Server-to-Server credential relies on the OAuth 2.0 `client_credentials` grant type to generate access tokens. 
To generate an access token, your application can make a single HTTP request with your `client_id` and `client_secret` and `scopes`.

Browse our [OAuth authentication documentation](https://developer.adobe.com/developer-console/docs/guides/authentication/ServerToServerAuthentication/#oauth-server-to-server-credential)
for more details.

This Java library will help you fetch your OAuth token flow, 
and start interacting with the many Adobe I/O API that support such authentication.

### Configurations


To get you started quickly you could use a `.properties` file,
see our [sample config file](./src/test/resources/workspace.oauth.properties)


### Our reusable `OpenFeign` OAuth Authentication `RequestInterceptor`

This lib contains an OAuth Authentication `RequestInterceptor`: [OAuthInterceptor](src/main/java/com/adobe/aio/ims/api/OAuthInterceptor.java)
It is a [Open Feign RequestInterceptor](https://github.com/OpenFeign/feign#request-interceptors).
It can be leverage to add the authentication headers expected by many Adobe APIs, it will add
* an `Authorization` header with a `Bearer` access token (generated from a JWT exchange flow)
  * renewing it only when expired (after 24 hours) or when not present in memory yet
* a `x-api-key` header matching your JWT token

### Test Drive


    Workspace workspace = Workspace.builder()
            .systemEnv()
            .build(); // [1]
    ImsService imsService = ImsService.builder().workspace(workspace).build(); // [2]

    AccessToken accessToken = imsService.getOAuthAccessToken(); // [3]

    // [1] build your `Workspace` (a Java POJO representation of your `Adobe Developer Console` Workspace)
    //     looking up other System Environment variables.
    //     Note that our fluent workspace and private Key builders offers many ways to have your workspace configured,
    //     we are showing here the most concise
    // [2] build the Ims Service wrapper and have it use this workspace context
    // [3] use this service to retrieve an OAuth access token

    // here is one way you can build the related IMS Feign OAuth Interceptor
    RequestInterceptor authInterceptor = OAuthInterceptor.builder()
            .workspace(workspace)
            .build();


Have a look at our [ImsService `main()` Test Drive](./src/test/java/com/adobe/aio/ims/ImsServiceTestDrive.java)

## Service Account (JWT) credential (deprecated)

A Service Account connection allows your application to call Adobe services on behalf of 
the application itself or on behalf of an enterprise organization.

For this type of connection, you will create a JSON Web Token (JWT) that encapsulates 
your credentials and begin each API session by exchanging the JWT for an access token.

The JWT encodes all of the identity and security information required to obtain an access 
token and must be signed with the private key that is associated with a public key certificate specified on your integration.

Browse our [JWT authentication documentation](https://developer.adobe.com/developer-console/docs/guides/authentication/JWT/)
for more details.

This Java library will help you implement this JWT exchange token flow, to get a valid access token
and start interacting with the many Adobe I/O API that support such authentication.

### Configurations

This library fluent workspace builder API offers many ways to have your `Workspace` (a Java POJO representation of your `Adobe Developer Console` Workspace) configured.

To get you started quickly you could use a `.properties` file, 
see our [sample config file](./src/test/resources/workspace.properties)

#### Create and configure your public and private key

As introduced above the authentication flow signs the JWT request and therefore requires private-public keys configurations
, therefore you will need to

* First, create this  RSA private/public certificate pair, using openssl:

     `openssl req -x509 -sha256 -nodes -days 365 -newkey rsa:2048 -keyout private.key -out certificate_pub.crt`

* Then, upload the public key in your Adobe Developer Workspace, see our [JWT authentication documentation](https://developer.adobe.com/developer-console/docs/guides/authentication/JWT/)
* Finally, configure this library (and its [`PrivateKeyBuilder`](./src/main/java/com/adobe/util/PrivateKeyBuilder.java)) with your privateKey, you may either
  * use a pcks8 file
  * use a base 64 encoded pcks8 key
  * use a keystore file 

##### Option 1: Use a pcks8 file

First, convert your private key to a PKCS8 format, use the following command:

    openssl pkcs8 -topk8 -inform PEM -outform DER -in private.key -nocrypt > private.pkcs8.key

Then, set your workspace `aio_pkcs8_file_path` properties 
to match the `private.pkcs8.key` file path (you generated using the previous command)


##### Option 2: use a base 64 encoded pcks8 key

First, convert your private key to a PKCS8 format, use the following command:

    openssl pkcs8 -topk8 -inform PEM -outform DER -in private.key -nocrypt > private.pkcs8.key

Then, base 64 encode it, use the following command:

    base64 private.pkcs8.key 

Finally, set your workspace `aio_encoded_pkcs8` properties value using the string you generated with the above command

##### Option 3: use a keystore

First, use the following commands to set the alias (as `myalias` here)  and a non-empty keystore password.

    cat private.key certificate_pub.crt > private-key-crt
    openssl pkcs12 -export -in private-key-crt -out keystore.p12 -name myalias -noiter -nomaciter

Then fill the associated `aio_pkcs12_file_path`, `aio_pkcs12_alias` and `aio_pkcs12_password` workspace properties.


### Our reusable `OpenFeign` JWT (exchange token flow) Authentication `RequestInterceptor`

This lib also contains JWT (exchange token flow) Authentication `RequestInterceptor`: [JWTAuthInterceptor](src/main/java/com/adobe/aio/ims/api/JWTAuthInterceptor.java) 
It is a [Open Feign RequestInterceptor](https://github.com/OpenFeign/feign#request-interceptors).
It can be leverage to add the authentication headers expected by many Adobe APIs, it will add
* an `Authorization` header with a `Bearer` access token (generated from a JWT exchange flow)
  * renewing it only when expired (after 24 hours) or when not present in memory yet
* a `x-api-key` header matching your JWT token

### Test Drive

    PrivateKey privateKey = new PrivateKeyBuilder().systemEnv().build(); // [1]
    Workspace workspace = Workspace.builder()
        .systemEnv()
        .privateKey(privateKey)
        .build(); // [2]
    ImsService imsService = ImsService.builder().workspace(workspace).build(); // [3]

    AccessToken accessToken = imsService.getJwtExchangeAccessToken(); // [4]

    // [1] Build your PrivateKey looking up the key indicated by you System Environment variables
    // [2] build your `Workspace` (a Java POJO representation of your `Adobe Developer Console` Workspace)
    //     looking up other System Environment variables. 
    //     Note that our fluent workspace and private Key builders offers many ways to have your workspace configured,
    //     we are showing here the most concise
    // [3] build the Ims Service wrapper and have it use this workspace context
    // [4] use this service to retrieve an access token using a jwt exchange token flow


Have a look at our [ImsService `main()` Test Drive](./src/test/java/com/adobe/aio/ims/ImsServiceTestDrive.java)


## Builds

This Library is build with [maven](https://maven.apache.org/) (it also runs the unit tests):

## Contributing

Contributions are welcomed! Read the [Contributing Guide](../.github/CONTRIBUTING.md) for more information.

## Licensing

This project is licensed under the Apache V2 License. See [LICENSE](../LICENSE.md) for more information.

  
