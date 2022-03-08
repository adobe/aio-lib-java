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
package com.adobe.event.journal;

import com.adobe.ims.JWTAuthInterceptor;
import com.adobe.ims.util.TestUtil;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import feign.RequestInterceptor;
import org.junit.Rule;
import org.junit.Test;
import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static junit.framework.TestCase.assertNotNull;

public class JournalServiceTest {

  private static final String POSITION = "10";

  @Rule
  public WireMockRule wireMockRule = new WireMockRule(9999);

  @Test
  public void journalServiceTest(){

    assertNotNull(getJournalService());

//    /** get */
    stubFor(get(anyUrl())
            .withHeader(TestUtil.IMS_ORG_ID_HEADER, equalTo(TestUtil.IMS_ORG_ID))
            .willReturn(aResponse()
                    .withStatus(200)
            ));

    getJournalService().get(TestUtil.getWorkspace().getImsUrl());
    verify(getRequestedFor(anyUrl()));

    //    /** getLatest */
    stubFor(get(urlEqualTo("/?latest=true"))
            .withHeader(TestUtil.IMS_ORG_ID_HEADER, equalTo(TestUtil.IMS_ORG_ID))
            .willReturn(aResponse()
                    .withStatus(200)
            ));

    getJournalService().getLatest();
    verify(getRequestedFor(urlEqualTo("/?latest=true")));

    //    /** getSince */
    stubFor(get(urlEqualTo("/?since=10"))
            .withHeader(TestUtil.IMS_ORG_ID_HEADER, equalTo(TestUtil.IMS_ORG_ID))
            .willReturn(aResponse()
                    .withStatus(200)
            ));

    getJournalService().getSince(POSITION);
    verify(getRequestedFor(urlEqualTo("/?since=10")));

    //    /** getSince -> with batchSize */
    stubFor(get(urlEqualTo("/?since=10&limit=30"))
            .withHeader(TestUtil.IMS_ORG_ID_HEADER, equalTo(TestUtil.IMS_ORG_ID))
            .willReturn(aResponse()
                    .withStatus(200)
            ));

    getJournalService().getSince(POSITION, 30);
    verify(getRequestedFor(urlEqualTo("/?since=10&limit=30")));

  }

  private JournalService getJournalService(){

    stubFor(post(urlEqualTo("/ims/exchange/jwt"))
            .willReturn(okJson(TestUtil.AUTH_RESPONSE)
                    .withStatus(200)
            ));
    RequestInterceptor authInterceptor = JWTAuthInterceptor.builder()
            .workspace(TestUtil.getWorkspace())
            .build();

    return JournalService.builder()
            .workspace(TestUtil.getWorkspace())
            .authInterceptor(authInterceptor)
            .url(TestUtil.getWorkspace().getImsUrl())
            .build();
  }

}
