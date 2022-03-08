/*
 * Copyright 2017 Adobe. All rights reserved.
 * This file is licensed to you under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License. You may obtain a copy
 * of the License at http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under
 * the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR REPRESENTATIONS
 * OF ANY KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package com.adobe.event.management;

import com.adobe.Workspace;
import com.adobe.event.management.model.EventMetadata;
import com.adobe.event.management.model.Provider;
import com.adobe.event.management.model.ProviderInputModel;
import com.adobe.ims.JWTAuthInterceptor;
import com.adobe.ims.util.PrivateKeyBuilder;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import feign.RequestInterceptor;
import java.security.PrivateKey;
import java.util.*;
import org.apache.commons.lang3.StringUtils;
import org.junit.Rule;
import org.junit.Test;
import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static junit.framework.TestCase.*;

public class ProviderServiceTestDrive {

  @Rule
  public WireMockRule wireMockRule = new WireMockRule(9999);

  @Test
  public void providerServiceTest() {

    Optional<Provider> provider;
    assertNotNull(getProviderService());

//    /** getProviders */
    stubFor(get(urlEqualTo("/events/2318/providers"))
            .willReturn(okJson("{\"_links\":{\"self\":{\"href\":\"localhost:9999/events/2318/providers\"}},\"_embedded\":{\"providers\":[{\"_links\":{\"rel:eventmetadata\":{\"href\":\"localhost:9999/events/providers/207aad29-f183-4c3d-87b5-22b21b8f0ee9/eventmetadata\"},\"self\":{\"href\":\"localhost:9999/events/providers/207aad29-f183-4c3d-87b5-22b21b8f0ee9\"}},\"id\":\"207aad29-f183-4c3d-87b5-22b21b8f0ee9\",\"label\":\"Analytics Triggers\",\"description\":\"Adobe Analytics Triggers events provider (gain insight into behavior of users on your site)\",\"source\":\"urn:uuid:207aad29-f183-4c3d-87b5-22b21b8f0ee9\",\"docs_url\":\"https://www.adobe.com/go/devs_events_triggers_docs\",\"publisher\":\"Adobe\"}]}}")
                    .withStatus(200)
            ));
    List<Provider> providers = getProviderService().getProviders();
    verify(getRequestedFor(urlEqualTo("/events/2318/providers")));
    assertNotNull(providers);
    assertEquals(1, providers.size());

//    /** findProviderById */
    stubFor(get(urlEqualTo("/events/providers/619363ad-2461-4c1c-bb09-848f8b4d0a30?eventmetadata=true"))
            .willReturn(okJson("{\"_links\":{\"rel:eventmetadata\":{\"href\":\"localhost:9999/events/providers/619363ad-2461-4c1c-bb09-848f8b4d0a30/eventmetadata\"},\"rel:update\":{\"href\":\"localhost:9999/events/2318/4566206088344551943/4566206088344552874/providers/619363ad-2461-4c1c-bb09-848f8b4d0a30\"},\"self\":{\"href\":\"localhost:9999/events/providers/619363ad-2461-4c1c-bb09-848f8b4d0a30\"}},\"id\":\"619363ad-2461-4c1c-bb09-848f8b4d0a30\",\"label\":\"Adobe XD\",\"description\":\"XD events provider\",\"source\":\"urn:uuid:619363ad-2461-4c1c-bb09-848f8b4d0a30\",\"docs_url\":\"https://www.adobe.com/go/devs_events_triggers_docs\",\"publisher\":\"01AB82@AdobeOrg\"}")
                    .withStatus(200)
            ));
    provider = getProviderService().findProviderById("619363ad-2461-4c1c-bb09-848f8b4d0a30"); //[3]
    verify(getRequestedFor(urlEqualTo("/events/providers/619363ad-2461-4c1c-bb09-848f8b4d0a30?eventmetadata=true")));
    assertEquals("619363ad-2461-4c1c-bb09-848f8b4d0a30", Objects.requireNonNull(provider.orElse(null)).getId());
    assertEquals("Adobe XD", provider.get().getLabel());
    assertEquals("XD events provider", provider.get().getDescription());
    assertEquals("urn:uuid:619363ad-2461-4c1c-bb09-848f8b4d0a30", provider.get().getSource());
    assertEquals("https://www.adobe.com/go/devs_events_triggers_docs", provider.get().getDocsUrl());
    assertEquals("01AB82@AdobeOrg", provider.get().getPublisher());

    stubFor(get(urlEqualTo("/events/providers/619363ad-2461-4c1c-bb09-848f8b4d0a30?eventmetadata=true"))
            .willReturn(aResponse()
            .withStatus(404)
            ));
    provider = getProviderService().findProviderById("619363ad-2461-4c1c-bb09-848f8b4d0a30"); //[3]
    verify(getRequestedFor(urlEqualTo("/events/providers/619363ad-2461-4c1c-bb09-848f8b4d0a30?eventmetadata=true")));
    assertEquals(Optional.empty(),provider);

