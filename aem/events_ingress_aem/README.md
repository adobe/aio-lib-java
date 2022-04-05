# `aio-aem-events-publish`

`aio-aem-events-publish` is a Java OSGI bundle hosting OSGI Components
wrapping [`aio-lib-java-events-ingress`](../../events_ingress)

It hosts the services to
* publish events to Adobe I/O Events
  * either programmatically
  * or through Apache Sling Jobs (using the `aio/events` topic)

## Configuration

This bundle leverages
* [aio-aem-core](../core_aem) which expects a Workspace Adobe Developer Console `Workspace` Configuration
* [aio-aem-events-mgmt](../events_mgmt_aem) holding its own (optional) configurations

## Status Check

This bundle comes with a status endpoint:
from [/bin/aio/events/publish_ping.json](http://localhost:4502/bin/aio/events/publish_ping.json)
you can `GET` the status of the Adobe I/O Events Publish service 
(it will actually trigger the publication of a `ping` event)

The response json payload should like this:

    {
    "status": "up",
    "details": {
      "aio_event_provider_status": ...,
      "aio_ping_published": true,
      "aio_publish_url": "https://eventsingress.adobe.io",
      "workspace_status": ...
    },
    "error": null
    }


## Builds

This Library is build with [maven](https://maven.apache.org/)

### Contributing

Contributions are welcomed! Read the [Contributing Guide](../.github/CONTRIBUTING.md) for more
information.

### Licensing

This project is licensed under the Apache V2 License. See [LICENSE](../LICENSE.md) for more
information.


