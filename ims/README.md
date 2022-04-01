
# `aio-lib-java-ims`

`aio-lib-java-ims` is an Adobe I/O - Java SDK - IMS Library.
This Java library wraps http API endpoints exposed by 
[Adobe Identity Management System (IMS)](https://www.adobe.io/authentication/auth-methods.html#!AdobeDocs/adobeio-auth/master/AuthenticationOverview/AuthenticationGuide.md)

## Service Account Integration (JWT authentication flow)

A Service Account connection allows your application to call Adobe services on behalf of 
the application itself or on behalf of an enterprise organization.

For this type of connection, you will create a JSON Web Token (JWT) that encapsulates 
your credentials and begin each API session by exchanging the JWT for an access token.

The JWT encodes all of the identity and security information required to obtain an access 
token and must be signed with the private key that is associated with a public key certificate specified on your integration.

This Java library will help you implement this JWT exchange token flow, to get a valid access token
and start interacting with the many Adobe I/O API that support such authentication.

### Configurations

Browse our [Service Account Integration (JWT authentication flow) doc](https://www.adobe.io/authentication/auth-methods.html#!AdobeDocs/adobeio-auth/master/AuthenticationOverview/ServiceAccountIntegration.md), 
our fluent workspace builder offers many ways to have your `Workspace` (a Java POJO representation of your `Adobe Developer Console` Workspace) configured.

To get you started quickly you could use a `.properties` file, 
see our [sample config file](./src/test/resources/workspace.properties)

#### Create and configure your public and private key

First, use openssl to create an RSA private/public certificate pair

     openssl req -x509 -sha256 -nodes -days 365 -newkey rsa:2048 -keyout private.key -out certificate_pub.crt

Our [`PrivateKeyBuilder`](./src/main/java/com/adobe/util/PrivateKeyBuilder.java) 
offer 3 options to configure/use this privateKey:
* Option 1: use a pcks8 file
* Option 2: use a base 64 encoded pcks8 key
* Option 3: use a keystore 

For option 1, to convert your private key to a PKCS8 format, use the following command: 

    openssl pkcs8 -topk8 -inform PEM -outform DER -in private.key -nocrypt > private.pkcs8.key

For option 2, to base 64 encode it, use the following command: 

    base64 private.pkcs8.key 
    
For option 3, Use the following commands to set the alias (as `myalias` here)  and a non-empty keystore password.

    cat private.key certificate_pub.crt > private-key-crt
    openssl pkcs12 -export -in private-key-crt -out keystore.p12 -name myalias -noiter -nomaciter

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

  
