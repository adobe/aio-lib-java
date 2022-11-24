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
package com.adobe.xdm.common;


import com.fasterxml.jackson.annotation.JsonProperty;

public class XdmContext {

  public static final String XDM_BASE_URL = "https://ns.adobe.com/xdm";
  public static final String XDM_EXTENSION_BASE_URL = "https://ns.adobe.com/xdm-extensions";

  public static final String XDM_AEM_USER_TYPE = "xdmAemUser";
  public static final String XDM_AEM_USER_PREFIX = XDM_AEM_USER_TYPE;
  public static final String XDM_AEM_USER_JSONLD_IRI = XDM_EXTENSION_BASE_URL + "/aem/user";

  public static final String OSGI_EVENT_TYPE = "osgiEvent";
  public static final String OSGI_EVENT_PREFIX = OSGI_EVENT_TYPE;
  public static final String OSGI_EVENT_JSONLD_IRI = "https://osgi.org/javadoc/r4v42/org/osgi/service/event/Event.html";

  public static final String XDM_ASSET_TYPE = "xdmAsset";
  public static final String XDM_ASSET_PREFIX = XDM_ASSET_TYPE;
  public static final String XDM_ASSET_JSONLD_IRI = XDM_BASE_URL + "/assets/asset#";

  public static final String XDM_DIRECTORY_TYPE = "xdmDirectory";
  public static final String XDM_DIRECTORY_PREFIX = XDM_DIRECTORY_TYPE;
  public static final String XDM_DIRECTORY_JSONLD_IRI = "https://ns.adobe.com/adobecloud/core/1.0/directory#";

  public static final String XDM_IMS_ORG_TYPE = "xdmImsOrg";
  public static final String XDM_IMS_ORG_PREFIX = XDM_IMS_ORG_TYPE;
  public static final String XDM_IMS_ORG_JSONLD_IRI = XDM_EXTENSION_BASE_URL + "/ims/organization#";

  public static final String XDM_IMS_USER_TYPE = "xdmImsUser";
  public static final String XDM_IMS_USER_PREFIX = XDM_IMS_USER_TYPE;
  public static final String XDM_IMS_USER_JSONLD_IRI = XDM_EXTENSION_BASE_URL + "/ims/user#";

  public static final String XDM_CONTENT_REPOSITORY_TYPE = "xdmContentRepository";
  public static final String XDM_CONTENT_REPOSITORY_PREFIX = XDM_CONTENT_REPOSITORY_TYPE;
  public static final String XDM_CONTENT_REPOSITORY_JSONLD_IRI =
      XDM_BASE_URL + "/content/repository#";

  public static final String XDM_COMPONENTIZED_PAGE_TYPE = "xdmComponentizedPage";
  public static final String XDM_COMPONENTIZED_PAGE_PREFIX = XDM_COMPONENTIZED_PAGE_TYPE;
  public static final String XDM_COMPONENTIZED_PAGE_JSONLD_IRI =
      XDM_BASE_URL + "/content/componentized-page#";

  public static final String XDM_EVENT_CREATED_TYPE = "xdmCreated";
  public static final String XDM_EVENT_CREATED_JSONLD_IRI = XDM_BASE_URL + "/common/event/created#";
  public static final String XDM_EVENT_DELETED_TYPE = "xdmDeleted";
  public static final String XDM_EVENT_DELETED_JSONLD_IRI = XDM_BASE_URL + "/common/event/deleted#";
  public static final String XDM_EVENT_UPDATED_TYPE = "xdmUpdated";
  public static final String XDM_EVENT_UPDATED_JSONLD_IRI = XDM_BASE_URL + "/common/event/updated#";

  public static final String XDM_EVENT_PUBLISHED_TYPE = "xdmPublished";
  public static final String XDM_EVENT_PUBLISHED_JSONLD_IRI = XDM_BASE_URL + "/common/event/published#";
  public static final String XDM_EVENT_UNPUBLISHED_TYPE = "xdmUnpublished";
  public static final String XDM_EVENT_UNPUBLISHED_JSONLD_IRI = XDM_BASE_URL + "/common/event/unpublished#";

