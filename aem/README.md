# `aem`

## Modules

This project is the multi-modules project hosting Adobe I/O Events and AEM connectors and package, it contains:

* [`com.adobe.aio.aem.lib-osgi`](./lib_osgi) is a Java OSGI bundle embedding all the plain old Java `aio-lib-java-*` libraries (as well as their dependencies tree).
* [`com.adobe.aio.aem.core`](./core_aem) is a Java OSGI bundle hosting OSGI Components wrapping [`aio-lib-java-core`](../core) and [`aio-lib-java-ims`](../ims)
* [`com.adobe.aio.aem.event.management`](./events_mgmt_aem) is a Java OSGI bundle hosting OSGI Components wrapping [`aio-lib-java-events-mgmt`](../events_mgmt_aem)
* [`com.adobe.aio.aem.event.publish`](./events_ingress_aem) is a Java OSGI bundle hosting OSGI Components  wrapping [`aio-lib-java-events-ingress`](../events_ingress)
* [`aio-aem-events`](./aio-aem-events)] is an AEM package containing all the OSGI bundles listed above, with their default configuration, enabling AEM and Adobe I/O Events integration.

## Builds

This project is a [maven](https://maven.apache.org/) multi-module project.

### Contributing

Contributions are welcomed! Read the [Contributing Guide](./.github/CONTRIBUTING.md) for more information.

### Licensing

This project is licensed under the Apache V2 License. See [LICENSE](LICENSE.md) for more information.

### Support

This project is still in preliminary phase. No official support/SLA's is available for the SDK. 
