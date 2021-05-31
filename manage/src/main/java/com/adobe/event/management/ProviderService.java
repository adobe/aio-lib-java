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
package com.adobe.event.management;

import com.adobe.event.management.model.Provider;
import java.util.List;
import java.util.Optional;

public interface ProviderService {

  List<Provider> getProviders(String consumerOrgId);

  Optional<Provider> findById(String id);

  Optional<Provider> findBy(String consumerOrgId, String providerMetadataId, String instanceId);

}
