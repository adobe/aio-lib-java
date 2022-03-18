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
package com.adobe.aio.aem.event.publish.ocd;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name = "Adobe I/O Events' Publish API Configuration",
    description = "Adobe I/O Events' Publish API Configuration")
public @interface ApiPublishingConfig {

  @AttributeDefinition(name = "Adobe I/O Events' Publish API URL",
      description = "prod: https://eventsingress.adobe.io | stage: https://eventsingress-stage.adobe.io")
  String aio_publish_url() default "https://eventsingress.adobe.io";

}
