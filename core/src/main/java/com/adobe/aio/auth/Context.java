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
   * Property name for looking up Authentication Client Secret in various contexts.
   * Reference: <a href="https://developer.adobe.com/developer-console/docs/guides/authentication/ServerToServerAuthentication/#server-to-server-credential-types">AIO Developer Documentation</a>
   */
  String CLIENT_SECRET = "aio_client_secret";

  /**
   * Validates this context is minimally populated and able to function.
   *
   * @throws IllegalStateException if this context is not valid for use for generating access tokens
   */
  default void validate() throws IllegalStateException {}
}