  public static final String XDM_EVENT_EMITTED_TYPE = "xdmEmitted";
  public static final String XDM_EVENT_EMITTED_TYPE_JSONLD_IRI = XDM_BASE_URL + "/common/event/emitted#";


  public static final String W3C_ACTIVITYSTREAMS_PREFIX = "activitystreams";
  public static final String W3C_ACTIVITYSTREAMS_JSONLD_IRI = "http://www.w3.org/ns/activitystreams#";

  public static final String XDM_EVENT_ENVELOPE_PREFIX = "xdmEventEnvelope";
  public static final String XDM_EVENT_ENVELOPE_JSONLD_IRI = XDM_BASE_URL + "/event-envelope#";

  public static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSX";

  @JsonProperty(W3C_ACTIVITYSTREAMS_PREFIX)
  public String getActivityStreamsIRI() {
    return W3C_ACTIVITYSTREAMS_JSONLD_IRI;
  }

  @JsonProperty(XDM_EVENT_ENVELOPE_PREFIX)
  public String getXdmEnvelopeIRI() {
    return XDM_EVENT_ENVELOPE_JSONLD_IRI;
  }

  @JsonProperty(XDM_COMPONENTIZED_PAGE_PREFIX)
  public String getXdmComponentizedPageIRI() {
    return XDM_COMPONENTIZED_PAGE_JSONLD_IRI;
  }

  @JsonProperty(XDM_IMS_ORG_PREFIX)
  public String getXdmImsOrgIRI() {
    return XDM_IMS_ORG_JSONLD_IRI;
  }

  @JsonProperty(XDM_CONTENT_REPOSITORY_PREFIX)
  public String getXdmContentRepositoryIRI() {
    return XDM_CONTENT_REPOSITORY_JSONLD_IRI;
  }

  @JsonProperty(XDM_AEM_USER_PREFIX)
  public String getXdmAemUserIRI() {
    return XDM_AEM_USER_JSONLD_IRI;
  }

  @JsonProperty(XDM_IMS_USER_PREFIX)
  public String getXdmImsUserIRI() {
    return XDM_IMS_USER_JSONLD_IRI;
  }

  @JsonProperty(XDM_ASSET_PREFIX)
  public String getXdmAssetIRI() {
    return XDM_ASSET_JSONLD_IRI;
  }

  @JsonProperty("xdmDirectory")
  public String getXdmDirectoryIRI() {
    return XDM_DIRECTORY_JSONLD_IRI;
  }

  @JsonProperty(XDM_EVENT_CREATED_TYPE)
  public String getXdmCreatedIRI() {
    return XDM_EVENT_CREATED_JSONLD_IRI;
  }

  @JsonProperty(XDM_EVENT_UPDATED_TYPE)
  public String getXdmUpdatedIRI() {
    return XDM_EVENT_UPDATED_JSONLD_IRI;
  }

  @JsonProperty(XDM_EVENT_DELETED_TYPE)
  public String getXdmDeletedIRI() {
    return XDM_EVENT_DELETED_JSONLD_IRI;
  }

  @JsonProperty(XDM_EVENT_PUBLISHED_TYPE)
  public String getXdmPublishedIRI() {
    return XDM_EVENT_PUBLISHED_JSONLD_IRI;
  }

  @JsonProperty(XDM_EVENT_UNPUBLISHED_TYPE)
  public String getXdmUnpublishedIRI() {
    return XDM_EVENT_UNPUBLISHED_JSONLD_IRI;
  }

  @JsonProperty(XDM_EVENT_EMITTED_TYPE)
  public String getXdmEmittedIRI() {
    return XDM_EVENT_EMITTED_TYPE_JSONLD_IRI;
  }

  @JsonProperty(OSGI_EVENT_TYPE)
  public String getOsgiEventdIRI() {
    return OSGI_EVENT_JSONLD_IRI;
  }

}
