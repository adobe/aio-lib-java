# `aio-lib-java-events-xdm`

`aio-lib-java-events-xdm` is Adobe I/O - Java SDK - Jackson based implementation of the Adobe XDM Event Envelope Model.

This Adobe XDM Event Envelope Model is 
based on [json-ld w3c activity streams spec](https://github.com/w3c/activitystreams/blob/master/ns/activitystreams.jsonld),
for more detailed specifications, 
visit the [Adobe XDM event envelope schema](https://github.com/adobe/xdm/blob/master/archived/common/eventenvelope.schema.json)

## `json-ld` notes and pointers

Note that as we chose to serve the json-ld `@context` through link header [9] and keep fixed json-ld prefixes,
we based this implementation of plain and simple jackson [10] serialization,
otherwise for full-fledged json-ld implementation hydra [0] and jsonld-java [8] could have been used

* [0] http://www.hydra-cg.com/
* [1] https://github.com/dschulten/hydra-java
* [2] https://github.com/dschulten/hydra-java/tree/master/hydra-jsonld
* [3] https://json-ld.org/playground/
* [4] https://github.com/w3c/activitystreams/issues/134#issuecomment-108122077
* [5] https://github.com/w3c/activitystreams/issues/136
* [8] https://github.com/jsonld-java/jsonld-java
* [9] https://www.w3.org/TR/json-ld/#interpreting-json-as-json-ld
* [10] https://github.com/FasterXML/jackson-core

## Builds

This Library is build with [maven](https://maven.apache.org/) (it also runs the unit tests):

### Contributing

Contributions are welcomed! Read the [Contributing Guide](../.github/CONTRIBUTING.md) for more information.

### Licensing

This project is licensed under the Apache V2 License. See [LICENSE](../LICENSE.md) for more information.