//    /** findProviderBy */
    stubFor(get(urlEqualTo("/events/2318/providers?providerMetadataId=12345&instanceId=54321"))
            .willReturn(okJson("{\"_links\":{\"self\":{\"href\":\"localhost:9999/events/2318/providers\"}},\"_embedded\":{\"providers\":[{\"_links\":{\"rel:eventmetadata\":{\"href\":\"localhost:9999/events/providers/207aad29-f183-4c3d-87b5-22b21b8f0ee9/eventmetadata\"},\"self\":{\"href\":\"localhost:9999/events/providers/207aad29-f183-4c3d-87b5-22b21b8f0ee9\"}},\"id\":\"207aad29-f183-4c3d-87b5-22b21b8f0ee9\",\"label\":\"Analytics Triggers\",\"description\":\"Adobe Analytics Triggers events provider (gain insight into behavior of users on your site)\",\"source\":\"urn:uuid:207aad29-f183-4c3d-87b5-22b21b8f0ee9\",\"docs_url\":\"https://www.adobe.com/go/devs_events_triggers_docs\",\"publisher\":\"Adobe\"}]}}")
                    .withStatus(200)
            ));
    Optional<Provider> providersBy = getProviderService().findProviderBy("12345","54321"); //[3]
    verify(getRequestedFor(urlEqualTo("/events/2318/providers?providerMetadataId=12345&instanceId=54321")));
    assertNotNull(providersBy);

//    /** createProvider */
    ProviderInputModel providerCreateModel = ProviderInputModel.builder()
            .label("aio-lib-java Wiremock - test drive provider label")
            .description("aio-lib-java Wiremock - test drive provider description")
            .docsUrl("https://github.com/adobe/aio-lib-java/blob/main/events_mgmt/README.md#providerservice-test-drive")
            .build();
    stubFor(post(urlEqualTo("/events/2318/4566206088344551943/4566206088344552874/providers"))
            .withHeader("content-type", equalTo("application/json"))
            .willReturn(okJson("{\"_links\":{\"rel:eventmetadata\":{\"href\":\"localhost:9999/events/providers/619363ad-2461-4c1c-bb09-848f8b4d0a30/eventmetadata\"},\"rel:update\":{\"href\":\"localhost:9999/events/2318/4566206088344551943/4566206088344552874/providers/619363ad-2461-4c1c-bb09-848f8b4d0a30\"},\"self\":{\"href\":\"localhost:9999/events/providers/619363ad-2461-4c1c-bb09-848f8b4d0a30\"}},\"id\":\"619363ad-2461-4c1c-bb09-848f8b4d0a30\",\"label\":\"Adobe XD\",\"description\":\"XD events provider\",\"source\":\"urn:uuid:619363ad-2461-4c1c-bb09-848f8b4d0a30\",\"docs_url\":\"https://www.adobe.com/go/devs_events_triggers_docs\",\"publisher\":\"01AB82@AdobeOrg\"}")
                    .withStatus(201)
            ));
    provider = getProviderService().createProvider(providerCreateModel);
    verify(postRequestedFor(urlEqualTo("/events/2318/4566206088344551943/4566206088344552874/providers")));
    assertEquals("619363ad-2461-4c1c-bb09-848f8b4d0a30", Objects.requireNonNull(provider.orElse(null)).getId());
    assertEquals("Adobe XD", provider.get().getLabel());
    assertEquals("XD events provider", provider.get().getDescription());
    assertEquals("urn:uuid:619363ad-2461-4c1c-bb09-848f8b4d0a30", provider.get().getSource());
    assertEquals("https://www.adobe.com/go/devs_events_triggers_docs", provider.get().getDocsUrl());
    assertEquals("01AB82@AdobeOrg", provider.get().getPublisher());

