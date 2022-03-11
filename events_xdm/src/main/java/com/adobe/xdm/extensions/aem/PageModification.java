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
package com.adobe.xdm.extensions.aem;

import com.adobe.xdm.content.Page;
import com.day.cq.wcm.api.PageModification.ModificationType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Date;
import java.util.Optional;
import java.util.Set;

@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PageModification extends Page {

  public static final String PAGE_MODIFICATION_TYPE = "pageModification";

  private final String movedFromPath;
  private final String above;
  private final String vid;
  private final Date modificationDate;
  private final Set<String> modificationPaths;
  private final ModificationType modificationType;

  public static Optional<PageModification> of(Page page,
      com.day.cq.wcm.api.PageModification pageModification) {
    if (page == null) {
      return Optional.empty();
    } else if (pageModification.getType().equals(ModificationType.MOVED)) {
      return Optional.of(new PageModification(page,
          pageModification.getType(),
          pageModification.getVersionId(),
          pageModification.getPath(),
          pageModification.getAbove(),
          pageModification.getModificationDate(),
          pageModification.getModificationPaths()));
    } else {
      return Optional.of(new PageModification(page,
          pageModification.getType(),
          pageModification.getVersionId(),
          null,
          pageModification.getAbove(),
          pageModification.getModificationDate(),
          pageModification.getModificationPaths()));
    }
  }

  private PageModification(Page page, ModificationType modificationType,
      String vid, String movedFromPath, String above, Date md,
      Set<String> modificationPaths) {
    super();

    this.setTitle(page.getTitle());
    this.setId(page.getId());
    this.setPath(page.getPath());
    this.setVersion(page.getVersion());
    this.setType(PAGE_MODIFICATION_TYPE);
    this.modificationType = modificationType;
    this.vid = vid;
    this.movedFromPath = movedFromPath;
    this.above = above;
    this.modificationDate = md == null ? new Date() : md;
    this.modificationPaths = modificationPaths;
  }

  @JsonProperty(PAGE_MODIFICATION_TYPE + ":moveFromPath")
  public String getMovedFromPath() {
    return movedFromPath;
  }

  @JsonProperty(PAGE_MODIFICATION_TYPE + ":above")
  public String getAbove() {
    return above;
  }

  @JsonProperty(PAGE_MODIFICATION_TYPE + ":vid")
  public String getVid() {
    return vid;
  }

  @JsonProperty(PAGE_MODIFICATION_TYPE + ":modificationDate")
  public Date getModificationDate() {
    return modificationDate;
  }

  @JsonProperty(PAGE_MODIFICATION_TYPE + ":modificationPaths")
  public Set<String> getModificationPaths() {
    return modificationPaths;
  }

  @Override
  @JsonProperty(PAGE_MODIFICATION_TYPE + ":title")
  public String getTitle() {
    return super.getTitle();
  }

  @Override
  @JsonProperty(PAGE_MODIFICATION_TYPE + ":path")
  public String getPath() {
    return super.getPath();
  }

  @JsonProperty(PAGE_MODIFICATION_TYPE + ":type")
  public String getModificationSubType() {
    return modificationType.toString();
  }

  @JsonProperty(PAGE_MODIFICATION_TYPE + ":version")
  public String getVersion() {
    return super.getVersion();
  }

  @JsonIgnore
  public ModificationType getModificationType() {
    return modificationType;
  }
}


