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
package com.adobe.aio.aem.event.management.internal;

import com.adobe.aio.aem.event.management.EventProviderConfigSupplier;
import com.adobe.aio.aem.event.management.ocd.EventProviderConfig;
import com.adobe.aio.aem.status.Status;
import com.adobe.aio.aem.util.ResourceResolverWrapper;
import com.adobe.aio.aem.util.ResourceResolverWrapperFactory;
import com.adobe.aio.event.management.model.ProviderInputModel;
import com.adobe.aio.exception.AIOException;
import com.day.cq.commons.Externalizer;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(service = EventProviderConfigSupplier.class,
    property = {"label = Adobe I/O Events' Provider Config Supplier",
        "description = Adobe I/O Events' Provider Config Supplier"})
@Designate(ocd = EventProviderConfig.class)
public class EventProviderConfigSupplierImpl implements EventProviderConfigSupplier {

  static final String AEM_PROVIDER_METADATA_ID = "aem";
  static final String AEM_CLOUD_DOMAIN_SUFFIX = ".adobeaemcloud.com";

  private final Logger log = LoggerFactory.getLogger(getClass());

  @Reference
  Externalizer externalizerService;

  @Reference
  private ResourceResolverWrapperFactory resourceResolverWrapperFactory;

  private String externalizerName;
  private URL rootUrl;
  private ProviderInputModel providerInputModel;
  private Throwable error;

  static String getInstanceId(URL url) {
    int indexOfAdobeAemCloudDomain = url.getHost().lastIndexOf(AEM_CLOUD_DOMAIN_SUFFIX);
    if (indexOfAdobeAemCloudDomain > 1) {
      return url.getHost().substring(0, indexOfAdobeAemCloudDomain);
      // we are on `aem as a cloud` domain let's use only the specific host prefix
    } else if (url.getHost().contains("localhost")) {
      // we are on localhost let's make this developer friendly and append the port number
      return url.getHost() + url.getPort();
    } else {
      return url.getHost();
    }
  }

  @Activate
  @Modified
  protected void activate(EventProviderConfig configuration) {
    try {
      this.externalizerName = configuration.externalizerName();
      this.rootUrl = resolveRootUrl();
      this.providerInputModel = getProviderInputModel(configuration, rootUrl);
    } catch (Exception e) {
      log.error("Adobe I/O Events' Provider Config Supplier activation error: {}"
          , e.getMessage(), e);
      error = e;
    }
  }

  @Override
  public Status getStatus() {
    Map<String, Object> details = new HashMap<>(1);
    details.put("externalizer_name", externalizerName);
    details.put("root_url", rootUrl != null ? rootUrl.toString() : null);
    details.put("provider_input_model", providerInputModel);
    return new Status(details, error);
  }

  @Override
  public ProviderInputModel getProviderInputModel() {
    return this.providerInputModel;
  }

  @Override
  public URL getRootUrl() {
    return rootUrl;
  }

  private URL resolveRootUrl() {
    try (ResourceResolverWrapper resourceResolverWrapper = resourceResolverWrapperFactory.getWrapper()) {
      return new URL(
          externalizerService.externalLink(resourceResolverWrapper.getResolver(), externalizerName,
              "/"));
    } catch (Exception e) {
      throw new AIOException
          ("Cannot look up the Adobe I/O Event providers instanceId due to " + e.getMessage(), e);
    }
  }

  private ProviderInputModel getProviderInputModel(
      final EventProviderConfig providerConfig,
      final URL rootUrl) {
    String instanceId = getInstanceId(rootUrl);
    return ProviderInputModel.builder()
        .instanceId(instanceId)
        .providerMetadataId(AEM_PROVIDER_METADATA_ID)
        .label(providerConfig.aio_provider_label().isEmpty() ? instanceId
            : providerConfig.aio_provider_label())
        .description(providerConfig.aio_provider_description().isEmpty() ? "AEM " + instanceId
            : providerConfig.aio_provider_description())
        .docsUrl(providerConfig.aio_provider_docs_url())
        .build();
  }

}