//    /** createOrUpdateProvider */
    stubFor(post(urlEqualTo("/events/2318/4566206088344551943/4566206088344552874/providers"))
            .withHeader("content-type", equalTo("application/json"))
            .willReturn(okJson("{\"reason\":\"Use PUT or PATCH to update: Another provider exists with the same natural key (organization_id, provider_metadata/type, instanceId). Request id: Zl5ZErvdGJ2GiFXNq1X1fTEVQOuT16Xp.\", \"message\":\"619363ad-2461-4c1c-bb09-848f8b4d0a30\"}")
                    .withStatus(409)
            ));
    stubFor(put(urlEqualTo("/events/2318/4566206088344551943/4566206088344552874/providers/619363ad-2461-4c1c-bb09-848f8b4d0a30"))
            .withHeader("content-type", equalTo("application/json"))
            .willReturn(okJson("{\"_links\":{\"rel:eventmetadata\":{\"href\":\"localhost:9999/events/providers/619363ad-2461-4c1c-bb09-848f8b4d0a30/eventmetadata\"},\"rel:update\":{\"href\":\"localhost:9999/events/2318/4566206088344551943/4566206088344552874/providers/619363ad-2461-4c1c-bb09-848f8b4d0a30\"},\"self\":{\"href\":\"localhost:9999/events/providers/619363ad-2461-4c1c-bb09-848f8b4d0a30\"}},\"id\":\"619363ad-2461-4c1c-bb09-848f8b4d0a30\",\"label\":\"aio-lib-java Wiremock - test drive provider label\",\"description\":\"aio-lib-java Wiremock - test drive provider description\",\"source\":\"urn:uuid:619363ad-2461-4c1c-bb09-848f8b4d0a30\",\"docs_url\":\"https://github.com/adobe/aio-lib-java/blob/main/events_mgmt/README.md#providerservice-test-drive\",\"publisher\":\"01AB82@AdobeOrg\"}")
                    .withStatus(200)
            ));
    provider = getProviderService().createOrUpdateProvider(providerCreateModel);
    verify(postRequestedFor(urlEqualTo("/events/2318/4566206088344551943/4566206088344552874/providers")));
    verify(putRequestedFor(urlEqualTo("/events/2318/4566206088344551943/4566206088344552874/providers/619363ad-2461-4c1c-bb09-848f8b4d0a30")));
    assertEquals("619363ad-2461-4c1c-bb09-848f8b4d0a30", Objects.requireNonNull(provider.orElse(null)).getId());
    assertEquals("aio-lib-java Wiremock - test drive provider label", provider.get().getLabel());
    assertEquals("aio-lib-java Wiremock - test drive provider description", provider.get().getDescription());
    assertEquals("urn:uuid:619363ad-2461-4c1c-bb09-848f8b4d0a30", provider.get().getSource());
    assertEquals("https://github.com/adobe/aio-lib-java/blob/main/events_mgmt/README.md#providerservice-test-drive", provider.get().getDocsUrl());
    assertEquals("01AB82@AdobeOrg", provider.get().getPublisher());

