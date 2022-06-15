
# `aio-lib-java-events-mgmt`

`aio-lib-java-events-mgmt` is Adobe I/O - Java SDK - Events Management Library.
This Java library wraps http API endpoints exposed 
by [`Adobe I/O Events` Provider and Registration APIs](https://www.adobe.io/apis/experienceplatform/events/docs.html#!adobedocs/adobeio-events/master/api/api.md)
 
## ProviderService Test Drive

     ProviderService providerService = ProviderService.builder()
          .workspace(workspace) // [1]
          .build(); 
      Optional<Provider> provider = providerService.findById("someProviderId"); //[2]   

 * [1] build your ProviderService by passing your workspace context  (cf. the [aio-lib-java-core](../core) for workspace documentation)
 * [2] have this service retrieve one of your event provider by passing a provider id.

See our [`main()` Test Drive](./src/test/java/com/adobe/aio/event/management/ProviderServiceTestDrive.java)

## RegistrationService Test Drive

      RegistrationService registrationService = RegistrationService.builder()
          .workspace(workspace) // [1]
          .build(); //
      Optional<Registration> registration =
          registrationService.findById("someRegistrationId"); // [2]

 * [1] build your RegistrationService by passing your workspace context  (cf. the [aio-lib-java-core](../core) for workspace documentation)
 * [2] have this service retrieve one of your registration by passing a registration id.

See our [`main()` Test Drive](./src/test/java/com/adobe/event/management/RegistrationServiceTestDrive.java)

## Builds

This Library is build with [maven](https://maven.apache.org/) (it also runs the unit tests):

### Contributing

Contributions are welcomed! Read the [Contributing Guide](../.github/CONTRIBUTING.md) for more information.

### Licensing

This project is licensed under the Apache V2 License. See [LICENSE](../LICENSE.md) for more information.

  
