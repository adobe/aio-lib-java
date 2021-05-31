
# `aio-lib-java-events-management`

`aio-lib-java-events-management` is Adobe I/O Events - Java SDK - Management Library.
This Java library wraps http API endpoints exposed 
by [`Adobe I/O Events` Provider and Registration APIs](https://www.adobe.io/apis/experienceplatform/events/docs.html#!adobedocs/adobeio-events/master/api/api.md)
 
## ProviderService Test Drive

    ProviderService providerService = ProviderServiceImpl.build(authInterceptor); //[1]
    Optional<Provider> provider = providerService.findById(providerId); //[2]
      
* [1] build your ProviderService by passing a OpenFeign Authentication Request Interceptor
* [2] have this service retrieve one of your event provider by passing a provider id.

The above assumes the [Open Feign RequestInterceptor](https://github.com/OpenFeign/feign#request-interceptors) adds
* an `Authorization` header with a valid `Bearer` access token 
* a `x-api-key` header associated with the above token

Note our [ims](../ims) library provides such interceptors.
Have a look at out [ProviderService `main()` Test Drive](./src/test/java/com/adobe/event/management/ProviderServiceTestDrive.java)

## Builds

This Library is build with [maven](https://maven.apache.org/) (it also runs the unit tests):

### Contributing

Contributions are welcomed! Read the [Contributing Guide](../.github/CONTRIBUTING.md) for more information.

### Licensing

This project is licensed under the Apache V2 License. See [LICENSE](../LICENSE.md) for more information.

  
