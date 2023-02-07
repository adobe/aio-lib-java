# `aio-lib-java-events-webhook`

`aio-lib-java-events-webhook` is Adobe I/O - Java SDK - Events Webhook Library, 
a reusable implementation of [Adobe I/O Events Webhook Signature Verification](https://developer.adobe.com/events/docs/guides/#security-considerations).

## Test Drive

    boolean result =  new EventVerifier().verify(eventPayload, apiKey, httpRequestHeaders);

The above one-liner implements all Adobe I/O Events event payload signature verifications, read `EventVerifier` javadocs for more details.

## Builds

This Library is build with [maven](https://maven.apache.org/) (it also runs the unit tests):

### Contributing

Contributions are welcomed! Read the [Contributing Guide](../.github/CONTRIBUTING.md) for more information.

### Licensing

This project is licensed under the Apache V2 License. See [LICENSE](../LICENSE.md) for more information.

  
