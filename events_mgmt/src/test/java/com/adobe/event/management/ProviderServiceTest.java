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

import com.adobe.event.management.model.EventMetadata;
import com.adobe.event.management.model.Provider;
import com.adobe.event.management.model.ProviderInputModel;
import com.adobe.ims.JWTAuthInterceptor;
import com.adobe.ims.util.TestUtil;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import feign.RequestInterceptor;
import org.junit.Rule;
import org.junit.Test;
import java.util.*;
import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;

public class ProviderServiceTest {

  private static final String PROVIDER_ID = "619363ad-2461-4c1c-bb09-848f8b4d0a30";
  private static final String SOURCE_ID = "urn:uuid:619363ad-2461-4c1c-bb09-848f8b4d0a30";

  private static final String PROVIDER_MODEL_LABEL = "aio-lib-java Wiremock - test drive provider label";
  private static final String PROVIDER_MODEL_DESCRIPTION = "aio-lib-java Wiremock - test drive provider description";
  private static final String PROVIDER_MODEL_DOCS_URL = "https://localhost.com/test";

  private static final String METADATA_EVENTCODE = "wiremock_eventcode";
  private static final String METADATA_DESCRIPTION = "wiremock test description";
  private static final String METADATA_LABEL = "test label";

  @Rule
  public WireMockRule wireMockRule = new WireMockRule(9999);

