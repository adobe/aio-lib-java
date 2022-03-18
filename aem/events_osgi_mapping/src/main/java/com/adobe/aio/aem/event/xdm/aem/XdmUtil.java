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
package com.adobe.aio.aem.event.xdm.aem;

import com.adobe.aio.aem.event.xdm.event.AemPageModificationEvent;
import com.adobe.aio.exception.AIOException;
import com.adobe.xdm.XdmObject;
import com.adobe.xdm.assets.Asset;
import com.adobe.xdm.common.XdmContext;
import com.adobe.xdm.common.XdmEvent;
import com.adobe.xdm.content.ContentRepository;
import com.adobe.xdm.content.Page;
import com.adobe.xdm.event.AemAssetCreatedEvent;
import com.adobe.xdm.event.AemAssetDeletedEvent;
import com.adobe.xdm.event.AemAssetUpdatedEvent;
import com.adobe.xdm.event.AemPagePublishedEvent;
import com.adobe.xdm.event.AemPageUnpublishedEvent;
import com.adobe.xdm.event.OsgiEmittedEvent;
import com.adobe.xdm.extensions.aem.AemUser;
import com.adobe.xdm.extensions.ims.ImsOrg;
import com.day.cq.dam.api.DamConstants;
import java.lang.reflect.InvocationTargetException;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Predicate;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import org.apache.jackrabbit.JcrConstants;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;

public class XdmUtil {

  public static final String URN_OBJECT_ID_PREFIX = "urn:aaid:aem:";
  public static final String URN_EVENT_ID_PREFIX = "urn:oeid:aem:";
  public static final String PAGE_PRIMARY_TYPE = "cq:Page";
  public static final String NON_AVAILABLE = "N/A";

  private XdmUtil() {
  }


  public static XdmEvent getXdmEvent(String xdmEventType) {
    try {
      /*
       * CloudManager won't let us use dynamic class loading:
       * hence all these ifs and elses below
       */
      Class<?> xdmClassDefinition;
      if (xdmEventType.equals(OsgiEmittedEvent.class.getCanonicalName())) {
        xdmClassDefinition = OsgiEmittedEvent.class;
      } else if (xdmEventType.equals(AemAssetCreatedEvent.class.getCanonicalName())) {
        xdmClassDefinition = AemAssetCreatedEvent.class;
      } else if (xdmEventType.equals(AemAssetUpdatedEvent.class.getCanonicalName())) {
        xdmClassDefinition = AemAssetUpdatedEvent.class;
      } else if (xdmEventType.equals(AemAssetDeletedEvent.class.getCanonicalName())) {
        xdmClassDefinition = AemAssetDeletedEvent.class;
      } else if (xdmEventType.equals(AemPagePublishedEvent.class.getCanonicalName())) {
        xdmClassDefinition = AemPagePublishedEvent.class;
      } else if (xdmEventType.equals(AemPageUnpublishedEvent.class.getCanonicalName())) {
        xdmClassDefinition = AemPageUnpublishedEvent.class;
      } else if (xdmEventType.equals(AemPageModificationEvent.class.getCanonicalName())) {
        xdmClassDefinition = AemPageModificationEvent.class;
      } else {
        throw new ClassNotFoundException(
            "Unexpected/unimplemented/unmapped xdm event class: " +
                xdmEventType);
      }
      return (XdmEvent) xdmClassDefinition.getDeclaredConstructor().newInstance();
    } catch (IllegalAccessException | InstantiationException | ClassNotFoundException | NoSuchMethodException | InvocationTargetException e) {
      throw new AIOException(
          "Invalid Adobe I/O Events' OSGI Event Mapping `aio.xdm.event.type`. "
              + xdmEventType + " could not be used due to " + e.getMessage(),
          e);
    }
  }


  public static AemUser getAemUser(String userId) {
    AemUser aemUser = new AemUser();
    if (userId != null) {
      aemUser.setId(userId);
    } else {
      aemUser.setId(NON_AVAILABLE);
    }
    return aemUser;
  }


  private static ContentRepository getContentRepository(String rootUrl) {
    ContentRepository aemInstance = new ContentRepository();
    aemInstance.setRoot(rootUrl);
    return aemInstance;
  }

