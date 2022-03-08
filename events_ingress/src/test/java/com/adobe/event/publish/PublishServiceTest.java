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
package com.adobe.event.publish;

import com.adobe.ims.JWTAuthInterceptor;
import com.adobe.ims.util.TestUtil;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import feign.RequestInterceptor;
import org.junit.Rule;
import org.junit.Test;
import java.io.IOException;
import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static junit.framework.TestCase.assertNotNull;

public class PublishServiceTest {

  private static final String PROVIDER_ID = "d49a4e9b-99e3-47c8-9daa-6232bb7da4de";
  private static final String EVENT_CODE = "osgi_ping";
  private static final String EVENT_PAYLOAD = "some event payload";

  @Rule
  public WireMockRule wireMockRule = new WireMockRule(9999);

  @Test
  public void publishServiceTest() throws IOException {

    assertNotNull(getPublishService());

//    /** publishRawEvent */
    stubFor(post(anyUrl())
            .withHeader(TestUtil.CONTENT_TYPE_HEADER, equalTo(TestUtil.CONTENT_TYPE_HEADER_VALUE))
            .withHeader("x-adobe-event-provider-id", equalTo(PROVIDER_ID))
            .withHeader("x-adobe-event-code", equalTo(EVENT_CODE))
            .willReturn(aResponse()
                    .withStatus(200)
            ));
    getPublishService().publishRawEvent(PROVIDER_ID, EVENT_CODE, EVENT_PAYLOAD);
    verify(postRequestedFor(anyUrl()));

//    /** publishCloudEvent */
    stubFor(post(anyUrl())
            .withHeader(TestUtil.CONTENT_TYPE_HEADER, equalTo("application/cloudevents+json"))
            .willReturn(aResponse()
                    .withStatus(200)
            ));
    getPublishService().publishCloudEvent(PROVIDER_ID, EVENT_CODE, EVENT_PAYLOAD);
    verify(postRequestedFor(anyUrl()));
  }

  private PublishService getPublishService(){

    stubFor(post(urlEqualTo("/ims/exchange/jwt"))
            .willReturn(okJson(TestUtil.AUTH_RESPONSE)
                    .withStatus(200)
            ));
    RequestInterceptor authInterceptor = JWTAuthInterceptor.builder()
            .workspace(TestUtil.getWorkspace())
            .build();

    return PublishService.builder()
            .authInterceptor(authInterceptor)
            .url(TestUtil.getWorkspace().getImsUrl())
            .build();
  }

}
