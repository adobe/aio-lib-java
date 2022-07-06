## Adobe I/O - Java SDK

This SDK is composed of a few Java Libraries :
* `aio-lib-java-core`  holds the core models, builders, utilities used across the other libraries below,
* `aio-lib-java-ims` is a library wrapping a subset of [Adobe Identity Management System (IMS) API](https://www.adobe.io/authentication/auth-methods.html#!AdobeDocs/adobeio-auth/master/AuthenticationOverview/AuthenticationGuide.md)
* `aio-lib-java-events-mgmt` is a library wrapping [Adobe I/O Events Provider and Registration API](https://www.adobe.io/apis/experienceplatform/events/docs.html#!adobedocs/adobeio-events/master/api/api.md)
* `aio-lib-java-events-ingress` is a library wrapping [Adobe I/O Events Publishing API](https://www.adobe.io/apis/experienceplatform/events/docs.html#!adobedocs/adobeio-events/master/api/eventsingress_api.md)
* `aio-lib-java-events-journal` is a library wrapping [Adobe I/O Events Journaling API](https://www.adobe.io/apis/experienceplatform/events/docs.html#!adobedocs/adobeio-events/master/api/journaling_api.md)
* as well as `aem` a sub-module, home of various [Adobe I/O Events and AEM osgi connectors and packages](https://developer.adobe.com/events/docs/guides/using/aem/)

### Maven Dependency

Include via Maven:

Example:
```
<dependency>
    <groupId>com.adobe.aio</groupId>
    <artifactId>aio-lib-java-ims</artifactId>
    <version>0.0.46</version>
</dependency>
```

See the [JavaDocs](https://opensource.adobe.com/aio-lib-java/apidocs/) for the API.
