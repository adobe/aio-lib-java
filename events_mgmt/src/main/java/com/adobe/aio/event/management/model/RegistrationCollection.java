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
package com.adobe.aio.event.management.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import io.openapitools.jackson.dataformat.hal.HALLink;
import io.openapitools.jackson.dataformat.hal.annotation.EmbeddedResource;
import io.openapitools.jackson.dataformat.hal.annotation.Link;
import io.openapitools.jackson.dataformat.hal.annotation.Resource;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

@Resource
@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class RegistrationCollection {

  @Link
  private HALLink self;

  @EmbeddedResource("registrations")
  private Collection<Registration> registrationHalModels;

  public HALLink getSelf() {
    return self;
  }

  public void setSelf(HALLink self) {
    this.self = self;
  }

  public Collection<Registration> getRegistrationHalModels() {
    return Collections.unmodifiableCollection(registrationHalModels);
  }

  public void setRegistrationHalModels(Collection<Registration> registrationHalModels) {
    this.registrationHalModels = registrationHalModels;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof RegistrationCollection)) {
      return false;
    }
    RegistrationCollection that = (RegistrationCollection) o;
    return Objects.equals(self, that.self) &&
                    Objects.equals(registrationHalModels, that.registrationHalModels);
  }

  @Override
  public int hashCode() {
    return Objects.hash(self, registrationHalModels);
  }

  @Override
  public String toString() {
    return "RegistrationCollectionHalModel{" +
                    "self=" + self +
                    ", RegistrationHalModels=" + registrationHalModels +
                    '}';
  }
}
