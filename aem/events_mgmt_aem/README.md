# `aio-aem-events-mgmt`

`aio-aem-events-mgmt` is a Java OSGI bundle hosting OSGI Components
wrapping [`aio-lib-java-events-mgmt`](../../events_mgmt)

It hosts the services to
* register the AEM instance/cluster on which is is deployed as an Adobe I/O Events Provider
  * by default it will use the `author` [Externalizer](https://developer.adobe.com/experience-manager/reference-materials/6-5/javadoc/com/day/cq/commons/Externalizer.html) configuration to compute the AEM rootUrl, 
Adobe I/O Events provider instanceId, label and description
* register its various event metadata (see configuration below) against Adobe I/O Events

## Configuration

This bundle leverages [aio-aem-core](../core_aem) which expects a Workspace Adobe Developer Console `Workspace` Configuration

This bundle will look up [Adobe I/O Event Metadata OSGI configuration](src/main/java/com/adobe/aio/aem/event/management/ocd/EventMetadataConfig.java): 
to drive the event metadata registration against Adobe I/O Events.

Confer the [aem-io-events](../aio_aem_events) package, 
where we added a [`ping` Event Metadata configuration](../aio_aem_events/src/cs/content/jcr_root/apps/aio-aem-events/osgiconfig/config/com.adobe.aio.aem.event.management.internal.EventMetadataSupplierImpl-ping.cfg.json)

    {
    "aio.event.code": "ping",
    "aio.event.label": "ping",
    "aio.event.description": "ping event"
    }

For advanced use case you may also tune various other configurations: set for instance the externalizer name to `publish` instead of `author`.

## Status Check

This bundle comes with a few status endpoints:

### Adobe I/O Events Provider configuration check

From [bin/aio/events/provider_config.json](http://localhost:4502/bin/aio/events/provider_config.json)
you can `GET` the status of your Adobe I/O Events provider configuration.

The response json payload should like this:

    {
    "status": "up",
    "details": {
      "root_url": "https://author-p1234-e5678.adobeaemcloud.com/",
      "provider_input_model": {
        "label": "author-p1234-e5678",
        "description": "AEM author-p1234-e5678",
        "instance_id": "author-p1234-e5678",
        "provider_metadata": "aem",
        "docs_url": "https://developer.adobe.com/events/docs/guides/using/aem/"
      },
      "externalizer_name": "author"
    },
    "error": null
    }

### Adobe I/O Events Provider registration check

From [bin/aio/events/provider.json](http://localhost:4502/bin/aio/events/provider.json)
you can `GET` the status of your Adobe I/O Events provider registration.

The response json payload should like this:

    {
      "status": "up",
      "details": {
        "provider_already_registered": true,
        "registered_provider": {
          "id": "...",
          ...
          },
        ...
      },
      "error": null
    }

### Adobe I/O Events Event Metadata registration check

From [bin/aio/events/event_metadata.json](http://localhost:4502/bin/aio/events/event_metadata.json)
you can `GET` the status of your Adobe I/O Events Event Metadata registration.

The response json payload should like this:

    {
    "status": "up",
    "details": {
    "size": "12",
    "event_metadata": {
      "ping": {
        "configuredEventMetadata": {
        "event_code": "ping",
         ...
        },
        "registeredEventMetadata": {
        "event_code": "ping",
         ...
        },
        "error": null,
        "up": true
      },
      ...
    }
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


