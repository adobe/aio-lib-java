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
package com.adobe.aio.aem.util;

import com.adobe.aio.exception.AIOException;
import java.util.Collections;
import java.util.Map;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;

public class ResourceResolverWrapper implements AutoCloseable {

  /**
   * The Sling ServiceUserMapper service allows for mapping Service IDs comprised of the Service
   * Names defined by the providing bundles and optional Subservice Name to ResourceResolver and/or
   * JCR Repository user IDs. This mapping is configurable such that system administrators are in
   * full control of assigning users to services. cf. http://sling.apache.org/documentation/the-sling-engine/service-authentication.html#implementation
   */
  String USER_MAPPED_SUB_SERVICE_NAME = "aio_service";

  /**
   * An immutable map containing the authentication info for the service user. cf
   * http://sling.apache.org/documentation/the-sling-engine/service-authentication.html#implementation
   */
  Map<String, Object> SERVICE_AUTH_INFO = Collections.singletonMap(
      ResourceResolverFactory.SUBSERVICE, USER_MAPPED_SUB_SERVICE_NAME);


  ResourceResolver resolver;
  ResourceResolverFactory factory;

  public ResourceResolverWrapper(ResourceResolverFactory rrf) {
    factory = rrf;
  }

  @Override
  public void close() throws Exception {
    if (resolver != null) {
      resolver.close();
    }
  }

  /**
   * Get a valid ResourceResolver.
   *
   * @return a ResourceResolver. If called multiple times, it's always the same ResourceResolver
   * instance
   */
  public ResourceResolver getResolver() {
    if (resolver == null) {
      try {
        resolver = factory.getServiceResourceResolver(SERVICE_AUTH_INFO);
      } catch (LoginException e) {
        throw new AIOException(
            "Configuration error, Could not access the resource resolver "
                + "associated with the system-user `" + USER_MAPPED_SUB_SERVICE_NAME
                + "` due to : " + e.getMessage(), e);
      }
    }
    return resolver;
  }
}
