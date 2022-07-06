
# `aio-lib-java-events-journal`

`aio-lib-java-events-journal` is Adobe I/O - Java SDK - Events Journal Library.
This Java library wraps http API endpoints exposed 
by [`Adobe I/O Events` Journaling API](https://www.adobe.io/apis/experienceplatform/events/docs.html#!adobedocs/adobeio-events/master/api/journaling_api.md)


## JournalService Test Drive

      JournalService journalService = JournalService.builder()
          .workspace(workspace) // [1]
          .url(journalUrl) // [2]
          .build(); //

      Entry entry = journalService.getOldest(); // [3]
      while (!entry.isEmpty()) {
        entry = journalService.get(entry.getNextLink()); // [4]
        }

 * [1] build your JournalService by passing your workspace context  (cf. the [aio-lib-java-core](../core) for workspace documentation)
 * [2] pass your registration journaling endpoint URL 
 * [3] have this service retrieve the oldest entry available in your registration journal 
 * [4] you can then loop to retrieve them all following entries' `next` links
 
See our [`main()` Test Drive](./src/test/java/com/adobe/event/journal/JournalServiceTestDrive.java)
 

## Builds

This Library is build with [maven](https://maven.apache.org/) (it also runs the unit tests):

### Contributing

Contributions are welcomed! Read the [Contributing Guide](../.github/CONTRIBUTING.md) for more information.

### Licensing

This project is licensed under the Apache V2 License. See [LICENSE](../LICENSE.md) for more information.

  
