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

  private static final String PROVIDER_MODEL_LABEL = "aio-lib-java Wiremock - test drive provider label";
  private static final String PROVIDER_MODEL_DESCRIPTION = "aio-lib-java Wiremock - test drive provider description";
  private static final String PROVIDER_MODEL_DOCS_URL = "https://localhost.com/test";

  private static final String EVENT_CODE = "wiremock_eventcode";
  private static final String EVENT_DESCRIPTION = "wiremock test description";
  private static final String EVENT_LABEL = "test label";

  private static final String PROVIDER_METADATA_ID = "someProviderMetadataId";
  private static final String INSTANCE_ID = "someProviderInstanceId";

  @Rule
  public WireMockRule wireMockRule = new WireMockRule(Integer.parseInt(TestUtil.PORT));

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
    stubFor(get(urlEqualTo(String.format("/events/%s/providers", TestUtil.CONSUMER_ORG_ID)))
            .willReturn(okJson("{\"_links\":{\"self\":{\"href\":\""+TestUtil.API_MANAGEMENT_URL+"/events/"+TestUtil.CONSUMER_ORG_ID+"/providers\"}},\"_embedded\":{\"providers\":[{\"_links\":{\"rel:eventmetadata\":{\"href\":\""+TestUtil.API_MANAGEMENT_URL+"/events/providers/"+TestUtil.PROVIDER_ID+"/eventmetadata\"},\"self\":{\"href\":\""+TestUtil.API_MANAGEMENT_URL+"/events/providers/"+TestUtil.PROVIDER_ID+"\"}},\"id\":\""+TestUtil.PROVIDER_ID+"\",\"label\":\""+PROVIDER_MODEL_LABEL+"\",\"description\":\""+PROVIDER_MODEL_DESCRIPTION+"\",\"source\":\""+TestUtil.SOURCE_ID+"\",\"docs_url\":\""+PROVIDER_MODEL_DOCS_URL+"\",\"publisher\":\"Adobe\"}]}}")
                    .withStatus(200)
            ));
    List<Provider> providers = getProviderService().getProviders();
    verify(getRequestedFor(urlEqualTo(String.format("/events/%s/providers", TestUtil.CONSUMER_ORG_ID))));
    assertNotNull(providers);
    assertEquals(1, providers.size());
    assertEquals(TestUtil.PROVIDER_ID, providers.get(0).getId());
    assertEquals(TestUtil.SOURCE_ID, providers.get(0).getSource());
    assertEquals(PROVIDER_MODEL_LABEL, providers.get(0).getLabel());
    assertEquals(PROVIDER_MODEL_DESCRIPTION, providers.get(0).getDescription());
    assertEquals(PROVIDER_MODEL_DOCS_URL, providers.get(0).getDocsUrl());

//    /** findProviderById */
    stubFor(get(urlEqualTo(String.format("/events/providers/%s?eventmetadata=true", TestUtil.PROVIDER_ID)))
            .willReturn(okJson(getProviderResponse())
                    .withStatus(200)
            ));
    provider = getProviderService().findProviderById(TestUtil.PROVIDER_ID);
    verify(getRequestedFor(urlEqualTo(String.format("/events/providers/%s?eventmetadata=true", TestUtil.PROVIDER_ID))));
    assertEquals(TestUtil.PROVIDER_ID, Objects.requireNonNull(provider.orElse(null)).getId());
    assertEquals(PROVIDER_MODEL_LABEL, provider.get().getLabel());
    assertEquals(PROVIDER_MODEL_DESCRIPTION, provider.get().getDescription());
    assertEquals(TestUtil.SOURCE_ID, provider.get().getSource());
    assertEquals(PROVIDER_MODEL_DOCS_URL, provider.get().getDocsUrl());
    assertEquals(TestUtil.IMS_ORG_ID, provider.get().getPublisher());

    stubFor(get(urlEqualTo(String.format("/events/providers/%s?eventmetadata=true", TestUtil.PROVIDER_ID)))
            .willReturn(aResponse()
            .withStatus(404)
            ));
    provider = getProviderService().findProviderById(TestUtil.PROVIDER_ID);
    verify(getRequestedFor(urlEqualTo(String.format("/events/providers/%s?eventmetadata=true", TestUtil.PROVIDER_ID))));
    assertEquals(Optional.empty(),provider);

