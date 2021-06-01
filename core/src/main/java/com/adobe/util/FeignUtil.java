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
package com.adobe.util;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Feign;
import feign.Logger;
import feign.Logger.Level;
import feign.Request;
import feign.form.FormEncoder;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import feign.optionals.OptionalDecoder;
import feign.slf4j.Slf4jLogger;
import java.util.concurrent.TimeUnit;

public class FeignUtil {

  public static final int MAX_IDLE_CONNECTIONS = 100;
  public static final int KEEP_ALIVE_DURATION = 10;
  public static final int DEFAULT_CONNECT_TIMEOUT_IN_SECONDS = 10;
  public static final int DEFAULT_READ_TIMEOUT_IN_SECONDS = 60;
  public static final String NON_AVAILABLE = "NA";

  private static final ObjectMapper objectMapper = new ObjectMapper()
      .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

  private FeignUtil() {
  }

  /**
   * @return the base Feign builder we want to reuse across the sdk with jackson decoder, logger and
   * our global read and time out options
   */
  public static Feign.Builder getBaseBuilder() {
    return Feign.builder()
        .logger(new Slf4jLogger())
        .decode404()
        .decoder(new OptionalDecoder(new JacksonDecoder(objectMapper)))
        //.errorDecoder(new YourErrorDecoderHere(decoder)) // todo add upstream requestId in the log
        .logger(new Logger.ErrorLogger())
        .logLevel(Level.NONE) // tune this when debugging
        .options(new Request.Options(DEFAULT_CONNECT_TIMEOUT_IN_SECONDS, TimeUnit.SECONDS,
            DEFAULT_READ_TIMEOUT_IN_SECONDS, TimeUnit.SECONDS, true));
  }

  /**
   * @return a Feign builder we want to reuse across the sdk with jackson encoder and decoder,
   * logger and our global read and time out options
   */
  public static Feign.Builder getDefaultBuilderWithJacksonEncoder() {
    return getBaseBuilder()
        .encoder(new JacksonEncoder(objectMapper));

  }

  /**
   * @return a Feign builder we want to reuse across the sdk with jackson decoder, logger and our
   * global read and time out options, and form encoder
   */
  public static Feign.Builder getBuilderWithFormEncoder() {
    return getBaseBuilder()
        .encoder(new FormEncoder());

  }

}
