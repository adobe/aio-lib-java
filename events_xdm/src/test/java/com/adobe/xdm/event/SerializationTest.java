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
package com.adobe.xdm.event;

import static org.junit.Assert.assertTrue;

import com.adobe.xdm.Xdm;
import com.adobe.xdm.assets.Asset;
import com.adobe.xdm.content.ContentRepository;
import com.adobe.xdm.content.Page;
import com.adobe.xdm.extensions.aem.AemUser;
import com.adobe.xdm.extensions.aem.OsgiEvent;
import com.adobe.xdm.extensions.ims.ImsOrg;
import com.adobe.xdm.extensions.ims.ImsUser;
import com.adobe.xdm.external.repo.Directory;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Hashtable;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SerializationTest {

  Logger logger = LoggerFactory.getLogger(SerializationTest.class);
  private static final String PUBLISHED_DATE = "1970-01-01T01:00:00.000+01";
  //the above is new SimpleDateFormat(XdmContext.DATE_FORMAT,Locale.US).format(new Date(0));
  private ObjectMapper mapper;

  @Before
  public void setUp() {
    mapper = new ObjectMapper();
  }

  private CCAssetEvent getCCAssetSampleEvent(CCAssetEvent assetEvent) {
    assetEvent.setId("82235bac-2b81-4e70-90b5-2bd1f04b5c7b");
    assetEvent.setPublished(PUBLISHED_DATE);
    ImsUser imsUser = new ImsUser();
    imsUser.setImsUserId("D13A1E7053E46A220A4C86E1@AdobeID");
    assetEvent.setTo(imsUser);
    assetEvent.setActor(imsUser);

    ContentRepository creativeCloud = new ContentRepository();
    creativeCloud.setRoot("https://cc-api-storage.adobe.io/");
    assetEvent.setGenerator(creativeCloud);

    Asset asset = new Asset();
    asset.setFormat("image/jpeg");
    asset.setAssetId("urn:aaid:sc:us:4123ba4c-93a8-4c5d-b979-ffbbe4318185");
    asset.setAssetName("example.jpg");
    asset.setEtag("6fc55d0389d856ae7deccebba54f110e");
    asset.setPath("/MyFolder/example.jpg");
    assetEvent.setObject(asset);
    return assetEvent;
  }

  private CCDirectoryEvent getCCDirectorySampleEvent(CCDirectoryEvent directoryEvent) {
    directoryEvent.setId("82235bac-2b81-4e70-90b5-2bd1f04b5c7b");
    directoryEvent.setPublished(PUBLISHED_DATE);
    ImsUser imsUser = new ImsUser();
    imsUser.setImsUserId("D13A1E7053E46A220A4C86E1@AdobeID");
    directoryEvent.setTo(imsUser);
    directoryEvent.setActor(imsUser);

    ContentRepository creativeCloud = new ContentRepository();
    creativeCloud.setRoot("https://cc-api-storage.adobe.io/");
    directoryEvent.setGenerator(creativeCloud);

    Directory directory = new Directory();
    directory.setFormat("application/vnd.adobecloud.directory+json");
    directory.setAssetId("urn:aaid:sc:us:4123ba4c-93a8-4c5d-b979-ffbbe4318185");
    directory.setName("example");
    directory.setEtag("6fc55d0389d856ae7deccebba54f110e");
    directory.setPath("/MyFolder/example");
    directoryEvent.setObject(directory);
    return directoryEvent;
  }

  private <T> void assertDeserialization(T xdmEvent, String xdmEventJsonFile,
      Class<T> xdmEventClass)
      throws IOException {
    T xdmEventFromFile = mapper.readValue(readFile(xdmEventJsonFile), xdmEventClass);
    assertTrue(xdmEventFromFile.equals(xdmEvent));
  }

  @Test
  public void testCCAssetCreatedEventSerialization() throws IOException {
    CCAssetEvent xdmEvent = getCCAssetSampleEvent(new CCAssetCreatedEvent());
    String prettyString = JsonUtils.toPrettyString(xdmEvent);
    logger.info(prettyString);
    assertDeserialization(xdmEvent, "asset_created_cc_sample.json", CCAssetEvent.class);
  }

  @Test
  public void testCCAssetUpdatedEventSerialization() throws IOException {
    CCAssetEvent xdmEvent = getCCAssetSampleEvent(new CCAssetUpdatedEvent());
    String prettyString = JsonUtils.toPrettyString(xdmEvent);
    logger.info(prettyString);

    assertDeserialization(xdmEvent, "asset_updated_cc_sample.json", CCAssetEvent.class);
  }

  @Test
  public void testCCAssetDeletedEventSerialization() throws IOException {
    CCAssetEvent assetEvent = getCCAssetSampleEvent(new CCAssetDeletedEvent());
    Asset asset = new Asset();
    asset.setAssetId("urn:aaid:sc:us:4123ba4c-93a8-4c5d-b979-ffbbe4318185");
    assetEvent.setObject(asset);
    String prettyString = JsonUtils.toPrettyString(assetEvent);
    logger.info(prettyString);

    assertDeserialization(assetEvent, "asset_deleted_cc_sample.json", CCAssetEvent.class);
  }

  @Test
  public void testCCDirectoryCreatedEventSerialization() throws IOException {
    CCDirectoryEvent xdmEvent = getCCDirectorySampleEvent(new CCDirectoryCreatedEvent());
    String prettyString = JsonUtils.toPrettyString(xdmEvent);
    logger.info(prettyString);
    assertDeserialization(xdmEvent, "directory_created_cc_sample.json", CCDirectoryEvent.class);
  }

  private AemAssetEvent getAemAssetSampleEvent(AemAssetEvent assetEvent) {
    assetEvent.setId("82235bac-2b81-4e70-90b5-2bd1f04b5c7b");
    assetEvent.setPublished(PUBLISHED_DATE);
    ImsOrg imsOrg = new ImsOrg();
    imsOrg.setImsOrgId("08B3E5CE5822FC520A494229@AdobeOrg");
    assetEvent.setTo(imsOrg);

    ContentRepository aemInstance = new ContentRepository();
    aemInstance.setRoot("http://francois.corp.adobe.com:4502/");
    assetEvent.setGenerator(aemInstance);

    AemUser aemUser = new AemUser();
    aemUser.setAemUserId("admin");
    assetEvent.setActor(aemUser);

    Asset asset = new Asset();
    asset.setFormat("image/png");
    asset.setAssetId("urn:aaid:aem:4123ba4c-93a8-4c5d-b979-ffbbe4318185");
    asset.setAssetName("Fx_DUKE-small.png");
    asset.setEtag("6fc55d0389d856ae7deccebba54f110e");
    asset.setPath("/content/dam/Fx_DUKE-small.png");
    assetEvent.setObject(asset);
    return assetEvent;
  }


  @Test
  public void testAemAssetCreatedEventSerialization() throws IOException {
    AemAssetEvent xdmEvent = getAemAssetSampleEvent(new AemAssetCreatedEvent());
    String prettyString = JsonUtils.toPrettyString(xdmEvent);
    logger.info(prettyString);
    assertDeserialization(xdmEvent, "asset_created_aem_sample.json", AemAssetEvent.class);
  }

  @Test
  public void testAemAssetUpdatedEventSerialization() throws IOException {
    AemAssetEvent xdmEvent = getAemAssetSampleEvent(new AemAssetUpdatedEvent());
    String prettyString = JsonUtils.toPrettyString(xdmEvent);
    logger.info(prettyString);
    assertDeserialization(xdmEvent, "asset_updated_aem_sample.json", AemAssetEvent.class);
  }

  @Test
  public void testAemAssetDeletedEventSerialization() throws IOException {
    AemAssetEvent assetEvent = getAemAssetSampleEvent(new AemAssetDeletedEvent());
    Asset asset = new Asset();
    asset.setAssetId("urn:aaid:aem:4123ba4c-93a8-4c5d-b979-ffbbe4318185");
    assetEvent.setObject(asset);
    String prettyString = JsonUtils.toPrettyString(assetEvent);
    logger.info(prettyString);

    assertDeserialization(assetEvent, "asset_deleted_aem_sample.json", AemAssetEvent.class);
  }


  private AemPageEvent getAemPageSampleEvent(AemPageEvent pageEvent) {
    pageEvent.setId("82235bac-2b81-4e70-90b5-2bd1f04b5c7b");
    pageEvent.setPublished(PUBLISHED_DATE);

    ImsOrg imsOrg = new ImsOrg();
    imsOrg.setImsOrgId("08B3E5CE5822FC520A494229@AdobeOrg");
    pageEvent.setTo(imsOrg);

    ContentRepository aemInstance = new ContentRepository();
    aemInstance.setRoot("http://francois.corp.adobe.com:4502/");
    pageEvent.setGenerator(aemInstance);

    AemUser aemUser = new AemUser();
    aemUser.setAemUserId("admin");
    pageEvent.setActor(aemUser);

    Page page = new Page();
    page.setTitle("Vintage Collection");
    page.setPath("/content/geometrixx/en/vintage.html");
    page.setId("http://adobesummit.adobesandbox.com:4502/content/geometrixx/en/vintage.html");
    pageEvent.setObject(page);
    return pageEvent;
  }

  @Test
  public void testAemPagePublishedEventSerialization() throws IOException {
    AemPageEvent xdmEvent = getAemPageSampleEvent(new AemPagePublishedEvent());
    String prettyString = JsonUtils.toPrettyString(xdmEvent);
    logger.info(prettyString);

    assertDeserialization(xdmEvent, "page_published_aem_sample.json", AemPageEvent.class);
  }

  @Test
  public void testAemPageUnpublishedEventSerialization() throws IOException {
    AemPageEvent xdmEvent = getAemPageSampleEvent(new AemPageUnpublishedEvent());
    String prettyString = JsonUtils.toPrettyString(xdmEvent);
    logger.info(prettyString);

    assertDeserialization(xdmEvent, "page_unpublished_aem_sample.json", AemPageEvent.class);
  }

  private OsgiEmittedEvent getOsgiEmittedEventSampleEvent() {
    OsgiEmittedEvent osgiEmittedEvent = new OsgiEmittedEvent();

    osgiEmittedEvent.setId("82235bac-2b81-4e70-90b5-2bd1f04b5c7b");
    osgiEmittedEvent.setPublished(PUBLISHED_DATE);
    ImsOrg imsOrg = new ImsOrg();
    imsOrg.setImsOrgId("08B3E5CE5822FC520A494229@AdobeOrg");
    osgiEmittedEvent.setTo(imsOrg);

    ContentRepository aemInstance = new ContentRepository();
    aemInstance.setRoot("http://francois.corp.adobe.com:4502/");
    osgiEmittedEvent.setGenerator(aemInstance);

    Hashtable properties = new Hashtable();
    properties.put("type", "created");
    properties.put("id", "1234");
    OsgiEvent osgiEvent = new OsgiEvent();
    osgiEvent.setTopic("io/adobe/event/sample/sku");
    osgiEvent.setProperties(properties);

    osgiEmittedEvent.setObject(osgiEvent);
    return osgiEmittedEvent;
  }

  @Test
  public void testOsgiEmittedEventSampleEventSerialization() throws IOException {
    OsgiEmittedEvent xdmEvent = getOsgiEmittedEventSampleEvent();
    String prettyString = JsonUtils.toPrettyString(xdmEvent);
    logger.info(prettyString);
    assertDeserialization(xdmEvent, "custom_osgi_emitted_aem_sample.json", OsgiEmittedEvent.class);
  }

  @Test
  public void testXdmContextSerialization() throws IOException {
    Xdm xdm = new Xdm();
    String prettyString = JsonUtils.toPrettyString(xdm);
    logger.info(prettyString);
  }

  private static String readFile(String fileName) throws IOException {
    byte[] encoded = Files.readAllBytes(Paths.get(
        Thread.currentThread().getContextClassLoader()
            .getResource(fileName).getPath()));
    return new String(encoded, StandardCharsets.UTF_8);
  }

}
