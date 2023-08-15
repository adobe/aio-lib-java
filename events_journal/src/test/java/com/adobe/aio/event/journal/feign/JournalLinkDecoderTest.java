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
package com.adobe.aio.event.journal.feign;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.adobe.aio.event.journal.model.JournalEntry;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Request;
import feign.Response;
import feign.codec.DecodeException;
import feign.codec.Decoder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class JournalLinkDecoderTest {

  @Mock
  private Decoder mockDelegate;

  @Mock
  private Request request;

  @Mock
  private Response response;

  private static final String TEST_IMS_ORG_ID = "testImsOrgId";
  private static final int RETRY_AFTER_VALUE = 10;

  @Test
  public void unsupportedType() {
    when(response.request()).thenReturn(request);
    JournalLinkDecoder decoder = new JournalLinkDecoder(mockDelegate);
    assertThrows(DecodeException.class, () -> decoder.decode(response, JournalLinkDecoder.class));
  }

  @Test
  public void errorResponse() throws Exception {
    when(response.status()).thenReturn(302);
    JournalLinkDecoder decoder = new JournalLinkDecoder(mockDelegate);
    assertNull(decoder.decode(response, JournalEntry.class));
  }

  @Test
  public void get204() throws Exception {
    final String rootServerUrl = "http://localhost:1234";

    Map<String, Collection<String>> headers = new HashMap<>();
    headers.put(JournalLinkDecoder.RETRY_AFTER_HEADER, Collections.singleton("" + RETRY_AFTER_VALUE));
    List<String> links = new ArrayList<>();
    links.add(getNextLink());
    links.add(getCountLink());
    headers.put(JournalLinkDecoder.LINK_HEADER, links);

    when(response.status()).thenReturn(204);
    when(response.headers()).thenReturn(headers);
    when(response.request()).thenReturn(request);

    when(request.url()).thenReturn(rootServerUrl);

    JournalLinkDecoder decoder = new JournalLinkDecoder(mockDelegate);
    JournalEntry actual = (JournalEntry) decoder.decode(response, JournalEntry.class);

    assertTrue(actual.isEmpty());
    assertEquals("http://localhost:1234/events-fast/organizations/organizations/junit/integrations/junit/junit?since=salmon:1.salmon:2", actual.getNextLink());
    assertEquals("http://localhost:1234/count/organizations/organizations/junit/integrations/junit/junit?since=salmon:1.salmon:2", actual.getLinks().get("count"));
    assertEquals(RETRY_AFTER_VALUE, actual.getRetryAfterInSeconds());
  }

  @Test
  public void get200() throws Exception {
    final String rootServerUrl = "http://localhost:1234";

    String payload = "{\"events\": [ { \"position\": \"aposition\", \"event\": {  \"key\": \"value\"  }} ], \"_page\": { \"last\": \"aposition\",  \"count\": 1 }}";
    JournalEntry entry = new ObjectMapper().readValue(payload, JournalEntry.class);

    Map<String, Collection<String>> headers = new HashMap<>();
    headers.put(JournalLinkDecoder.RETRY_AFTER_HEADER, Collections.singleton("" + RETRY_AFTER_VALUE));
    List<String> links = new ArrayList<>();
    links.add(getNextLink());
    links.add(getCountLink());
    headers.put(JournalLinkDecoder.LINK_HEADER, links);

    when(response.status()).thenReturn(200);
    when(response.headers()).thenReturn(headers);
    when(response.request()).thenReturn(request);

    when(request.url()).thenReturn(rootServerUrl);

    when(mockDelegate.decode(response, JournalEntry.class)).thenReturn(entry);

    JournalLinkDecoder decoder = new JournalLinkDecoder(mockDelegate);
    JournalEntry actual = (JournalEntry) decoder.decode(response, JournalEntry.class);

    assertFalse(actual.isEmpty());
    assertEquals("http://localhost:1234/events-fast/organizations/organizations/junit/integrations/junit/junit?since=salmon:1.salmon:2", actual.getNextLink());
    assertEquals("http://localhost:1234/count/organizations/organizations/junit/integrations/junit/junit?since=salmon:1.salmon:2", actual.getLinks().get("count"));
    assertEquals(1, actual.size());
    assertEquals("aposition", entry.getEvents().get(0).getPosition());
    assertEquals("{\"key\":\"value\"}", entry.getEvents().get(0).getEvent().toString());
    assertEquals("aposition", entry.getPage().getLast());
    assertEquals(1, entry.getPage().getCount());
  }

  private String getNextLink() {
    return "</events-fast/organizations/organizations/junit/integrations/junit/junit?since=salmon:1.salmon:2>; rel=\"next\"";
  }

  private String getCountLink() {
    return "</count/organizations/organizations/junit/integrations/junit/junit?since=salmon:1.salmon:2>; rel=\"count\"";
  }

}