//    /** findProviderBy */
    stubFor(get(urlEqualTo(String.format("/events/%s/providers?providerMetadataId=%s&instanceId=%s", TestUtil.CONSUMER_ORG_ID, PROVIDER_METADATA_ID, INSTANCE_ID)))
            .willReturn(okJson(getProviderResponse())
                    .withStatus(200)
            ));
    Optional<Provider> providersBy = getProviderService().findProviderBy(PROVIDER_METADATA_ID, INSTANCE_ID);
    verify(getRequestedFor(urlEqualTo(String.format("/events/%s/providers?providerMetadataId=%s&instanceId=%s", TestUtil.CONSUMER_ORG_ID, PROVIDER_METADATA_ID, INSTANCE_ID))));
    assertNotNull(providersBy);

//    /** createProvider */
    stubFor(post(urlEqualTo(String.format("/events/%s/%s/%s/providers", TestUtil.CONSUMER_ORG_ID, TestUtil.PROJECT_ID, TestUtil.WORKSPACE_ID)))
            .withHeader(TestUtil.CONTENT_TYPE_HEADER, equalTo(TestUtil.CONTENT_TYPE_HEADER_VALUE))
            .willReturn(okJson(getProviderResponse())
                    .withStatus(201)
            ));
    provider = getProviderService().createProvider(providerCreateModel);
    verify(postRequestedFor(urlEqualTo(String.format("/events/%s/%s/%s/providers", TestUtil.CONSUMER_ORG_ID, TestUtil.PROJECT_ID, TestUtil.WORKSPACE_ID))));
    assertEquals(TestUtil.PROVIDER_ID, Objects.requireNonNull(provider.orElse(null)).getId());
    assertEquals(PROVIDER_MODEL_LABEL, provider.get().getLabel());
    assertEquals(PROVIDER_MODEL_DESCRIPTION, provider.get().getDescription());
    assertEquals(TestUtil.SOURCE_ID, provider.get().getSource());
    assertEquals(PROVIDER_MODEL_DOCS_URL, provider.get().getDocsUrl());
    assertEquals(TestUtil.IMS_ORG_ID, provider.get().getPublisher());

//    /** createOrUpdateProvider */
    stubFor(post(urlEqualTo(String.format("/events/%s/%s/%s/providers", TestUtil.CONSUMER_ORG_ID, TestUtil.PROJECT_ID, TestUtil.WORKSPACE_ID)))
            .withHeader(TestUtil.CONTENT_TYPE_HEADER, equalTo(TestUtil.CONTENT_TYPE_HEADER_VALUE))
            .willReturn(okJson("{\"reason\":\"Use PUT or PATCH to update: Another provider exists with the same natural key (organization_id, provider_metadata/type, instanceId). Request id: Zl5ZErvdGJ2GiFXNq1X1fTEVQOuT16Xp.\", \"message\":\""+TestUtil.PROVIDER_ID+"\"}")
                    .withStatus(409)
            ));
    stubFor(put(urlEqualTo(String.format("/events/%s/%s/%s/providers/%s", TestUtil.CONSUMER_ORG_ID, TestUtil.PROJECT_ID, TestUtil.WORKSPACE_ID, TestUtil.PROVIDER_ID)))
            .withHeader(TestUtil.CONTENT_TYPE_HEADER, equalTo(TestUtil.CONTENT_TYPE_HEADER_VALUE))
            .willReturn(okJson(getProviderResponse())
                    .withStatus(200)
            ));
    provider = getProviderService().createOrUpdateProvider(providerCreateModel);
    verify(postRequestedFor(urlEqualTo(String.format("/events/%s/%s/%s/providers", TestUtil.CONSUMER_ORG_ID, TestUtil.PROJECT_ID, TestUtil.WORKSPACE_ID))));
    verify(putRequestedFor(urlEqualTo(String.format("/events/%s/%s/%s/providers/%s", TestUtil.CONSUMER_ORG_ID, TestUtil.PROJECT_ID, TestUtil.WORKSPACE_ID, TestUtil.PROVIDER_ID))));
    assertEquals(TestUtil.PROVIDER_ID, Objects.requireNonNull(provider.orElse(null)).getId());
    assertEquals(PROVIDER_MODEL_LABEL, provider.get().getLabel());
    assertEquals(PROVIDER_MODEL_DESCRIPTION, provider.get().getDescription());
    assertEquals(TestUtil.SOURCE_ID, provider.get().getSource());
    assertEquals(PROVIDER_MODEL_DOCS_URL, provider.get().getDocsUrl());
    assertEquals(TestUtil.IMS_ORG_ID, provider.get().getPublisher());

