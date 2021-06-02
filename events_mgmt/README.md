
# `aio-lib-java-events-mgmt`

`aio-lib-java-events-mgmt` is Adobe I/O - Java SDK - Events Management Library.
This Java library wraps http API endpoints exposed 
by [`Adobe I/O Events` Provider and Registration APIs](https://www.adobe.io/apis/experienceplatform/events/docs.html#!adobedocs/adobeio-events/master/api/api.md)
 
## ProviderService Test Drive

    ProviderService providerService = ProviderService.builder()
        .authInterceptor(authInterceptor) // [1]
        .consumerOrgId(workspace.getConsumerOrgId()) // [2]
        .build(); //
    Optional<Provider> provider = providerService.findById("someProviderId"); //[3]

 * [1] build your ProviderService by passing a OpenFeign Authentication Request Interceptor
       This [Open Feign RequestInterceptor](https://github.com/OpenFeign/feign#request-interceptors)
       duty is to add :
      * an `Authorization` header with a valid `Bearer` access token
      * and a `x-api-key` header associated with the above token
 * [2] set the consumerOrgId workspace context expected by the ProviderService
 * [3] have this service retrieve one of your event provider by passing a provider id.

Note our [ims](../ims) library provides such interceptors.
Have a look at out [ProviderService `main()` Test Drive](./src/test/java/com/adobe/event/management/ProviderServiceTestDrive.java)

## Builds

This Library is build with [maven](https://maven.apache.org/) (it also runs the unit tests):

### Contributing

Contributions are welcomed! Read the [Contributing Guide](../.github/CONTRIBUTING.md) for more information.

### Licensing

This project is licensed under the Apache V2 License. See [LICENSE](../LICENSE.md) for more information.

  