  @Test
  public void providerServiceTest() {

    Optional<Provider> provider;
    assertNotNull(getProviderService());

    ProviderInputModel providerCreateModel = ProviderInputModel.builder()
            .label(PROVIDER_MODEL_LABEL)
            .description(PROVIDER_MODEL_DESCRIPTION)
            .docsUrl(PROVIDER_MODEL_DOCS_URL)
            .build();

//    /** getProviders */
    stubFor(get(urlEqualTo("/events/someConsumerOrgId/providers"))
            .willReturn(okJson("{\"_links\":{\"self\":{\"href\":\"localhost:9999/events/someConsumerOrgId/providers\"}},\"_embedded\":{\"providers\":[{\"_links\":{\"rel:eventmetadata\":{\"href\":\"localhost:9999/events/providers/207aad29-f183-4c3d-87b5-22b21b8f0ee9/eventmetadata\"},\"self\":{\"href\":\"localhost:9999/events/providers/207aad29-f183-4c3d-87b5-22b21b8f0ee9\"}},\"id\":\"207aad29-f183-4c3d-87b5-22b21b8f0ee9\",\"label\":\"Analytics Triggers\",\"description\":\"Adobe Analytics Triggers events provider (gain insight into behavior of users on your site)\",\"source\":\"urn:uuid:207aad29-f183-4c3d-87b5-22b21b8f0ee9\",\"docs_url\":\"https://localhost.com/test\",\"publisher\":\"Adobe\"}]}}")
                    .withStatus(200)
            ));
    List<Provider> providers = getProviderService().getProviders();
    verify(getRequestedFor(urlEqualTo("/events/someConsumerOrgId/providers")));
    assertNotNull(providers);
    assertEquals(1, providers.size());

//    /** findProviderById */
    stubFor(get(urlEqualTo("/events/providers/619363ad-2461-4c1c-bb09-848f8b4d0a30?eventmetadata=true"))
            .willReturn(okJson("{\"_links\":{\"rel:eventmetadata\":{\"href\":\"localhost:9999/events/providers/619363ad-2461-4c1c-bb09-848f8b4d0a30/eventmetadata\"},\"rel:update\":{\"href\":\"localhost:9999/events/someConsumerOrgId/someProjectId/someWorkspaceId/providers/619363ad-2461-4c1c-bb09-848f8b4d0a30\"},\"self\":{\"href\":\"localhost:9999/events/providers/619363ad-2461-4c1c-bb09-848f8b4d0a30\"}},\"id\":\"619363ad-2461-4c1c-bb09-848f8b4d0a30\",\"label\":\"aio-lib-java Wiremock - test drive provider label\",\"description\":\"aio-lib-java Wiremock - test drive provider description\",\"source\":\"urn:uuid:619363ad-2461-4c1c-bb09-848f8b4d0a30\",\"docs_url\":\"https://localhost.com/test\",\"publisher\":\"someImsOrgId\"}")
                    .withStatus(200)
            ));
    provider = getProviderService().findProviderById(PROVIDER_ID);
    verify(getRequestedFor(urlEqualTo("/events/providers/619363ad-2461-4c1c-bb09-848f8b4d0a30?eventmetadata=true")));
    assertEquals(PROVIDER_ID, Objects.requireNonNull(provider.orElse(null)).getId());
    assertEquals(PROVIDER_MODEL_LABEL, provider.get().getLabel());
    assertEquals(PROVIDER_MODEL_DESCRIPTION, provider.get().getDescription());
    assertEquals(SOURCE_ID, provider.get().getSource());
    assertEquals(PROVIDER_MODEL_DOCS_URL, provider.get().getDocsUrl());
    assertEquals(TestUtil.IMS_ORG_ID, provider.get().getPublisher());

    stubFor(get(urlEqualTo("/events/providers/619363ad-2461-4c1c-bb09-848f8b4d0a30?eventmetadata=true"))
            .willReturn(aResponse()
            .withStatus(404)
            ));
    provider = getProviderService().findProviderById(PROVIDER_ID); //[3]
    verify(getRequestedFor(urlEqualTo("/events/providers/619363ad-2461-4c1c-bb09-848f8b4d0a30?eventmetadata=true")));
    assertEquals(Optional.empty(),provider);

//    /** findProviderBy */
    stubFor(get(urlEqualTo("/events/someConsumerOrgId/providers?providerMetadataId=12345&instanceId=54321"))
            .willReturn(okJson("{\"_links\":{\"self\":{\"href\":\"localhost:9999/events/someConsumerOrgId/providers\"}},\"_embedded\":{\"providers\":[{\"_links\":{\"rel:eventmetadata\":{\"href\":\"localhost:9999/events/providers/207aad29-f183-4c3d-87b5-22b21b8f0ee9/eventmetadata\"},\"self\":{\"href\":\"localhost:9999/events/providers/207aad29-f183-4c3d-87b5-22b21b8f0ee9\"}},\"id\":\"207aad29-f183-4c3d-87b5-22b21b8f0ee9\",\"label\":\"Analytics Triggers\",\"description\":\"Adobe Analytics Triggers events provider (gain insight into behavior of users on your site)\",\"source\":\"urn:uuid:207aad29-f183-4c3d-87b5-22b21b8f0ee9\",\"docs_url\":\"https://localhost.com/test\",\"publisher\":\"Adobe\"}]}}")
                    .withStatus(200)
            ));
    Optional<Provider> providersBy = getProviderService().findProviderBy("12345","54321"); //[3]
    verify(getRequestedFor(urlEqualTo("/events/someConsumerOrgId/providers?providerMetadataId=12345&instanceId=54321")));
    assertNotNull(providersBy);

//    /** createProvider */
    stubFor(post(urlEqualTo("/events/someConsumerOrgId/someProjectId/someWorkspaceId/providers"))
            .withHeader(TestUtil.CONTENT_TYPE_HEADER, equalTo(TestUtil.CONTENT_TYPE_HEADER_VALUE))
            .willReturn(okJson("{\"_links\":{\"rel:eventmetadata\":{\"href\":\"localhost:9999/events/providers/619363ad-2461-4c1c-bb09-848f8b4d0a30/eventmetadata\"},\"rel:update\":{\"href\":\"localhost:9999/events/someConsumerOrgId/someProjectId/someWorkspaceId/providers/619363ad-2461-4c1c-bb09-848f8b4d0a30\"},\"self\":{\"href\":\"localhost:9999/events/providers/619363ad-2461-4c1c-bb09-848f8b4d0a30\"}},\"id\":\"619363ad-2461-4c1c-bb09-848f8b4d0a30\",\"label\":\"aio-lib-java Wiremock - test drive provider label\",\"description\":\"aio-lib-java Wiremock - test drive provider description\",\"source\":\"urn:uuid:619363ad-2461-4c1c-bb09-848f8b4d0a30\",\"docs_url\":\"https://localhost.com/test\",\"publisher\":\"someImsOrgId\"}")
                    .withStatus(201)
            ));
    provider = getProviderService().createProvider(providerCreateModel);
    verify(postRequestedFor(urlEqualTo("/events/someConsumerOrgId/someProjectId/someWorkspaceId/providers")));
    assertEquals(PROVIDER_ID, Objects.requireNonNull(provider.orElse(null)).getId());
    assertEquals(PROVIDER_MODEL_LABEL, provider.get().getLabel());
    assertEquals(PROVIDER_MODEL_DESCRIPTION, provider.get().getDescription());
    assertEquals(SOURCE_ID, provider.get().getSource());
    assertEquals(PROVIDER_MODEL_DOCS_URL, provider.get().getDocsUrl());
    assertEquals(TestUtil.IMS_ORG_ID, provider.get().getPublisher());

//    /** createOrUpdateProvider */
    stubFor(post(urlEqualTo("/events/someConsumerOrgId/someProjectId/someWorkspaceId/providers"))
            .withHeader(TestUtil.CONTENT_TYPE_HEADER, equalTo(TestUtil.CONTENT_TYPE_HEADER_VALUE))
            .willReturn(okJson("{\"reason\":\"Use PUT or PATCH to update: Another provider exists with the same natural key (organization_id, provider_metadata/type, instanceId). Request id: Zl5ZErvdGJ2GiFXNq1X1fTEVQOuT16Xp.\", \"message\":\"619363ad-2461-4c1c-bb09-848f8b4d0a30\"}")
                    .withStatus(409)
            ));
    stubFor(put(urlEqualTo("/events/someConsumerOrgId/someProjectId/someWorkspaceId/providers/619363ad-2461-4c1c-bb09-848f8b4d0a30"))
            .withHeader(TestUtil.CONTENT_TYPE_HEADER, equalTo(TestUtil.CONTENT_TYPE_HEADER_VALUE))
            .willReturn(okJson("{\"_links\":{\"rel:eventmetadata\":{\"href\":\"localhost:9999/events/providers/619363ad-2461-4c1c-bb09-848f8b4d0a30/eventmetadata\"},\"rel:update\":{\"href\":\"localhost:9999/events/someConsumerOrgId/someProjectId/someWorkspaceId/providers/619363ad-2461-4c1c-bb09-848f8b4d0a30\"},\"self\":{\"href\":\"localhost:9999/events/providers/619363ad-2461-4c1c-bb09-848f8b4d0a30\"}},\"id\":\"619363ad-2461-4c1c-bb09-848f8b4d0a30\",\"label\":\"aio-lib-java Wiremock - test drive provider label\",\"description\":\"aio-lib-java Wiremock - test drive provider description\",\"source\":\"urn:uuid:619363ad-2461-4c1c-bb09-848f8b4d0a30\",\"docs_url\":\"https://localhost.com/test\",\"publisher\":\"someImsOrgId\"}")
                    .withStatus(200)
            ));
    provider = getProviderService().createOrUpdateProvider(providerCreateModel);
    verify(postRequestedFor(urlEqualTo("/events/someConsumerOrgId/someProjectId/someWorkspaceId/providers")));
    verify(putRequestedFor(urlEqualTo("/events/someConsumerOrgId/someProjectId/someWorkspaceId/providers/619363ad-2461-4c1c-bb09-848f8b4d0a30")));
    assertEquals(PROVIDER_ID, Objects.requireNonNull(provider.orElse(null)).getId());
    assertEquals(PROVIDER_MODEL_LABEL, provider.get().getLabel());
    assertEquals(PROVIDER_MODEL_DESCRIPTION, provider.get().getDescription());
    assertEquals(SOURCE_ID, provider.get().getSource());
    assertEquals(PROVIDER_MODEL_DOCS_URL, provider.get().getDocsUrl());
    assertEquals(TestUtil.IMS_ORG_ID, provider.get().getPublisher());

//    /** updateProvider */
    stubFor(put(urlEqualTo("/events/someConsumerOrgId/someProjectId/someWorkspaceId/providers/619363ad-2461-4c1c-bb09-848f8b4d0a30"))
            .withHeader(TestUtil.CONTENT_TYPE_HEADER, equalTo(TestUtil.CONTENT_TYPE_HEADER_VALUE))
            .willReturn(okJson("{\"_links\":{\"rel:eventmetadata\":{\"href\":\"localhost:9999/events/providers/619363ad-2461-4c1c-bb09-848f8b4d0a30/eventmetadata\"},\"rel:update\":{\"href\":\"localhost:9999/events/someConsumerOrgId/someProjectId/someWorkspaceId/providers/619363ad-2461-4c1c-bb09-848f8b4d0a30\"},\"self\":{\"href\":\"localhost:9999/events/providers/619363ad-2461-4c1c-bb09-848f8b4d0a30\"}},\"id\":\"619363ad-2461-4c1c-bb09-848f8b4d0a30\",\"label\":\"aio-lib-java Wiremock - test drive provider label\",\"description\":\"aio-lib-java Wiremock - test drive provider description\",\"source\":\"urn:uuid:619363ad-2461-4c1c-bb09-848f8b4d0a30\",\"docs_url\":\"https://localhost.com/test\",\"publisher\":\"someImsOrgId\"}")
                    .withStatus(200)
            ));
    provider = getProviderService().updateProvider(PROVIDER_ID, providerCreateModel);
    verify(putRequestedFor(urlEqualTo("/events/someConsumerOrgId/someProjectId/someWorkspaceId/providers/619363ad-2461-4c1c-bb09-848f8b4d0a30")));
    assertEquals(PROVIDER_ID, Objects.requireNonNull(provider.orElse(null)).getId());
    assertEquals(PROVIDER_MODEL_LABEL, provider.get().getLabel());
    assertEquals(PROVIDER_MODEL_DESCRIPTION, provider.get().getDescription());
    assertEquals(SOURCE_ID, provider.get().getSource());
    assertEquals(PROVIDER_MODEL_DOCS_URL, provider.get().getDocsUrl());
    assertEquals(TestUtil.IMS_ORG_ID, provider.get().getPublisher());

    stubFor(put(urlEqualTo("/events/someConsumerOrgId/someProjectId/someWorkspaceId/providers/619363ad-2461-4c1c-bb09-848f8b4d0a30"))
            .withHeader(TestUtil.CONTENT_TYPE_HEADER, equalTo(TestUtil.CONTENT_TYPE_HEADER_VALUE))
            .willReturn(aResponse()
                    .withStatus(404)
            ));
    provider = getProviderService().updateProvider(PROVIDER_ID, providerCreateModel);
    verify(putRequestedFor(urlEqualTo("/events/someConsumerOrgId/someProjectId/someWorkspaceId/providers/619363ad-2461-4c1c-bb09-848f8b4d0a30")));
    assertEquals(Optional.empty(),provider);

//    /** deleteProvider */
    stubFor(delete(urlEqualTo("/events/someConsumerOrgId/someProjectId/someWorkspaceId/providers/619363ad-2461-4c1c-bb09-848f8b4d0a30"))
            .willReturn(aResponse()
                    .withStatus(204)
            ));
    getProviderService().deleteProvider(PROVIDER_ID);
    verify(deleteRequestedFor(urlEqualTo("/events/someConsumerOrgId/someProjectId/someWorkspaceId/providers/619363ad-2461-4c1c-bb09-848f8b4d0a30")));

  }

