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

import com.adobe.event.journal.model.JournalEntry;
import com.adobe.ims.JWTAuthInterceptor;
import com.adobe.ims.util.TestUtil;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import feign.RequestInterceptor;
import org.junit.Rule;
import org.junit.Test;
import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static junit.framework.TestCase.*;

public class JournalServiceTest {

  private static final String POSITION = "10";
  private static final int BATCH_SIZE = 30;

  @Rule
  public WireMockRule wireMockRule = new WireMockRule(Integer.parseInt(TestUtil.PORT));

  @Test
  public void journalServiceTest(){

    assertNotNull(getJournalService());

//    /** get */
    stubFor(get(anyUrl())
            .withHeader(TestUtil.IMS_ORG_ID_HEADER, equalTo(TestUtil.IMS_ORG_ID))
            .willReturn(okJson("{\"events\":[{\"position\":\"somePosition\",\"event\":{\"data\":{\"rawEventId\":\"someRawEventId\"},\"id\":\"someId\",\"source\":\"someSource\",\"specversion\":\"1.0\",\"type\":\"sometype\",\"datacontenttype\":\"application/json\",\"event_id\":\"someEventId\",\"recipient_client_id\":\"someRecipientClientId\"}}],\"_page\":{\"last\":\"last\",\"count\":1}}")
                    .withStatus(200)
            ));

    JournalEntry entry = getJournalService().get(TestUtil.LINK_URL);
    verify(getRequestedFor(anyUrl()));
    assertNotNull(entry);
    assertEquals(1, entry.getEvents().size());
    assertEquals(0, entry.getLinks().size());
    assertEquals(0, entry.getRetryAfterInSeconds());
    assertEquals(1, entry.getPage().getCount());
    assertEquals("last", entry.getPage().getLast());


    //    /** getLatest */
    stubFor(get(urlEqualTo("/?latest=true"))
            .withHeader(TestUtil.IMS_ORG_ID_HEADER, equalTo(TestUtil.IMS_ORG_ID))
            .willReturn(okJson("{}")
                    .withStatus(200)
            ));

    entry  = getJournalService().getLatest();
    verify(getRequestedFor(urlEqualTo("/?latest=true")));
    assertNotNull(entry);
    assertEquals(0, entry.getLinks().size());
    assertEquals(0, entry.getRetryAfterInSeconds());

    //    /** getSince */
    stubFor(get(urlEqualTo(String.format("/?since=%s", POSITION)))
            .withHeader(TestUtil.IMS_ORG_ID_HEADER, equalTo(TestUtil.IMS_ORG_ID))
            .willReturn(aResponse()
                    .withStatus(200)
            ));

    getJournalService().getSince(POSITION);
    verify(getRequestedFor(urlEqualTo(String.format("/?since=%s", POSITION))));

    //    /** getSince -> with batchSize */
    stubFor(get(urlEqualTo(String.format("/?since=%s&limit=%s", POSITION, BATCH_SIZE)))
            .withHeader(TestUtil.IMS_ORG_ID_HEADER, equalTo(TestUtil.IMS_ORG_ID))
            .willReturn(aResponse()
                    .withStatus(200)
            ));

    getJournalService().getSince(POSITION, BATCH_SIZE);
    verify(getRequestedFor(urlEqualTo(String.format("/?since=%s&limit=%d", POSITION, BATCH_SIZE))));

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
            .url(TestUtil.JOURNAL_URL)
            .build();
  }

}