//    /** updateProvider */
    stubFor(put(urlEqualTo(String.format("/events/%s/%s/%s/providers/%s", TestUtil.CONSUMER_ORG_ID, TestUtil.PROJECT_ID, TestUtil.WORKSPACE_ID, TestUtil.PROVIDER_ID)))
            .withHeader(TestUtil.CONTENT_TYPE_HEADER, equalTo(TestUtil.CONTENT_TYPE_HEADER_VALUE))
            .willReturn(okJson(getProviderResponse())
                    .withStatus(200)
            ));
    provider = getProviderService().updateProvider(TestUtil.PROVIDER_ID, providerCreateModel);
    verify(putRequestedFor(urlEqualTo(String.format("/events/%s/%s/%s/providers/%s", TestUtil.CONSUMER_ORG_ID, TestUtil.PROJECT_ID, TestUtil.WORKSPACE_ID, TestUtil.PROVIDER_ID))));
    assertEquals(TestUtil.PROVIDER_ID, Objects.requireNonNull(provider.orElse(null)).getId());
    assertEquals(PROVIDER_MODEL_LABEL, provider.get().getLabel());
    assertEquals(PROVIDER_MODEL_DESCRIPTION, provider.get().getDescription());
    assertEquals(TestUtil.SOURCE_ID, provider.get().getSource());
    assertEquals(PROVIDER_MODEL_DOCS_URL, provider.get().getDocsUrl());
    assertEquals(TestUtil.IMS_ORG_ID, provider.get().getPublisher());

    stubFor(put(urlEqualTo(String.format("/events/%s/%s/%s/providers/%s", TestUtil.CONSUMER_ORG_ID, TestUtil.PROJECT_ID, TestUtil.WORKSPACE_ID, TestUtil.PROVIDER_ID)))
            .withHeader(TestUtil.CONTENT_TYPE_HEADER, equalTo(TestUtil.CONTENT_TYPE_HEADER_VALUE))
            .willReturn(aResponse()
                    .withStatus(404)
            ));
    provider = getProviderService().updateProvider(TestUtil.PROVIDER_ID, providerCreateModel);
    verify(putRequestedFor(urlEqualTo(String.format("/events/%s/%s/%s/providers/%s", TestUtil.CONSUMER_ORG_ID, TestUtil.PROJECT_ID, TestUtil.WORKSPACE_ID, TestUtil.PROVIDER_ID))));
    assertEquals(Optional.empty(),provider);

