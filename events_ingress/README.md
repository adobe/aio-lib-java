
# `aio-lib-java-events-ingress`

`aio-lib-java-events-ingress` is Adobe I/O - Java SDK - Events Publishing Library.
This Java library wraps http API endpoints exposed 
by [`Adobe I/O Events` Publishing API](https://www.adobe.io/apis/experienceplatform/events/docs.html#!adobedocs/adobeio-events/master/api/eventsingress_api.md)


## PublishService Test Drive

          PublishService publishService = PublishService.builder()
                .workspace(workspace)// [1]
                .build(); //
          CloudEvent cloudEvent = publishService.publishCloudEvent(
              "your Adobe I/O Provider Id, 
              "your Adobe I/O Event Code", 
              "your Event payload" ); // [2]

 * [1] build your PublishService by passing your workspace context  (cf. the [aio-lib-java-core](../core) for workspace documentation)
 * [2] have this service publish a CloudEvent 
 
See our [`main()` Test Drive](./src/test/java/com/adobe/aio/event/publish/PublishServiceTestDrive.java)
 

## Builds
This Library is build with [maven](https://maven.apache.org/) (it also runs the unit tests):

### Contributing
Contributions are welcomed! Read the [Contributing Guide](../.github/CONTRIBUTING.md) for more information.

### Licensing
This project is licensed under the Apache V2 License. See [LICENSE](../LICENSE.md) for more information.

  
