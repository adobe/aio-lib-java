# `aio-lib-java-core`

`aio-lib-java-core` is the Adobe I/O  - Java SDK - Core Library. 
This Java library holds various core utilities used across the other modules.
It also holds the core `Adobe Developer Console` Workspace Builder.

## Workspace

This library holds a [`Workspace`](./src/main/java/com/adobe/aio/Workspace.java) POJO modeling
your [Adobe Developer Console Project Workspace](https://www.adobe.io/apis/experienceplatform/console/docs.html#!AdobeDocs/adobeio-console/master/projects.md),

To get you started quickly you could use a `.properties` file, 
see our [sample config file](./src/test/resources/workspace.properties) and our JUnit Test

For now, you have a bit of copy and paste to do, but we have a plan to streamline the process:
* https://github.com/adobe/aio-lib-java/issues/5

## Builds

This Library is build with [maven](https://maven.apache.org/) (it also runs the unit tests):

### Contributing

Contributions are welcomed! Read the [Contributing Guide](../.github/CONTRIBUTING.md) for more information.

### Licensing

This project is licensed under the Apache V2 License. See [LICENSE](../LICENSE.md) for more information.
  

  