  public static Optional<Asset> getDeletedAsset(String path, Predicate<String> isPathOfInterest) {
    if (!isPathOfInterest.test(path)) {
      return Optional.empty();
    } else {
      Asset asset = new Asset();
      asset.setAssetId(NON_AVAILABLE);
      asset.setPath(path);
      return Optional.of(asset);
    }
  }

  public static Optional<Asset> getAsset(Resource resource) {
    if (!isPrimaryTypeAsset(resource)) {
      return Optional.empty();
    }

    Asset asset = new Asset();
    //TODO use https://git.corp.adobe.com/CQ/adobecloud-api once it's released
    /*
    we pick the jcr path
    cf. https://git.corp.adobe.com/CQ/adobecloud-apis/blob/master/bundles/adobecloud-repository-api/src/main/java/com/adobe/cq/adobecloud/repository/impl/ResourceModelImpl.java#L285-L291
     */
    asset.setPath(resource.getPath());
    /*
    The (node) name of the resource, which should also match the last name in the path of the resource.
    cf. https://git.corp.adobe.com/CQ/adobecloud-apis/blob/master/bundles/adobecloud-repository-api/src/main/java/com/adobe/cq/adobecloud/repository/impl/ResourceModelImpl.java#L276-L280
     */
    asset.setAssetName(resource.getName());
    asset.setAssetId(getNodeJcrUuid(resource).orElse(NON_AVAILABLE));
    /*
      there is a dc:format property for assets at jcr:content/metadata.
      cf. https://git.corp.adobe.com/CQ/adobecloud-apis/blob/master/bundles/adobecloud-repository-api/src/main/java/com/adobe/cq/adobecloud/repository/impl/AssetModelImpl.java#L215-L219
     */
    Resource jcrContent = resource.getChild(JcrConstants.JCR_CONTENT);
    if (jcrContent != null) {
      Resource metadataResource = jcrContent.getChild("metadata");
      if (metadataResource != null) {
        ValueMap metadataValueMap = metadataResource.getValueMap();
        asset.setFormat(metadataValueMap.get("dc:format", String.class));
        /* We are working to get a true etag property for assets in AEM. Until then there is a bit of a hack in place:
        Use the dam:sha1 jcr:content/metadata property (which actually isn’t set right away after upload or updates—needs a up to a few seconds to be set) and,
        if it isn’t available, the current time. we hope to eventually replace this with an etag that is based on the original rendition binary data and set by the system.
        cf. https://git.corp.adobe.com/CQ/adobecloud-apis/blob/master/bundles/adobecloud-repository-api/src/main/java/com/adobe/cq/adobecloud/repository/impl/AssetModelImpl.java#L227-L245
        */

        // Use dam:sha1 for now and submit a request to Oak that an oak:etag be generated.
        // Other design idea: Keep a dam:etag that is a GUID, and a dam:etagLastModified.
        // When this value is requested, check the jcr:content/jcr:lastModified value.
        // If it is newer than dam:etagLastModified then generate a new GUID for dam:etag
        // and update dam:etagLastModified.
        String etag = metadataValueMap.get("dam:sha1", String.class);
        if (etag == null) {
          // If dam:sha1 is not set, just generate something random
          SecureRandom random = new SecureRandom();
          etag = System.currentTimeMillis() + "-" + random.nextInt(1000000);
        }
        asset.setEtag(etag);
      }
    }
    return Optional.of(asset);
  }

  public static Optional<String> getNodeJcrUuid(Resource resource) {
    return getNodePrefixedPropertyValue(resource, JcrConstants.JCR_UUID, URN_OBJECT_ID_PREFIX);
  }

  public static boolean isPrimaryTypePage(Resource resource) {
    return isNodeProperty(resource, JcrConstants.JCR_PRIMARYTYPE, PAGE_PRIMARY_TYPE);
  }

  public static boolean isPrimaryTypeAsset(Resource resource) {
    return isNodeProperty(resource, JcrConstants.JCR_PRIMARYTYPE, DamConstants.NT_DAM_ASSET);
  }

  private static boolean isNodeProperty(Resource resource, String propertyName,
      String expectedPropertyValue) {
    return (expectedPropertyValue
        .equals(getNodePropertyValue(resource, propertyName).orElse(null)));
  }

