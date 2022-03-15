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

import com.adobe.event.publish.model.CloudEvent;
import com.adobe.ims.JWTAuthInterceptor;
import com.adobe.ims.util.TestUtil;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import feign.RequestInterceptor;
import org.junit.Rule;
import org.junit.Test;
import java.io.IOException;
import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static junit.framework.TestCase.*;

public class PublishServiceTest {

  private static final String EVENT_CODE = "osgi_ping";
  private static final String EVENT_PAYLOAD = "some event payload";

  @Rule
  public WireMockRule wireMockRule = new WireMockRule(Integer.parseInt(TestUtil.PORT));

  @Test
  public void publishServiceTest() throws IOException {

    assertNotNull(getPublishService());

//    /** publishRawEvent */
    stubFor(post(urlEqualTo("/"))
            .withHeader(TestUtil.CONTENT_TYPE_HEADER, equalTo(TestUtil.CONTENT_TYPE_HEADER_VALUE))
            .withHeader("x-adobe-event-provider-id", equalTo(TestUtil.PROVIDER_ID))
            .withHeader("x-adobe-event-code", equalTo(EVENT_CODE))
            .willReturn(aResponse()
                    .withStatus(200)
            ));
    getPublishService().publishRawEvent(TestUtil.PROVIDER_ID, EVENT_CODE, EVENT_PAYLOAD);
    verify(postRequestedFor(urlEqualTo("/")));

//    /** publishCloudEvent */
    stubFor(post(urlEqualTo("/"))
            .withHeader(TestUtil.CONTENT_TYPE_HEADER, equalTo("application/cloudevents+json"))
            .willReturn(aResponse()
                    .withStatus(200)
            ));
    CloudEvent cloudEvent = getPublishService().publishCloudEvent(TestUtil.PROVIDER_ID, EVENT_CODE, EVENT_PAYLOAD);
    verify(postRequestedFor(urlEqualTo("/")));
    assertNotNull(cloudEvent.getId());
    assertNotNull(cloudEvent.getData());
    assertEquals(TestUtil.SOURCE_ID, cloudEvent.getSource());
    assertEquals(EVENT_CODE, cloudEvent.getType());
    assertEquals(TestUtil.CONTENT_TYPE_HEADER_VALUE, cloudEvent.getDataContentType());
    assertEquals("1.0", cloudEvent.getSpecVersion());
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
            .url(TestUtil.PUBLISH_URL)
            .build();
  }

}
