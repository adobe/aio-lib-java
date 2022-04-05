# `aio-aem-events-osgi-mapping`

`aio-aem-events-osgi-mapping` is a Java OSGI bundle mapping osgi events to Adobe I/O Events 

It hosts the services to
* register various osgi/sling event handlers (according to osgi-aio events mapping configurations) 
  * mapping Osgi events to Adobe I/O Events
  * register accordingly these Events metadata to Adobe I/O Events
* publish these events to Adobe I/O Events

## Configuration

This bundle leverages
* [aio-aem-core](../core_aem) which expects a Workspace Adobe Developer Console `Workspace` Configuration
* [aio-aem-events-mgmt](../events_mgmt_aem) holding its own (optional) configurations
* [aio-aem-events-publish](../events_ingress_aem) 

This bundle will look up [Adobe I/O Events' OSGI Event Mapping configuration](src/main/java/com/adobe/aio/aem/event/osgimapping/ocd/OsgiEventMappingConfig.java):
to drive the Osgi events to Adobe I/O Events mapping.

Confer the [aem-io-events](../aio_aem_events) package, where we added the 
[Adobe I/O Events' OSGI Event Mapping configurations](../aio_aem_events/src/cs/content/jcr_root/apps/aio-aem-events/osgiconfig/config.author/) 
this bundle supports out-of-the-box for aem `author` instances

For advanced/other use cases you may add your own.

## Status Check

This bundle comes with a status endpoint:
from [/bin/aio/events/osgi_event_mapping.json](http://localhost:4502/bin/aio/events/osgi_event_mapping.json)
you can `GET` the status of your OSGI to Adobe I/O Events Mappings services.

The response json payload should like this:

    {
    "status": "up",
    "details": {
    "size": "11",
    "osgi_event_mappings": {
      "asset_created": {
        "osgiEventMapping": {
        "eventCode": "asset_created",
        "xdmEventType": "com.adobe.xdm.event.AemAssetCreatedEvent",
        "osgiTopic": "org/apache/sling/api/resource/Resource/ADDED",
        "osgiFilter": "(&(resourceType=dam:Asset)(!(event.application=*)))",
        "pathFilter": "/content/dam/",
        "eventHandlerType": "com.adobe.aio.aem.event.osgimapping.eventhandler.ResourceEventHandler"
        },
        "error": null,
        "up": true
      },
      "page_created": {
        "osgiEventMapping": {
        "eventCode": "page_created",
        "xdmEventType": "com.adobe.aio.aem.event.xdm.event.AemPageModificationEvent",
        "osgiTopic": "com/day/cq/wcm/core/page",
        "osgiFilter": "(!(event.application=*))",
        "pathFilter": "/content/",
        "eventHandlerType": "com.adobe.aio.aem.event.osgimapping.eventhandler.PageCreatedEventHandler"
        },
        "error": null,
        "up": true
      },
      "page_versioned": {
        "osgiEventMapping": {
        "eventCode": "page_versioned",
        "xdmEventType": "com.adobe.aio.aem.event.xdm.event.AemPageModificationEvent",
        "osgiTopic": "com/day/cq/wcm/core/page",
        "osgiFilter": "(!(event.application=*))",
        "pathFilter": "/content/",
        "eventHandlerType": "com.adobe.aio.aem.event.osgimapping.eventhandler.PageVersionedEventHandler"
        },
        "error": null,
        "up": true
      },
      "page_published": {
        "osgiEventMapping": {
        "eventCode": "page_published",
        "xdmEventType": "com.adobe.xdm.event.AemPagePublishedEvent",
        "osgiTopic": "com/day/cq/replication",
        "osgiFilter": "(&(type=ACTIVATE)(!(event.application=*)))",
        "pathFilter": "/content/",
        "eventHandlerType": "com.adobe.aio.aem.event.osgimapping.eventhandler.PageReplicationEventHandler"
        },
        "error": null,
        "up": true
      },
      "page_updated": {
        "osgiEventMapping": {
        "eventCode": "page_updated",
        "xdmEventType": "com.adobe.aio.aem.event.xdm.event.AemPageModificationEvent",
        "osgiTopic": "com/day/cq/wcm/core/page",
        "osgiFilter": "(!(event.application=*))",
        "pathFilter": "/content/",
        "eventHandlerType": "com.adobe.aio.aem.event.osgimapping.eventhandler.PageUpdatedEventHandler"
        },
        "error": null,
        "up": true
      },
      "com.adobe.aio.aem.osgi.custom": {
        "osgiEventMapping": {
        "eventCode": "com.adobe.aio.aem.osgi.custom",
        "xdmEventType": "com.adobe.xdm.event.OsgiEmittedEvent",
        "osgiTopic": "aio/event/custom",
        "osgiFilter": "",
        "pathFilter": "",
        "eventHandlerType": "com.adobe.aio.aem.event.osgimapping.eventhandler.OsgiEventHandler"
        },
        "error": null,
        "up": true
      },
      "page_moved": {
        "osgiEventMapping": {
        "eventCode": "page_moved",
        "xdmEventType": "com.adobe.aio.aem.event.xdm.event.AemPageModificationEvent",
        "osgiTopic": "com/day/cq/wcm/core/page",
        "osgiFilter": "(!(event.application=*))",
        "pathFilter": "/content/",
        "eventHandlerType": "com.adobe.aio.aem.event.osgimapping.eventhandler.PageMovedEventHandler"
        },
        "error": null,
        "up": true
      },
      "page_unpublished": {
        "osgiEventMapping": {
        "eventCode": "page_unpublished",
        "xdmEventType": "com.adobe.xdm.event.AemPageUnpublishedEvent",
        "osgiTopic": "com/day/cq/replication",
        "osgiFilter": "(&(type=DEACTIVATE)(!(event.application=*)))",
        "pathFilter": "/content/",
        "eventHandlerType": "com.adobe.aio.aem.event.osgimapping.eventhandler.PageReplicationEventHandler"
        },
        "error": null,
        "up": true
      },
      "asset_updated": {
        "osgiEventMapping": {
        "eventCode": "asset_updated",
        "xdmEventType": "com.adobe.xdm.event.AemAssetUpdatedEvent",
        "osgiTopic": "com/day/cq/dam",
        "osgiFilter": "(&(type=VERSIONED)(!(event.application=*)))",
        "pathFilter": "/content/dam/",
        "eventHandlerType": "com.adobe.aio.aem.event.osgimapping.eventhandler.DamEventHandler"
        },
        "error": null,
        "up": true
      },
      "asset_deleted": {
        "osgiEventMapping": {
        "eventCode": "asset_deleted",
        "xdmEventType": "com.adobe.xdm.event.AemAssetDeletedEvent",
        "osgiTopic": "com/day/cq/replication",
        "osgiFilter": "(&(type=DELETE)(!(event.application=*)))",
        "pathFilter": "/content/dam/",
        "eventHandlerType": "com.adobe.aio.aem.event.osgimapping.eventhandler.AssetReplicationEventHandler"
        },
        "error": null,
        "up": true
      },
      "page_deleted": {
        "osgiEventMapping": {
        "eventCode": "page_deleted",
        "xdmEventType": "com.adobe.aio.aem.event.xdm.event.AemPageModificationEvent",
        "osgiTopic": "com/day/cq/wcm/core/page",
        "osgiFilter": "(!(event.application=*))",
        "pathFilter": "/content/",
        "eventHandlerType": "com.adobe.aio.aem.event.osgimapping.eventhandler.PageDeletedEventHandler"
        },
        "error": null,
        "up": true
      }
      }
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


