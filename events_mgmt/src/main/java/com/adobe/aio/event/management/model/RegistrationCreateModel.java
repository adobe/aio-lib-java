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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;

@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class RegistrationCreateModel extends RegistrationUpdateModel {

    @JsonProperty("client_id")
    private final String clientId;

    private RegistrationCreateModel(final String clientId, final String name, final String description,
                    final String deliveryType, final String runtimeAction,
                    final Set<EventsOfInterestInputModel> eventsOfInterestInputModels,
                    final String webhookUrl, final boolean enabled, final Set<CreateSubscriberFilterModel> subscriberFilters) {
        super(name, description, webhookUrl, eventsOfInterestInputModels, deliveryType, runtimeAction, enabled, subscriberFilters);
        if (StringUtils.isBlank(clientId)) {
            throw new IllegalArgumentException(
                            "Registration is missing a clientId");
        }
        this.clientId = clientId;
    }

    public String getClientId() {
        return clientId;
    }

    @Override public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RegistrationCreateModel)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        RegistrationCreateModel that = (RegistrationCreateModel) o;
        return Objects.equals(clientId, that.clientId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clientId, name, description, deliveryType, runtimeAction, eventsOfInterestInputModels, webhookUrl, enabled, subscriberFilters);
    }

    @Override
    public String toString() {
        return "RegistrationInputModel{" +
                        "clientId='" + clientId + '\'' +
                        ", name='" + name + '\'' +
                        ", description='" + description + '\'' +
                        ", deliveryType=" + deliveryType +
                        ", runtimeAction=" + runtimeAction +
                        ", eventsOfInterestInputModels=" + eventsOfInterestInputModels +
                        ", webhookUrl='" + webhookUrl + '\'' +
                        ", enabled='" + enabled + '\'' +
                        ", subscriberFilters=" + subscriberFilters +
                        '}';
    }

    @JsonIgnore
    public static Builder builder() {
        return new Builder();
    }


    public static class Builder extends RegistrationUpdateModel.Builder<Builder> {
        protected String clientId;

        public Builder clientId(String clientId) {
            this.clientId = clientId;
            return this;
        }

        @Override
        public RegistrationCreateModel build() {
            return new RegistrationCreateModel(clientId, name, description, deliveryType, runtimeAction,
                eventsOfInterestInputModels, webhookUrl, enabled, subscriberFilters);
        }
    }
}
