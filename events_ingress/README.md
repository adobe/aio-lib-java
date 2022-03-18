
# `aio-lib-java-events-ingress`

`aio-lib-java-events-ingress` is Adobe I/O - Java SDK - Events Publishing Library.
This Java library wraps http API endpoints exposed 
by [`Adobe I/O Events` Publishing API](https://www.adobe.io/apis/experienceplatform/events/docs.html#!adobedocs/adobeio-events/master/api/eventsingress_api.md)


## PublishService Test Drive

          PublishService publishService = PublishService.builder()
              .authInterceptor(authInterceptor) // [1]
              .build(); //
          CloudEvent cloudEvent = publishService.publishCloudEvent(
              "your Adobe I/O Provider Id, 
              "your Adobe I/O Event Code", 
              "your Event payload" ); // [2]

 * [1] build your PublishService by passing a OpenFeign Authentication Request Interceptor.
 You can use interceptors that our [ims](../ims) library provides,
 or any other [Open Feign RequestInterceptor](https://github.com/OpenFeign/feign#request-interceptors)
 adding :
   * an `Authorization` header with a valid `Bearer` access token
   * a `x-api-key` header associated with the above token
 * [3] have this service publish a CloudEvent 
 
See our [`main()` Test Drive](./src/test/java/com/adobe/aio/event/publish/PublishServiceTestDrive.java)
 

## Builds

This Library is build with [maven](https://maven.apache.org/) (it also runs the unit tests):

### Contributing

Contributions are welcomed! Read the [Contributing Guide](../.github/CONTRIBUTING.md) for more information.

### Licensing

This project is licensed under the Apache V2 License. See [LICENSE](../LICENSE.md) for more information.

  
