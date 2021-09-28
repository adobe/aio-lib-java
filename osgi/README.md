
# `com.adobe.aio-lib-java.osgi`

`com.adobe.aio-lib-java.osgi` is a Java OSGI bundle embedding all `aio-lib-java` libraries

### Test Drive

If you want to build and deploy this bundle from the source in your sling/crx/AEM osgi server.
You may override the default `pom.xml` sling/crx properties according to your needs:
* `crx.host`
* `crx.port`
* `crx.username`
* `crx.password`


and then run the following maven command (pick a path/`crx.path` where to deploy the bundle)

    mvn -Dcrx.path=/apps/changeMe/install -Dlicense.header.path=../copyright_header.txt -P localInstall clean install sling:install

## Builds

This Library is build with [maven](https://maven.apache.org/)

## Contributing

Contributions are welcomed! Read the [Contributing Guide](../.github/CONTRIBUTING.md) for more information.

## Licensing

This project is licensed under the Apache V2 License. See [LICENSE](../LICENSE.md) for more information.

  
