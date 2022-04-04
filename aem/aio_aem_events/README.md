# `aio-aem-events`

`aio-aem-events` is a AEM package containing all this sdk OSGI bundles, with their default
configurations, enabling AEM and Adobe I/O Events integration.

## Configuration

This package expects your Adobe Developer Console configured with OSGI configuration
see [core_aem documentation](../core_aem/README.md)

## Status Checks

This packages holds bundles that come with status servlet:
* from [/bin/aio/workspace.json](http://localhost:4502/bin/aio/workspace.json) 
you can `GET` the status of your workspace configuration (see [core_aem documentation](../core_aem/README.md))

http://localhost:4502/bin/aio/provider_config.json
http://localhost:4502/bin/aio/provider.json
http://localhost:4502/bin/aio/publish_event.json
http://localhost:4502/bin/aio/event_metadata.json
http://localhost:4502/bin/aio/osgi_event_metadata.json

## Builds

This Library is build with [maven](https://maven.apache.org/)

### Contributing

Contributions are welcomed! Read the [Contributing Guide](../.github/CONTRIBUTING.md) for more
information.

### Licensing

This project is licensed under the Apache V2 License. See [LICENSE](../LICENSE.md) for more
information.


  