//    /** deleteProvider */
    stubFor(delete(urlEqualTo(String.format("/events/%s/%s/%s/providers/%s", TestUtil.CONSUMER_ORG_ID, TestUtil.PROJECT_ID, TestUtil.WORKSPACE_ID, TestUtil.PROVIDER_ID)))
            .willReturn(aResponse()
                    .withStatus(204)
            ));
    getProviderService().deleteProvider(TestUtil.PROVIDER_ID);
    verify(deleteRequestedFor(urlEqualTo(String.format("/events/%s/%s/%s/providers/%s", TestUtil.CONSUMER_ORG_ID, TestUtil.PROJECT_ID, TestUtil.WORKSPACE_ID, TestUtil.PROVIDER_ID))));

  }

  @Test
  public void eventMetadataServiceTest() {

    Optional<EventMetadata> metadata;
    EventMetadata eventMetadata = EventMetadata.builder()
            .eventCode(EVENT_CODE)
            .description(EVENT_DESCRIPTION)
            .label(EVENT_LABEL)
            .build();

//    /** getEventMetadata */
    stubFor(get(urlEqualTo(String.format("/events/providers/%s/eventmetadata/%s", TestUtil.PROVIDER_ID, EVENT_CODE)))
            .willReturn(okJson(getMetadataResponse())
                    .withStatus(200)
            ));
    metadata = getProviderService().getEventMetadata(TestUtil.PROVIDER_ID, EVENT_CODE);
    verify(getRequestedFor(urlEqualTo(String.format("/events/providers/%s/eventmetadata/%s", TestUtil.PROVIDER_ID, EVENT_CODE))));
    assertEquals(EVENT_CODE, Objects.requireNonNull(metadata.orElse(null)).getEventCode());
    assertEquals(EVENT_DESCRIPTION, metadata.get().getDescription());
    assertEquals(EVENT_LABEL, metadata.get().getLabel());

    stubFor(get(urlEqualTo(String.format("/events/providers/%s/eventmetadata", TestUtil.PROVIDER_ID)))
            .willReturn(okJson("{\"_links\":{\"self\":{\"href\":\""+TestUtil.API_MANAGEMENT_URL+"/events/providers/"+TestUtil.PROVIDER_ID+"/eventmetadata\"}},\"_embedded\":{\"eventmetadata\":["+getMetadataResponse()+"]}}")
                    .withStatus(200)
            ));
    List<EventMetadata> metadataList = getProviderService().getEventMetadata(TestUtil.PROVIDER_ID);
    verify(getRequestedFor(urlEqualTo(String.format("/events/providers/%s/eventmetadata", TestUtil.PROVIDER_ID))));
    assertEquals(1, metadataList.size());

//    /** createEventMetadata */
    stubFor(post(urlEqualTo(String.format("/events/%s/%s/%s/providers/%s/eventmetadata", TestUtil.CONSUMER_ORG_ID, TestUtil.PROJECT_ID, TestUtil.WORKSPACE_ID, TestUtil.PROVIDER_ID)))
            .withHeader(TestUtil.CONTENT_TYPE_HEADER, equalTo(TestUtil.CONTENT_TYPE_HEADER_VALUE))
            .willReturn(okJson(getMetadataResponse())
                    .withStatus(200)
            ));
    metadata = getProviderService().createEventMetadata(TestUtil.PROVIDER_ID, eventMetadata);
    verify(postRequestedFor(urlEqualTo(String.format("/events/%s/%s/%s/providers/%s/eventmetadata", TestUtil.CONSUMER_ORG_ID, TestUtil.PROJECT_ID, TestUtil.WORKSPACE_ID, TestUtil.PROVIDER_ID))));
    assertEquals(EVENT_CODE, Objects.requireNonNull(metadata.orElse(null)).getEventCode());
    assertEquals(EVENT_DESCRIPTION, metadata.get().getDescription());
    assertEquals(EVENT_LABEL, metadata.get().getLabel());

//    /** updateEventMetadata */
    stubFor(put(urlEqualTo(String.format("/events/%s/%s/%s/providers/%s/eventmetadata/%s", TestUtil.CONSUMER_ORG_ID, TestUtil.PROJECT_ID, TestUtil.WORKSPACE_ID, TestUtil.PROVIDER_ID, EVENT_CODE)))
            .withHeader(TestUtil.CONTENT_TYPE_HEADER, equalTo(TestUtil.CONTENT_TYPE_HEADER_VALUE))
            .willReturn(okJson(getMetadataResponse())
                    .withStatus(200)
            ));
    metadata = getProviderService().updateEventMetadata(TestUtil.PROVIDER_ID, eventMetadata);
    verify(putRequestedFor(urlEqualTo(String.format("/events/%s/%s/%s/providers/%s/eventmetadata/%s", TestUtil.CONSUMER_ORG_ID, TestUtil.PROJECT_ID, TestUtil.WORKSPACE_ID, TestUtil.PROVIDER_ID, EVENT_CODE))));
    assertEquals(EVENT_CODE, Objects.requireNonNull(metadata.orElse(null)).getEventCode());
    assertEquals(EVENT_DESCRIPTION, metadata.get().getDescription());
    assertEquals(EVENT_LABEL, metadata.get().getLabel());

    stubFor(put(urlEqualTo(String.format("/events/%s/%s/%s/providers/%s/eventmetadata/%s", TestUtil.CONSUMER_ORG_ID, TestUtil.PROJECT_ID, TestUtil.WORKSPACE_ID, TestUtil.PROVIDER_ID, EVENT_CODE)))
            .withHeader(TestUtil.CONTENT_TYPE_HEADER, equalTo(TestUtil.CONTENT_TYPE_HEADER_VALUE))
            .willReturn(aResponse()
                    .withStatus(404)
            ));
    metadata = getProviderService().updateEventMetadata(TestUtil.PROVIDER_ID, eventMetadata);
    verify(putRequestedFor(urlEqualTo(String.format("/events/%s/%s/%s/providers/%s/eventmetadata/%s", TestUtil.CONSUMER_ORG_ID, TestUtil.PROJECT_ID, TestUtil.WORKSPACE_ID, TestUtil.PROVIDER_ID, EVENT_CODE))));
    assertEquals(Optional.empty(), metadata);

//    /** deleteEventMetadata */
    stubFor(delete(urlEqualTo(String.format("/events/%s/%s/%s/providers/%s/eventmetadata", TestUtil.CONSUMER_ORG_ID, TestUtil.PROJECT_ID, TestUtil.WORKSPACE_ID, TestUtil.PROVIDER_ID)))
            .willReturn(aResponse()
                    .withStatus(204)
            ));
    getProviderService().deleteAllEventMetadata(TestUtil.PROVIDER_ID);
    verify(deleteRequestedFor(urlEqualTo(String.format("/events/%s/%s/%s/providers/%s/eventmetadata", TestUtil.CONSUMER_ORG_ID, TestUtil.PROJECT_ID, TestUtil.WORKSPACE_ID, TestUtil.PROVIDER_ID))));

    stubFor(delete(urlEqualTo(String.format("/events/%s/%s/%s/providers/%s/eventmetadata/%s", TestUtil.CONSUMER_ORG_ID, TestUtil.PROJECT_ID, TestUtil.WORKSPACE_ID, TestUtil.PROVIDER_ID, EVENT_CODE)))
            .willReturn(aResponse()
                    .withStatus(204)
            ));
    getProviderService().deleteEventMetadata(TestUtil.PROVIDER_ID, EVENT_CODE);
    verify(putRequestedFor(urlEqualTo(String.format("/events/%s/%s/%s/providers/%s/eventmetadata/%s", TestUtil.CONSUMER_ORG_ID, TestUtil.PROJECT_ID, TestUtil.WORKSPACE_ID, TestUtil.PROVIDER_ID, EVENT_CODE))));

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
            .url(TestUtil.API_MANAGEMENT_URL)
            .build();
  }

    private static String getMetadataResponse(){
        return "{\"_links\":{\"rel:sample_event\":{\"href\":\""+TestUtil.API_MANAGEMENT_URL+"/events/providers/"+TestUtil.PROVIDER_ID+"/eventmetadata/"+EVENT_CODE+"/sample_event\"},\"rel:update\":{\"href\":\""+TestUtil.API_MANAGEMENT_URL+"/events/"+TestUtil.CONSUMER_ORG_ID+"/"+TestUtil.PROJECT_ID+"/"+TestUtil.WORKSPACE_ID+"/providers/"+TestUtil.PROVIDER_ID+"/eventmetadata/"+EVENT_CODE+"\"},\"self\":{\"href\":\""+TestUtil.API_MANAGEMENT_URL+"/events/providers/"+TestUtil.PROVIDER_ID+"/eventmetadata/"+EVENT_CODE+"\"}},\"description\":\""+EVENT_DESCRIPTION+"\",\"label\":\""+EVENT_LABEL+"\",\"event_code\":\""+EVENT_CODE+"\"}";
    }

    private static String getProviderResponse(){
        return "{\"_links\":{\"rel:eventmetadata\":{\"href\":\""+TestUtil.API_MANAGEMENT_URL+"/events/providers/"+TestUtil.PROVIDER_ID+"/eventmetadata\"},\"rel:update\":{\"href\":\""+TestUtil.API_MANAGEMENT_URL+"/events/"+TestUtil.CONSUMER_ORG_ID+"/"+TestUtil.PROJECT_ID+"/"+TestUtil.WORKSPACE_ID+"/providers/"+TestUtil.PROVIDER_ID+"\"},\"self\":{\"href\":\""+TestUtil.API_MANAGEMENT_URL+"/events/providers/"+TestUtil.PROVIDER_ID+"\"}},\"id\":\""+TestUtil.PROVIDER_ID+"\",\"label\":\""+PROVIDER_MODEL_LABEL+"\",\"description\":\""+PROVIDER_MODEL_DESCRIPTION+"\",\"source\":\""+TestUtil.SOURCE_ID+"\",\"docs_url\":\""+PROVIDER_MODEL_DOCS_URL+"\",\"publisher\":\""+TestUtil.IMS_ORG_ID+"\"}";
    }

}
