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

import com.adobe.Workspace;
import com.adobe.event.journal.model.Entry;
import feign.RequestInterceptor;

public interface JournalService {

  /**
   * @return the oldest the oldest events in the journal. However, because older events expire after
   * 7 days, the best the API can do is to return the oldest available events in the journal.
   * <p>
   * Hence, over time the response of the Journaling API when called without any query parameters
   * will change. This is only because as older events expire, the starting position of the journal
   * begins pointing to a new set of oldest available events.
   */
  Entry getOldest();

  /**
   * @return the latest journal Entry: in most cases, there will not be any events to consume yet
   * <p>
   * In an extremely rare case, there might actually be events that were written in near-real time
   * to the journal after the request was made
   * <p>
   * This method is just a way to jump to the "end" of the journal (asking for "events from now
   * onward"). The client applications should use the Entry.getNextLink() to iterate over the
   * journal from that position onward.
   * @see Entry#getNextLink()
   */
  Entry getLatest();

  /**
   * @param linkUrl
   * @return get a Journal Entry using the provided linkUrl
   */
  Entry get(String linkUrl);

  static Builder builder() {
    return new Builder();
  }

  class Builder {

    private RequestInterceptor authInterceptor;
    private String url;
    private Workspace workspace;

    public Builder authInterceptor(RequestInterceptor authInterceptor) {
      this.authInterceptor = authInterceptor;
      return this;
    }

    public Builder url(String url) {
      this.url = url;
      return this;
    }

    public Builder workspace(Workspace workspace) {
      this.workspace = workspace;
      return this;
    }

    public JournalService build() {
      return new JournalServiceImpl(authInterceptor, url, workspace);
    }
  }
}
