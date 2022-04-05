# `aio-aem-events`

`aio-aem-events` is a AEM package containing all this sdk OSGI bundles, with their default
configurations, enabling AEM and Adobe I/O Events integration.

## Configuration

This package expects your Adobe Developer Console defined with an OSGI configuration
see [aio-aem-core documentation](../core_aem/README.md)

## Status Checks

This packages holds bundles that come with a few status endpoints
* [aio-aem-core](../core_aem) endpoints
  * from [/bin/aio/workspace.json](http://localhost:4502/bin/aio/workspace.json) 
you can `GET` the status of your workspace configuration 
* [aio-aem-events-mgmt](../events_mgmt_aem)  endpoints
  * from [bin/aio/events/provider_config.json](http://localhost:4502/bin/aio/events/provider_config.json)
you can `GET` the status of your Adobe I/O Events provider configuration,
  * from [bin/aio/events/provider.json](http://localhost:4502/bin/aio/events/provider.json)
you can `GET` the status of your Adobe I/O Events provider registration,
  * from [bin/aio/events/event_metadata.json](http://localhost:4502/bin/aio/events/event_metadata.json)
you can `GET` the status of your (12) Adobe I/O Events Event Metadata registration,
* [aio-aem-events-publish](../events_ingress_aem)  endpoint
  * from [/bin/aio/events/publish_ping.json](http://localhost:4502/bin/aio/events/publish_ping.json)
you can `GET` the status of the Adobe I/O Events Publish service
* [aio-aem-events-osgi-mapping](../events_osgi_mapping) endpoint
  * from [/bin/aio/events/osgi_event_mapping.json](http://localhost:4502/bin/aio/events/osgi_event_mapping.json)
you can `GET` the status of your OSGI to Adobe I/O Events Mappings services.
  
## Builds

This Library is build with [maven](https://maven.apache.org/)

### Contributing

Contributions are welcomed! Read the [Contributing Guide](../.github/CONTRIBUTING.md) for more
information.

### Licensing

This project is licensed under the Apache V2 License. See [LICENSE](../LICENSE.md) for more
information.


  
