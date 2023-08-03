/*
 * Copyright 2023 Adobe. All rights reserved.
 * This file is licensed to you under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License. You may obtain a copy
 * of the License at http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under
 * the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR REPRESENTATIONS
 * OF ANY KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package com.adobe.aio.auth;

/**
 * Represents an Authentication context to Adobe IMS.
 *
 * @since 1.1
 */
public interface Context {

  /**
   * Property name used in maps and config files for setting the AIO IMS URL.
   */
  public static final String IMS_URL = "aio_ims_url";
  /**
   * Property name used in maps and config files for setting the AIO IMS Org Id.
   */
  public static final String IMS_ORG_ID = "aio_ims_org_id";

  /**
   * Validates this context is minimally populated and able to function.
   */
  void validate();
}