//    /** updateProvider */
    stubFor(put(urlEqualTo("/events/2318/4566206088344551943/4566206088344552874/providers/619363ad-2461-4c1c-bb09-848f8b4d0a30"))
            .withHeader("content-type", equalTo("application/json"))
            .willReturn(okJson("{\"_links\":{\"rel:eventmetadata\":{\"href\":\"localhost:9999/events/providers/619363ad-2461-4c1c-bb09-848f8b4d0a30/eventmetadata\"},\"rel:update\":{\"href\":\"localhost:9999/events/2318/4566206088344551943/4566206088344552874/providers/619363ad-2461-4c1c-bb09-848f8b4d0a30\"},\"self\":{\"href\":\"localhost:9999/events/providers/619363ad-2461-4c1c-bb09-848f8b4d0a30\"}},\"id\":\"619363ad-2461-4c1c-bb09-848f8b4d0a30\",\"label\":\"aio-lib-java Wiremock - test drive provider label\",\"description\":\"aio-lib-java Wiremock - test drive provider description\",\"source\":\"urn:uuid:619363ad-2461-4c1c-bb09-848f8b4d0a30\",\"docs_url\":\"https://github.com/adobe/aio-lib-java/blob/main/events_mgmt/README.md#providerservice-test-drive\",\"publisher\":\"01AB82@AdobeOrg\"}")
                    .withStatus(200)
            ));
    provider = getProviderService().updateProvider("619363ad-2461-4c1c-bb09-848f8b4d0a30",providerCreateModel);
    verify(putRequestedFor(urlEqualTo("/events/2318/4566206088344551943/4566206088344552874/providers/619363ad-2461-4c1c-bb09-848f8b4d0a30")));
    assertEquals("619363ad-2461-4c1c-bb09-848f8b4d0a30", Objects.requireNonNull(provider.orElse(null)).getId());
    assertEquals("aio-lib-java Wiremock - test drive provider label", provider.get().getLabel());
    assertEquals("aio-lib-java Wiremock - test drive provider description", provider.get().getDescription());
    assertEquals("urn:uuid:619363ad-2461-4c1c-bb09-848f8b4d0a30", provider.get().getSource());
    assertEquals("https://github.com/adobe/aio-lib-java/blob/main/events_mgmt/README.md#providerservice-test-drive", provider.get().getDocsUrl());
    assertEquals("01AB82@AdobeOrg", provider.get().getPublisher());

    stubFor(put(urlEqualTo("/events/2318/4566206088344551943/4566206088344552874/providers/619363ad-2461-4c1c-bb09-848f8b4d0a30"))
            .withHeader("content-type", equalTo("application/json"))
            .willReturn(aResponse()
                    .withStatus(404)
            ));
    provider = getProviderService().updateProvider("619363ad-2461-4c1c-bb09-848f8b4d0a30", providerCreateModel);
    verify(putRequestedFor(urlEqualTo("/events/2318/4566206088344551943/4566206088344552874/providers/619363ad-2461-4c1c-bb09-848f8b4d0a30")));
    assertEquals(Optional.empty(),provider);

