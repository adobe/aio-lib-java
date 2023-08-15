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

import static com.fasterxml.jackson.databind.type.TypeFactory.rawClass;

import com.adobe.aio.event.journal.model.JournalEntry;
import feign.Request;
import feign.Response;
import feign.Util;
import feign.codec.DecodeException;
import feign.codec.Decoder;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class JournalLinkDecoder implements Decoder {

  private final Logger logger = LoggerFactory.getLogger(this.getClass());
  final static String LINK_HEADER = "link";
  final static String RETRY_AFTER_HEADER = "retry-after";
  /**
   * links headers looks like the following:
   * <aPath>; rel=\"aName\"";
   */
  private final static String LINK_PATH_NAME_REGEXP = "\\s*<(.*?)>\\s*;\\s*rel\\s*=\\s*\"(.*?)\"\\s*";

  final Decoder delegate;

  public JournalLinkDecoder(Decoder delegate) {
    Objects.requireNonNull(delegate, "Decoder must not be null. ");
    this.delegate = delegate;
  }

  public Object decode(Response response, Type type) throws IOException {
    if (!rawClass(type).equals(JournalEntry.class)) {
      throw new DecodeException(response.status(), String.format("%s is not a type supported by this decoder.", type), response.request());
    }

    if (response.status() < 200 || response.status() >= 300) {
      logger.info("Not decoding Journal header link values when the response status is `{}`", response.status());
      return Util.emptyValueOf(type);
    }

    JournalEntry entry = (JournalEntry) this.delegate.decode(response, type);
    // Decode could return null.
    if (entry == null) entry = new JournalEntry();

    if (response.headers() != null) {
      if (response.status() == 204 && response.headers().containsKey(RETRY_AFTER_HEADER))  {
        entry.setRetryAfterInSeconds(response.headers().get(RETRY_AFTER_HEADER).iterator().next());
      }
      if (response.headers().containsKey(LINK_HEADER)) {
        Map<String, String> links = new HashMap<>();
        Collection<String> linkValues = response.headers().get(LINK_HEADER);
        for (String linkValue : linkValues) {
          Pattern pattern = Pattern.compile(LINK_PATH_NAME_REGEXP);
          Matcher matcher = pattern.matcher(linkValue);
          if (matcher.find()) {
            links.put(matcher.group(2), getRootUrl(response.request()) + matcher.group(1));
          } else {
            logger.error("unexpected Journal header link values format.");
          }
        }
        entry.setLinks(links);
      }
    }
    return entry;
  }

  private String getRootUrl(Request request) throws MalformedURLException {
    URL requestUrl = new URL(request.url());
    if (("https".equals(requestUrl.getProtocol()) && requestUrl.getPort() == 443) ||
        ("http".equals(requestUrl.getProtocol()) && requestUrl.getPort() == 80)) {
      return requestUrl.getProtocol() + "://" + requestUrl.getHost();
    } else {
      return requestUrl.getProtocol() + "://" + requestUrl.getHost() + ":" + requestUrl.getPort();
    }
  }
}



