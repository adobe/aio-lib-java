
# `aio-lib-java-events-mgmt`

`aio-lib-java-events-mgmt` is Adobe I/O - Java SDK - Events Management Library.
This Java library wraps http API endpoints exposed 
by [`Adobe I/O Events` Provider and Registration APIs](https://www.adobe.io/apis/experienceplatform/events/docs.html#!adobedocs/adobeio-events/master/api/api.md)
 
## ProviderService Test Drive

    ProviderService providerService = ProviderService.builder()
        .workspace(workspace) // [2]
        .consumerOrgId(workspace.getConsumerOrgId()) // [2]
        .build(); //
    Optional<Provider> provider = providerService.findById("someProviderId"); //[3]

 * [1] build your ProviderService by passing a OpenFeign Authentication Request Interceptor.
 You can use interceptors that our [ims](../ims) library provides,
 or any other [Open Feign RequestInterceptor](https://github.com/OpenFeign/feign#request-interceptors)
 adding :
   * an `Authorization` header with a valid `Bearer` access token
   * a `x-api-key` header associated with the above token
 * [2] set the workspace context expected by the ProviderService
 * [3] have this service retrieve one of your event provider by passing a provider id.

See our [`main()` Test Drive](./src/test/java/com/adobe/event/management/ProviderServiceTestDrive.java)

## RegistrationService Test Drive

      RegistrationService registrationService = RegistrationService.builder()
          .authInterceptor(authInterceptor) // [1]
          .workspace(workspace) // [2]
          .build(); //
      Optional<Registration> registration =
          registrationService.findById("someRegistrationId"); // [3]

 * [1] build your RegistrationService by passing a OpenFeign Authentication Request Interceptor.
 You can use interceptors that our [ims](../ims) library provides,
 or any other [Open Feign RequestInterceptor](https://github.com/OpenFeign/feign#request-interceptors)
 adding :
   * an `Authorization` header with a valid `Bearer` access token
   * a `x-api-key` header associated with the above token
 * [2] set the workspace context expected by the ProviderService
 * [3] have this service retrieve one of your registration by passing a registration id.

See our [`main()` Test Drive](./src/test/java/com/adobe/event/management/RegistrationServiceTestDrive.java)

## Builds

This Library is build with [maven](https://maven.apache.org/) (it also runs the unit tests):

### Contributing

Contributions are welcomed! Read the [Contributing Guide](../.github/CONTRIBUTING.md) for more information.

### Licensing

This project is licensed under the Apache V2 License. See [LICENSE](../LICENSE.md) for more information.

  