  private static Optional<String> getNodePropertyValue(Resource resource,
      String propertyName) {
    return getNodePrefixedPropertyValue(resource, propertyName, null);
  }

  private static Optional<String> getNodePrefixedPropertyValue(Resource resource,
      String propertyName, String valuePrefix) {
    if (resource == null) {
      return Optional.empty();
    }
    Node node = resource.adaptTo(Node.class);
    if (node == null) {
      return Optional.empty();
    }
    try {
      if (!node.hasProperty(propertyName)) {
        return Optional.empty();
      }
      String value = node.getProperty(propertyName).getString();
      return Optional.of(valuePrefix != null ? valuePrefix + value : value);
    } catch (RepositoryException e) {
      throw new AIOException
          ("Could not get the value of " + propertyName, e);
    }
  }

  public static ImsOrg getImsOrg(String imsOrgId) {
    ImsOrg imsOrg = new ImsOrg();
    imsOrg.setImsOrgId(imsOrgId);
    return imsOrg;
  }

  public static XdmEvent getXdmEvent(
      XdmObject object, XdmObject actor, Date publicationDate,
      String imsOrgId, String rootUrl,
      String xdmEventType) {
    XdmEvent xdmEvent = getXdmEvent(xdmEventType);
    xdmEvent.setTo(getImsOrg(imsOrgId));
    xdmEvent.setGenerator(getContentRepository(rootUrl));
    xdmEvent.setId(URN_EVENT_ID_PREFIX + UUID.randomUUID().toString());
    xdmEvent.setObject(object);
    xdmEvent.setPublished(new SimpleDateFormat(XdmContext.DATE_FORMAT,
        Locale.US).format(publicationDate));
    xdmEvent.setActor(actor);
    return xdmEvent;
  }

  public static Optional<Page> getPage(Resource cqPageResource) {
    if (!isPrimaryTypePage(cqPageResource) || cqPageResource == null) {
      return Optional.empty();
    }

    Page page = new Page();
    page.setId(
        getNodeJcrUuid(cqPageResource.getChild(JcrConstants.JCR_CONTENT)).orElse(NON_AVAILABLE));
    page.setVersion(getNodePrefixedPropertyValue(cqPageResource.getChild(JcrConstants.JCR_CONTENT),
        JcrConstants.JCR_VERSIONHISTORY, null).orElse(NON_AVAILABLE));
    page.setPath(cqPageResource.getPath());
    com.day.cq.wcm.api.Page cQPage = cqPageResource.adaptTo(com.day.cq.wcm.api.Page.class);
    page.setTitle(cQPage != null ? cQPage.getTitle() : "");
    return Optional.of(page);
  }

  public static Optional<Page> getDeletedPage(String path, Predicate<String> isPathOfInterest) {
    if (!isPathOfInterest.test(path)) {
      return Optional.empty();
    } else {
      Page page = new Page();
      page.setId(NON_AVAILABLE);
      page.setVersion(NON_AVAILABLE);
      page.setPath(path);
      return Optional.of(page);
    }
  }


  public static <T> Optional<T> getXdmObjectFromResource(Resource resource,
      Predicate<String> isPathOfInterest, Function<Resource, Optional<T>> converter) {
    if (!isPathOfInterest.test(resource.getPath())) {
      return Optional.empty();
    } else {
      return converter.apply(resource);
    }
  }

//  public static <T> Optional<T> getXdmObjectFromPath(
//      ResourceResolverFactory resourceResolverFactory,
//      Map<String, Object> authenticationInfo,
//      String path,
//      Predicate<String> isPathOfInterest,
//      Function<Resource, Optional<T>> getXdmObjectFromResource) {
//    if (!isPathOfInterest.test(path)) {
//      return Optional.empty();
//    } else {
//      try (ResourceResolver resourceResolver = resourceResolverFactory
//          .getServiceResourceResolver(authenticationInfo)) {
//        Resource resource = resourceResolver.getResource(path);
//        return getXdmObjectFromResource.apply(resource);
//      } catch (LoginException e) {
//        throw new EventProxyRuntimeException(
//            "Configuration error, Could not access the resource path associated with the osgi event: "
//                + e.getMessage(), e);
//      }
//    }
//  }

}