  @Test
  public void eventMetadataServiceTest() {

    Optional<EventMetadata> metadata;
    EventMetadata eventMetadata = EventMetadata.builder()
            .eventCode(METADATA_EVENTCODE)
            .description(METADATA_DESCRIPTION)
            .label(METADATA_LABEL)
            .build();

//    /** getEventMetadata */
    stubFor(get(urlEqualTo("/events/providers/619363ad-2461-4c1c-bb09-848f8b4d0a30/eventmetadata/wiremock_eventcode"))
            .willReturn(okJson("{\"_links\":{\"rel:sample_event\":{\"href\":\"localhost:9999/events/providers/619363ad-2461-4c1c-bb09-848f8b4d0a30/eventmetadata/wiremock_eventcode/sample_event\"},\"rel:update\":{\"href\":\"localhost:9999/events/someConsumerOrgId/someProjectId/someWorkspaceId/providers/619363ad-2461-4c1c-bb09-848f8b4d0a30/eventmetadata/wiremock_eventcode\"},\"self\":{\"href\":\"localhost:9999/events/providers/619363ad-2461-4c1c-bb09-848f8b4d0a30/eventmetadata/wiremock_eventcode\"}},\"description\":\"wiremock test description\",\"label\":\"test label\",\"event_code\":\"wiremock_eventcode\"}")
                    .withStatus(200)
            ));
    metadata = getProviderService().getEventMetadata(PROVIDER_ID, METADATA_EVENTCODE);
    verify(getRequestedFor(urlEqualTo("/events/providers/619363ad-2461-4c1c-bb09-848f8b4d0a30/eventmetadata/wiremock_eventcode")));
    assertEquals(METADATA_EVENTCODE, Objects.requireNonNull(metadata.orElse(null)).getEventCode());
    assertEquals(METADATA_DESCRIPTION, metadata.get().getDescription());
    assertEquals(METADATA_LABEL, metadata.get().getLabel());

    stubFor(get(urlEqualTo("/events/providers/619363ad-2461-4c1c-bb09-848f8b4d0a30/eventmetadata"))
            .willReturn(okJson("{\"_links\":{\"self\":{\"href\":\"localhost:9999/events/providers/619363ad-2461-4c1c-bb09-848f8b4d0a30/eventmetadata\"}},\"_embedded\":{\"eventmetadata\":[{\"_links\":{\"rel:sample_event\":{\"href\":\"localhost:9999/events/providers/619363ad-2461-4c1c-bb09-848f8b4d0a30/eventmetadata/wiremock_eventcode/sample_event\"},\"rel:update\":{\"href\":\"localhost:9999/events/someConsumerOrgId/someProjectId/someWorkspaceId/providers/619363ad-2461-4c1c-bb09-848f8b4d0a30/eventmetadata/wiremock_eventcode\"},\"self\":{\"href\":\"localhost:9999/events/providers/619363ad-2461-4c1c-bb09-848f8b4d0a30/eventmetadata/wiremock_eventcode\"}},\"description\":\"wiremock test description\",\"label\":\"test label\",\"event_code\":\"wiremock_eventcode\"}]}}")
                    .withStatus(200)
            ));
    List<EventMetadata> metadataList = getProviderService().getEventMetadata("619363ad-2461-4c1c-bb09-848f8b4d0a30");
    verify(getRequestedFor(urlEqualTo("/events/providers/619363ad-2461-4c1c-bb09-848f8b4d0a30/eventmetadata")));
    assertEquals(1, metadataList.size());

//    /** createEventMetadata */
    stubFor(post(urlEqualTo("/events/someConsumerOrgId/someProjectId/someWorkspaceId/providers/619363ad-2461-4c1c-bb09-848f8b4d0a30/eventmetadata"))
            .withHeader(TestUtil.CONTENT_TYPE_HEADER, equalTo(TestUtil.CONTENT_TYPE_HEADER_VALUE))
            .willReturn(okJson("{\"_links\":{\"rel:sample_event\":{\"href\":\"localhost:9999/events/providers/619363ad-2461-4c1c-bb09-848f8b4d0a30/eventmetadata/wiremock_eventcode/sample_event\"},\"rel:update\":{\"href\":\"localhost:9999/events/someConsumerOrgId/someProjectId/someWorkspaceId/providers/619363ad-2461-4c1c-bb09-848f8b4d0a30/eventmetadata/wiremock_eventcode\"},\"self\":{\"href\":\"localhost:9999/events/providers/619363ad-2461-4c1c-bb09-848f8b4d0a30/eventmetadata/wiremock_eventcode\"}},\"description\":\"wiremock test description\",\"label\":\"test label\",\"event_code\":\"wiremock_eventcode\"}")
                    .withStatus(200)
            ));
    metadata = getProviderService().createEventMetadata(PROVIDER_ID, eventMetadata);
    verify(postRequestedFor(urlEqualTo("/events/someConsumerOrgId/someProjectId/someWorkspaceId/providers/619363ad-2461-4c1c-bb09-848f8b4d0a30/eventmetadata")));
    assertEquals(METADATA_EVENTCODE, Objects.requireNonNull(metadata.orElse(null)).getEventCode());
    assertEquals(METADATA_DESCRIPTION, metadata.get().getDescription());
    assertEquals(METADATA_LABEL, metadata.get().getLabel());

//    /** updateEventMetadata */
    stubFor(put(urlEqualTo("/events/someConsumerOrgId/someProjectId/someWorkspaceId/providers/619363ad-2461-4c1c-bb09-848f8b4d0a30/eventmetadata/wiremock_eventcode"))
            .withHeader(TestUtil.CONTENT_TYPE_HEADER, equalTo(TestUtil.CONTENT_TYPE_HEADER_VALUE))
            .willReturn(okJson("{\"_links\":{\"rel:sample_event\":{\"href\":\"localhost:9999/events/providers/619363ad-2461-4c1c-bb09-848f8b4d0a30/eventmetadata/wiremock_eventcode/sample_event\"},\"rel:update\":{\"href\":\"localhost:9999/events/someConsumerOrgId/someProjectId/someWorkspaceId/providers/619363ad-2461-4c1c-bb09-848f8b4d0a30/eventmetadata/wiremock_eventcode\"},\"self\":{\"href\":\"localhost:9999/events/providers/619363ad-2461-4c1c-bb09-848f8b4d0a30/eventmetadata/wiremock_eventcode\"}},\"description\":\"wiremock test description\",\"label\":\"test label\",\"event_code\":\"wiremock_eventcode\"}")
                    .withStatus(200)
            ));
    metadata = getProviderService().updateEventMetadata(PROVIDER_ID, eventMetadata);
    verify(putRequestedFor(urlEqualTo("/events/someConsumerOrgId/someProjectId/someWorkspaceId/providers/619363ad-2461-4c1c-bb09-848f8b4d0a30/eventmetadata/wiremock_eventcode")));
    assertEquals(METADATA_EVENTCODE, Objects.requireNonNull(metadata.orElse(null)).getEventCode());
    assertEquals(METADATA_DESCRIPTION, metadata.get().getDescription());
    assertEquals(METADATA_LABEL, metadata.get().getLabel());

    stubFor(put(urlEqualTo("/events/someConsumerOrgId/someProjectId/someWorkspaceId/providers/619363ad-2461-4c1c-bb09-848f8b4d0a30/eventmetadata/wiremock_eventcode"))
            .withHeader(TestUtil.CONTENT_TYPE_HEADER, equalTo(TestUtil.CONTENT_TYPE_HEADER_VALUE))
            .willReturn(aResponse()
                    .withStatus(404)
            ));
    metadata = getProviderService().updateEventMetadata(PROVIDER_ID, eventMetadata);
    verify(putRequestedFor(urlEqualTo("/events/someConsumerOrgId/someProjectId/someWorkspaceId/providers/619363ad-2461-4c1c-bb09-848f8b4d0a30/eventmetadata/wiremock_eventcode")));
    assertEquals(Optional.empty(), metadata);

//    /** deleteEventMetadata */
    stubFor(delete(urlEqualTo("/events/someConsumerOrgId/someProjectId/someWorkspaceId/providers/619363ad-2461-4c1c-bb09-848f8b4d0a30/eventmetadata"))
            .willReturn(aResponse()
                    .withStatus(204)
            ));
    getProviderService().deleteAllEventMetadata(PROVIDER_ID);
    verify(deleteRequestedFor(urlEqualTo("/events/someConsumerOrgId/someProjectId/someWorkspaceId/providers/619363ad-2461-4c1c-bb09-848f8b4d0a30/eventmetadata")));

    stubFor(delete(urlEqualTo("/events/someConsumerOrgId/someProjectId/someWorkspaceId/providers/619363ad-2461-4c1c-bb09-848f8b4d0a30/eventmetadata/wiremock_eventcode"))
            .willReturn(aResponse()
                    .withStatus(204)
            ));
    getProviderService().deleteEventMetadata(PROVIDER_ID, METADATA_EVENTCODE);
    verify(putRequestedFor(urlEqualTo("/events/someConsumerOrgId/someProjectId/someWorkspaceId/providers/619363ad-2461-4c1c-bb09-848f8b4d0a30/eventmetadata/wiremock_eventcode")));

  }

  private ProviderService getProviderService(){

    stubFor(post(urlEqualTo("/ims/exchange/jwt"))
            .willReturn(okJson(TestUtil.AUTH_RESPONSE)
                    .withStatus(200)
            ));
    RequestInterceptor authInterceptor = JWTAuthInterceptor.builder()
            .workspace(TestUtil.getWorkspace())
            .build();

    return ProviderService.builder()
            .authInterceptor(authInterceptor)
            .workspace(TestUtil.getWorkspace())
            .url(TestUtil.getWorkspace().getImsUrl())
            .build();
  }

}