//    /** deleteProvider */
    stubFor(delete(urlEqualTo("/events/2318/4566206088344551943/4566206088344552874/providers/619363ad-2461-4c1c-bb09-848f8b4d0a30"))
            .willReturn(aResponse()
                    .withStatus(204)
            ));
    getProviderService().deleteProvider("619363ad-2461-4c1c-bb09-848f8b4d0a30");
    verify(deleteRequestedFor(urlEqualTo("/events/2318/4566206088344551943/4566206088344552874/providers/619363ad-2461-4c1c-bb09-848f8b4d0a30")));

  }

  @Test
  public void eventMetadataServiceTest() {

    Optional<EventMetadata> metadata;

//    /** getEventMetadata */
    stubFor(get(urlEqualTo("/events/providers/619363ad-2461-4c1c-bb09-848f8b4d0a30/eventmetadata/wiremock_eventcode"))
            .willReturn(okJson("{\"_links\":{\"rel:sample_event\":{\"href\":\"localhost:9999/events/providers/619363ad-2461-4c1c-bb09-848f8b4d0a30/eventmetadata/wiremock_eventcode/sample_event\"},\"rel:update\":{\"href\":\"localhost:9999/events/2318/4566206088344551943/4566206088344552874/providers/619363ad-2461-4c1c-bb09-848f8b4d0a30/eventmetadata/wiremock_eventcode\"},\"self\":{\"href\":\"localhost:9999/events/providers/619363ad-2461-4c1c-bb09-848f8b4d0a30/eventmetadata/wiremock_eventcode\"}},\"description\":\"wiremock test description\",\"label\":\"test label\",\"event_code\":\"wiremock_eventcode\"}")
                    .withStatus(200)
            ));
    metadata = getProviderService().getEventMetadata("619363ad-2461-4c1c-bb09-848f8b4d0a30", "wiremock_eventcode");
    verify(getRequestedFor(urlEqualTo("/events/providers/619363ad-2461-4c1c-bb09-848f8b4d0a30/eventmetadata/wiremock_eventcode")));
    assertEquals("wiremock_eventcode", Objects.requireNonNull(metadata.orElse(null)).getEventCode());
    assertEquals("wiremock test description", metadata.get().getDescription());
    assertEquals("test label", metadata.get().getLabel());

    stubFor(get(urlEqualTo("/events/providers/619363ad-2461-4c1c-bb09-848f8b4d0a30/eventmetadata"))
            .willReturn(okJson("{\"_links\":{\"self\":{\"href\":\"localhost:9999/events/providers/619363ad-2461-4c1c-bb09-848f8b4d0a30/eventmetadata\"}},\"_embedded\":{\"eventmetadata\":[{\"_links\":{\"rel:sample_event\":{\"href\":\"localhost:9999/events/providers/619363ad-2461-4c1c-bb09-848f8b4d0a30/eventmetadata/wiremock_eventcode/sample_event\"},\"rel:update\":{\"href\":\"localhost:9999/events/2318/4566206088344551943/4566206088344552874/providers/619363ad-2461-4c1c-bb09-848f8b4d0a30/eventmetadata/wiremock_eventcode\"},\"self\":{\"href\":\"localhost:9999/events/providers/619363ad-2461-4c1c-bb09-848f8b4d0a30/eventmetadata/wiremock_eventcode\"}},\"description\":\"wiremock test description\",\"label\":\"test label\",\"event_code\":\"wiremock_eventcode\"}]}}")
                    .withStatus(200)
            ));
    List<EventMetadata> metadataList = getProviderService().getEventMetadata("619363ad-2461-4c1c-bb09-848f8b4d0a30");
    verify(getRequestedFor(urlEqualTo("/events/providers/619363ad-2461-4c1c-bb09-848f8b4d0a30/eventmetadata")));
    assertEquals(1, metadataList.size());

//    /** createEventMetadata */
    EventMetadata eventMetadata = EventMetadata.builder()
            .eventCode("wiremock_eventcode")
            .description("wiremock test description")
            .label("test label")
            .build();
    stubFor(post(urlEqualTo("/events/2318/4566206088344551943/4566206088344552874/providers/619363ad-2461-4c1c-bb09-848f8b4d0a30/eventmetadata"))
            .withHeader("content-type", equalTo("application/json"))
            .willReturn(okJson("{\"_links\":{\"rel:sample_event\":{\"href\":\"localhost:9999/events/providers/619363ad-2461-4c1c-bb09-848f8b4d0a30/eventmetadata/wiremock_eventcode/sample_event\"},\"rel:update\":{\"href\":\"localhost:9999/events/2318/4566206088344551943/4566206088344552874/providers/619363ad-2461-4c1c-bb09-848f8b4d0a30/eventmetadata/wiremock_eventcode\"},\"self\":{\"href\":\"localhost:9999/events/providers/619363ad-2461-4c1c-bb09-848f8b4d0a30/eventmetadata/wiremock_eventcode\"}},\"description\":\"wiremock test description\",\"label\":\"test label\",\"event_code\":\"wiremock_eventcode\"}")
                    .withStatus(200)
            ));
    metadata = getProviderService().createEventMetadata("619363ad-2461-4c1c-bb09-848f8b4d0a30", eventMetadata);
    verify(postRequestedFor(urlEqualTo("/events/2318/4566206088344551943/4566206088344552874/providers/619363ad-2461-4c1c-bb09-848f8b4d0a30/eventmetadata")));
    assertEquals("wiremock_eventcode", Objects.requireNonNull(metadata.orElse(null)).getEventCode());
    assertEquals("wiremock test description", metadata.get().getDescription());
    assertEquals("test label", metadata.get().getLabel());

//    /** updateEventMetadata */
    stubFor(put(urlEqualTo("/events/2318/4566206088344551943/4566206088344552874/providers/619363ad-2461-4c1c-bb09-848f8b4d0a30/eventmetadata/wiremock_eventcode"))
            .withHeader("content-type", equalTo("application/json"))
            .willReturn(okJson("{\"_links\":{\"rel:sample_event\":{\"href\":\"localhost:9999/events/providers/619363ad-2461-4c1c-bb09-848f8b4d0a30/eventmetadata/wiremock_eventcode/sample_event\"},\"rel:update\":{\"href\":\"localhost:9999/events/2318/4566206088344551943/4566206088344552874/providers/619363ad-2461-4c1c-bb09-848f8b4d0a30/eventmetadata/wiremock_eventcode\"},\"self\":{\"href\":\"localhost:9999/events/providers/619363ad-2461-4c1c-bb09-848f8b4d0a30/eventmetadata/wiremock_eventcode\"}},\"description\":\"wiremock test description\",\"label\":\"test label\",\"event_code\":\"wiremock_eventcode\"}")
                    .withStatus(200)
            ));
    metadata = getProviderService().updateEventMetadata("619363ad-2461-4c1c-bb09-848f8b4d0a30", eventMetadata);
    verify(putRequestedFor(urlEqualTo("/events/2318/4566206088344551943/4566206088344552874/providers/619363ad-2461-4c1c-bb09-848f8b4d0a30/eventmetadata/wiremock_eventcode")));
    assertEquals("wiremock_eventcode", Objects.requireNonNull(metadata.orElse(null)).getEventCode());
    assertEquals("wiremock test description", metadata.get().getDescription());
    assertEquals("test label", metadata.get().getLabel());

    stubFor(put(urlEqualTo("/events/2318/4566206088344551943/4566206088344552874/providers/619363ad-2461-4c1c-bb09-848f8b4d0a30/eventmetadata/wiremock_eventcode"))
            .withHeader("content-type", equalTo("application/json"))
            .willReturn(aResponse()
                    .withStatus(404)
            ));
    metadata = getProviderService().updateEventMetadata("619363ad-2461-4c1c-bb09-848f8b4d0a30", eventMetadata);
    verify(putRequestedFor(urlEqualTo("/events/2318/4566206088344551943/4566206088344552874/providers/619363ad-2461-4c1c-bb09-848f8b4d0a30/eventmetadata/wiremock_eventcode")));
    assertEquals(Optional.empty(), metadata);

//    /** deleteEventMetadata */
    stubFor(delete(urlEqualTo("/events/2318/4566206088344551943/4566206088344552874/providers/619363ad-2461-4c1c-bb09-848f8b4d0a30/eventmetadata"))
            .willReturn(aResponse()
                    .withStatus(204)
            ));
    getProviderService().deleteAllEventMetadata("619363ad-2461-4c1c-bb09-848f8b4d0a30");
    verify(deleteRequestedFor(urlEqualTo("/events/2318/4566206088344551943/4566206088344552874/providers/619363ad-2461-4c1c-bb09-848f8b4d0a30/eventmetadata")));

    stubFor(delete(urlEqualTo("/events/2318/4566206088344551943/4566206088344552874/providers/619363ad-2461-4c1c-bb09-848f8b4d0a30/eventmetadata/wiremock_eventcode"))
            .willReturn(aResponse()
                    .withStatus(204)
            ));
    getProviderService().deleteEventMetadata("619363ad-2461-4c1c-bb09-848f8b4d0a30", "wiremock_eventcode");
    verify(putRequestedFor(urlEqualTo("/events/2318/4566206088344551943/4566206088344552874/providers/619363ad-2461-4c1c-bb09-848f8b4d0a30/eventmetadata/wiremock_eventcode")));

  }

  private ProviderService getProviderService(){

    PrivateKey privateKey = new PrivateKeyBuilder().encodePkcs8Key("MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCazh+VvC9xqWi1VzZj6FtOZNhTwPo6nckpFyDu6B79eXvm/RA5wVha78mKg0jQFxWs6ZqwyrtCIeRZq62lWrEaiSFEblkWTNsVZ6fhaNtOh7JMPwUheZIa4/d3EAyxEkaymvkpLcjDN3js3Q5hnWaRFZao8lojRjaH+wNWGj1Dw8pzk5scmnltTcUCSnhzB7ZiHLsSIZ+Pv3xek8hSKRQjLenb2cCEjwm1KM/QbUWivRo2upJL6oJzc+31lqAe2aqVMEtLFqnUT4oCa3RMRPgAp1LIpHHhqs9/IJUXX5bJ8Em8EH7epjnEi5GZ9ZbMW7PLRgceUETggpBWp1qciXaVAgMBAAECggEARsJY5ZRzCz0oQ1tt3RTkR10JFJ9swUZGIKYVw54OLEZPQDIELKIXxNk+AjYoHhWvLq5Iqu6/0Wa0fdhfMunVcg+kSSc3SV4v9gS/U+Ud+TNFaFyV98sd4XS6NI39fyKfdhwoL45h6fl9KKeSX0QXEdIQX4EHeoAphNZTnBO6VTJ/YhN8/cxl7brykBIDNubk3eJ8bsQ4o6FCc8Hq1QIb//xAA9uZiAMCuZOdsTTpWkCKFEUQchyxpy+PbAR6qxjEUr5lQjTfLoq7eCTnZu4yKRRYiY/v3YyRJ5Rlgg7FjZCbBTunCKPnNolNY1sBQpcFRY9eUTCzwDuuzuPnEldpMQKBgQDJuOlmpTtqqCSr8Ae/FD5yHUFcMHngd2UrC6S+l4PYuFTCfXHTe6hPsuMBcy6yfqIDvHLgvCzs5ZwctKdnAO0zDFPhOu929mxO7h8P/fw+1lv2QU95FCs2KPnI8RB8uXfMhbBNxDs/cWRoXpVS3P93h/otlXO+z8zDg2bR9XGE1wKBgQDEdXP2uE7baIzPAQ+6fZFGsHjVhYZ089QhOBi4PrAphYJcqO3hCilTqJblQpff0ltiUuhzzIsQIxPjwCHKrHk9Skgx3D6RfdmS5/5SFmFk+j7Eops06zM+goIpG5KvlHgBaxkWgYY5KtikbG0Lc0WbbLifYj4TROnEZOJ6cohGcwKBgQC5dhaw1q1gDCNbKR4WIaigBiG3fqIvK9aJ0vSufmMr952GCwuB4qkGTXPEO3/tf9u5D6OW16t+SkRTaAyY+RMb4fOkmijb+QfvMaLBc0RdCXwXVkiZC2AHNXkhs/Dymxp3oVpMxWOrmrcz9fHX83O1FAGBs2xtPGQIRWFdHAo4lQKBgETGlX020qxu8nR5e6ce1F/54aNmZkbFIWsrt0Ow9nzit1t27CgPJZ6a85B4+rApdUJ7odMANWLF1O2zUmEgdiUlvxZtcx39/9A1FUxpd1khXh36ivlAqaTljWmUtIpxIH3mn1bIq1OSE1ukdZw/k4uwyQVLIE4gnvHZG4wgUmLPAoGBAJZQclY6S26h43H9L4cZXIBZeDwO2MRcGN55XlSXMSBkHMJYxpLaHEqlv5MTPPc+Cog/hG0jRVz7Yk4EaUAAdJ3CRrSV2uaGXZHEj5fGDZhB3gQ3FjQZYndz51fCwl9IhMz3NRqO0VfNqdBNrcSH0eZoHUA15kjy+IMwnfmNrdl4").build();
    Map<String, String> map = new HashMap<>();
    map.put(Workspace.API_KEY, "0914e5540cb34ce28c80e5b27c99a12a");
    map.put(Workspace.CLIENT_SECRET, "P9sdSkY1r5NJ5wPAYMssdeb7");
    map.put(Workspace.CONSUMER_ORG_ID, "2318");
    map.put(Workspace.CREDENTIAL_ID, "60771");
    map.put(Workspace.IMS_ORG_ID, "01AB82@AdobeOrg");
    map.put(Workspace.IMS_URL, "http://localhost:9999");
    map.put(Workspace.PROJECT_ID, "4566206088344551943");
    map.put(Workspace.TECHNICAL_ACCOUNT_ID, "ABC123321CBA@techacct.adobe.com");
    map.put(Workspace.WORKSPACE_ID, "4566206088344552874");
    String metaScopes = StringUtils.join("/s/event_receiver_api, /s/ent_adobeio_sdk",',');
    map.put(Workspace.META_SCOPES, metaScopes);

    Workspace workspace = Workspace.builder()
            .configMap(map)
            .privateKey(privateKey)
            .build();

    stubFor(post(urlEqualTo("/ims/exchange/jwt"))
            .willReturn(okJson("{\n" +
                    "  \"expires_in\": 86399761,\n" +
                    "  \"token_type\": \"bearer\",\n" +
                    "  \"access_token\": \"eyJhbGciOiJSUzI1NiIsIng1dSI6Imltcy1rZXktMS5jZXIifQ.eyJpZCI6IjE0NDIyNzk3NjUwNDEtMzRhOWMwZjktNmU1Yi00MmJmLWI2N2MtZTk5OWE2MzA1NDVlIiwibW9pIjoiZWQ4ZGYyNDUiLCJzY29wZSI6ImNyZWF0ZWl2ZV9jbG91ZCxBZG9iZUlkLHJlYWRfb3JnYW5pemF0aW9ucyxvcGVuaWQiLCJjIjoiRGpXZlB0d0lta0x1eTJPNWJ5OGFGZz09IiwiYXMiOiJpbXMtbmExLXFhMiIsImNyZWF0ZWRfYXQiOiIxNDQyMjc5NzY1MDQxIiwiZXhwaXJlc19pbiI6Ijg2NDAwMDAwIiwidXNlcl9pZCI6IjBEQ0U2MTNBNTVFRkJDRUQ3RjAwMDEwMUB0ZWNoYWNjdC5hZG9iZS5jb20iLCJjbGllbnRfaWQiOiJ0ZXN0LXRlY2giLCJ0eXBlIjoiYWNjZXNzX3Rva2VuIn0.KTPsnDJI4tPJ7zbwYMDBG-FUSqxTb4Jh7qTFZIGEARJlUtR9fv_sLxCCtuu8FzTumvANm7yMD2H3WEqDyU4JPIctPfzQFqpQdcygSL4UrEEAIEwVqZN_7oTTCWb3lBVCemVX3cv27HCrpEOZ_LDT7W4hchpnbRHxj32_rI-RLhacj9Um8qHvv7wyxfyYtsb81Vs9__kDUeDk0YN2irpYj2LNkCf44vW-z4m6F-nBN8ntTG94D530f9EslP1NYqkwebIgKfondz01Lxty2TLFrf0Kn6QDgrM1rHGh61vqeVToeVrZAsQW17fSz1yjCibN9xbGaFwMUwfBj5b1656Nvg\"\n" +
                    "}")
                    .withStatus(200)
            ));
    RequestInterceptor authInterceptor = JWTAuthInterceptor.builder()
            .workspace(workspace)
            .build();

    return ProviderService.builder()
            .authInterceptor(authInterceptor)
            .workspace(workspace)
            .url("http://localhost:9999")
            .build();
  }

}
