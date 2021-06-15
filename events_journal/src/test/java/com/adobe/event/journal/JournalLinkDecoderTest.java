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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.adobe.event.journal.api.JournalApi;
import com.adobe.event.journal.model.Entry;
import com.adobe.util.JacksonUtil;
import feign.Feign;
import feign.FeignException;
import feign.jackson.JacksonDecoder;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class JournalLinkDecoderTest {

  private static final String TEST_IMS_ORG_ID = "testImsOrgId";
  private JournalLinkDecoder journalLinkDecoderUnderTest;
  @Rule
  public ExpectedException expectedEx = ExpectedException.none();

  @Test
  public void get204() {
    final MockWebServer server = new MockWebServer();
    String rootServerUrl = server.url("/").toString();
    server.enqueue(new MockResponse()
        .addHeader(JournalLinkDecoder.LINK_HEADER, getNextLink())
        .addHeader(JournalLinkDecoder.LINK_HEADER, getCountLink())
        .setResponseCode(204));
    server.enqueue(new MockResponse().setBody("foo"));

    final JournalApi api = Feign.builder()
        .decoder(new JournalLinkDecoder(new JacksonDecoder(JacksonUtil.DEFAULT_OBJECT_MAPPER)))
        .target(JournalApi.class, rootServerUrl);
    Entry entry = api.get(TEST_IMS_ORG_ID);
    assertTrue(entry.isEmpty());
    assertEquals("http://localhost:" + server.getPort()
            + "/events-fast/organizations/organizations/junit/integrations/junit/junit?since=salmon:1.salmon:2",
        entry.getNextLink());
    assertEquals("http://localhost:" + server.getPort()
            + "/count/organizations/organizations/junit/integrations/junit/junit?since=salmon:1.salmon:2",
        entry.getLinks().get("count"));
  }

  @Test
  public void get200() {
    final MockWebServer server = new MockWebServer();
    String rootServerUrl = server.url("/").toString();
    String entryJsonPayload = "{\"events\":"
        + " [ { \"position\": \"aposition\", \"event\": {  \"key\": \"value\"  }} ],"
        + " \"_page\": { \"last\": \"aposition\",  \"count\": 1 }}";
    server.enqueue(new MockResponse()
        .addHeader(JournalLinkDecoder.LINK_HEADER, getNextLink())
        .addHeader(JournalLinkDecoder.LINK_HEADER, getCountLink())
        .setResponseCode(200)
        .setBody(entryJsonPayload));

    final JournalApi api = Feign.builder()
        .decoder(new JournalLinkDecoder(new JacksonDecoder(JacksonUtil.DEFAULT_OBJECT_MAPPER)))
        .target(JournalApi.class, rootServerUrl);
    Entry entry = api.get(TEST_IMS_ORG_ID);
    assertTrue(!entry.isEmpty());
    assertEquals("http://localhost:" + server.getPort()
            + "/events-fast/organizations/organizations/junit/integrations/junit/junit?since=salmon:1.salmon:2",
        entry.getNextLink());
    assertEquals("http://localhost:" + server.getPort()
            + "/count/organizations/organizations/junit/integrations/junit/junit?since=salmon:1.salmon:2",
        entry.getLinks().get("count"));
    assertEquals(1, entry.size());
    assertEquals("aposition", entry.getEvents().get(0).getPosition());
    assertEquals("{\"key\":\"value\"}", entry.getEvents().get(0).getEvent().toString());
    assertEquals("aposition", entry.getPage().getLast());
    assertEquals(1, entry.getPage().getCount());
  }

  @Test
  public void get404() {
    final MockWebServer server = new MockWebServer();
    String rootServerUrl = server.url("/").toString();
    server.enqueue(new MockResponse()
        .setResponseCode(404));

    final JournalApi api = Feign.builder()
        .decoder(new JournalLinkDecoder(new JacksonDecoder(JacksonUtil.DEFAULT_OBJECT_MAPPER)))
        .target(JournalApi.class, rootServerUrl);

    expectedEx.expect(FeignException.class);
    expectedEx.expectMessage("404");
    Entry entry = api.get(TEST_IMS_ORG_ID);
  }


  private String getNextLink() {
    return "</events-fast/organizations/organizations/junit/integrations/junit/junit?since=salmon:1.salmon:2>; rel=\"next\"";
  }

  private String getCountLink() {
    return "</count/organizations/organizations/junit/integrations/junit/junit?since=salmon:1.salmon:2>; rel=\"count\"";
  }

}
