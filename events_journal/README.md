
# `aio-lib-java-events-journal`

`aio-lib-java-events-journal` is Adobe I/O - Java SDK - Events Journal Library.
This Java library wraps http API endpoints exposed 
by [`Adobe I/O Events` Journaling API](https://www.adobe.io/apis/experienceplatform/events/docs.html#!adobedocs/adobeio-events/master/api/journaling_api.md)


## JournalService Test Drive

      JournalService journalService = JournalService.builder()
          .authInterceptor(authInterceptor) // [1]
          .workspace(workspace) // [2]
          .url(journalUrl) // [3]
          .build(); //

      Entry entry = journalService.getOldest(); // [4]
      while (!entry.isEmpty()) {
        entry = journalService.get(entry.getNextLink()); // [5]
        }

 * [1] build your JournalService by passing a OpenFeign Authentication Request Interceptor.
 You can use interceptors that our [ims](../ims) library provides,
 or any other [Open Feign RequestInterceptor](https://github.com/OpenFeign/feign#request-interceptors)
 adding :
   * an `Authorization` header with a valid `Bearer` access token
   * a `x-api-key` header associated with the above token
 * [2] set the workspace context expected by the ProviderService
 * [3] pass your registration journaling endpoint URL 
 * [4] have this service retrieve the oldest entry available in your registration journal 
 * [5] you can then loop to retrieve them all following entries' `next` links
 
See our [`main()` Test Drive](./src/test/java/com/adobe/event/journal/JournalServiceTestDrive.java)
 

## Builds

This Library is build with [maven](https://maven.apache.org/) (it also runs the unit tests):

### Contributing

Contributions are welcomed! Read the [Contributing Guide](../.github/CONTRIBUTING.md) for more information.

### Licensing

This project is licensed under the Apache V2 License. See [LICENSE](../LICENSE.md) for more information.

  
