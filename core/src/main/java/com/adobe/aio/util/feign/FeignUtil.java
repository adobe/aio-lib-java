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
package com.adobe.aio.util.feign;

import static java.util.concurrent.TimeUnit.SECONDS;

import com.adobe.aio.util.JacksonUtil;
import feign.Feign;
import feign.Logger;
import feign.Logger.Level;
import feign.Request;
import feign.Retryer;
import feign.form.FormEncoder;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import feign.optionals.OptionalDecoder;
import feign.slf4j.Slf4jLogger;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class FeignUtil {

  /**
   * Environment variable to set the Feign logger class.
   * The value should be the fully qualified class name of the logger to use.
   * The name should be the result of calling {@link Class#getName()} on the logger class.
   * @see feign.Logger
   */
  public static final String AIO_FEIGN_LOGGER_CLASS = "AIO_FEIGN_LOGGER_CLASS";
  /**
   * Environment variable to set the log level for Feign clients.
   * The value should be one of the following: NONE, BASIC, HEADERS, FULL
   * @see Level
   */
  public static final String AIO_FEIGN_LOG_LEVEL = "AIO_FEIGN_LOG_LEVEL";
  /**
   * Environment variable to set the retry period in milliseconds for Feign clients, using a {@link Retryer.Default}
   */
  public static final String AIO_FEIGN_RETRY_PERIOD = "AIO_FEIGN_RETRY_PERIOD";
  /**
   * Environment variable to set the max attempts for Feign clients, using a {@link Retryer.Default}
   */
  public static final String AIO_FEIGN_RETRY_MAX_ATTEMPTS = "AIO_FEIGN_RETRY_MAX_ATTEMPTS";
  /**
   * Environment variable to set the max period in seconds for Feign clients, using a {@link Retryer.Default}
   */
  public static final String AIO_FEIGN_RETRY_MAX_PERIOD = "AIO_FEIGN_RETRY_MAX_PERIOD";


  private static final int DEFAULT_CONNECT_TIMEOUT_IN_SECONDS = 10;
  private static final int DEFAULT_READ_TIMEOUT_IN_SECONDS = 60;
  public static final long DEFAULT_RETRY_PERIOD_IN_SECONDS = 1000L;
  public static final int DEFAULT_MAX_ATTEMPTS = 3;
  public static final long DEFAULT_MAX_PERIOD_IN_SECONDS = 4L;

  private FeignUtil() {
  }

  /**
   * @return the base Feign builder we want to reuse across the sdk with jackson decoder, logger and
   * our global read and time out options
   */
  public static Feign.Builder getBaseBuilder() {
    return new ConfigBuilder().systemEnv().build();
  }

  /**
   * @return a Feign builder we want to reuse across the sdk with jackson encoder and decoder,
   * logger and our global read and time out options
   */
  public static Feign.Builder getDefaultBuilder() {
    return getBaseBuilder()
        .decoder(new OptionalDecoder(new JacksonDecoder(JacksonUtil.DEFAULT_OBJECT_MAPPER)))
        .encoder(new JacksonEncoder(JacksonUtil.DEFAULT_OBJECT_MAPPER));

  }

  /**
   * @return a Feign builder we want to reuse across the sdk with jackson decoder, logger and our
   * global read and time out options, and form encoder
   */
  public static Feign.Builder getBuilderWithFormEncoder() {
    return getBaseBuilder()
        .decoder(new OptionalDecoder(new JacksonDecoder(JacksonUtil.DEFAULT_OBJECT_MAPPER)))
        .encoder(new FormEncoder());
  }

  public static class ConfigBuilder {
    private Class<Logger> loggerClass;
    private Level logLevel;
    private long retryPeriod;
    private int maxAttempts;
    private long maxPeriod;

    public ConfigBuilder() {
    }

    /**
     * @param loggerClass the logger class to use
     *                    @see feign.Logger
     * @return the current instance of the builder for chaining
     */
    public ConfigBuilder loggerClass(final Class<Logger> loggerClass) {
      this.loggerClass = loggerClass;
      return this;
    }

    public ConfigBuilder logLevel(final Level logLevel) {
      this.logLevel = logLevel;
      return this;
    }

    public ConfigBuilder retryPeriod(final long retryPeriod) {
      this.retryPeriod = retryPeriod;
      return this;
    }

    public ConfigBuilder maxAttempts(final int maxAttempts) {
      this.maxAttempts = maxAttempts;
      return this;
    }

    public ConfigBuilder maxPeriod(final long maxPeriod) {
      this.maxPeriod = maxPeriod;
      return this;
    }

    public Class<Logger> getLoggerClass() {
      return loggerClass;
    }

    public Level getLogLevel() {
      return logLevel;
    }

    public long getRetryPeriod() {
      return retryPeriod;
    }

    public int getMaxAttempts() {
      return maxAttempts;
    }

    public long getMaxPeriod() {
      return maxPeriod;
    }

    public ConfigBuilder configMap(final Map<String, String> configMap) {
      try {
        return this
            .loggerClass((Class<Logger>) Class.forName(
                configMap.getOrDefault(AIO_FEIGN_LOGGER_CLASS, Slf4jLogger.class.getName())))
            .logLevel(Level.valueOf(
                configMap.getOrDefault(AIO_FEIGN_LOG_LEVEL, Level.NONE.name())))
            .retryPeriod(Long.parseLong(
                configMap.getOrDefault(AIO_FEIGN_RETRY_PERIOD, String.valueOf(DEFAULT_RETRY_PERIOD_IN_SECONDS))))
            .maxAttempts(Integer.parseInt(
                configMap.getOrDefault(AIO_FEIGN_RETRY_MAX_ATTEMPTS, String.valueOf(DEFAULT_MAX_ATTEMPTS))))
            .maxPeriod(Long.parseLong(
                configMap.getOrDefault(AIO_FEIGN_RETRY_MAX_PERIOD, String.valueOf(DEFAULT_MAX_PERIOD_IN_SECONDS))));
      } catch (Exception e) {
        throw new IllegalArgumentException("Provided Feign configuration is invalid", e);
      }
    }

    public ConfigBuilder systemEnv() {
      return configMap(System.getenv());
    }

    public Feign.Builder build() {
      try {
        return Feign.builder()
            .logger(this.loggerClass.getConstructor().newInstance())
            .logLevel(this.logLevel)
            .decode404()
            .retryer(
                new Retryer.Default(
                    this.retryPeriod, SECONDS.toMillis(this.maxPeriod), this.maxAttempts))
            .errorDecoder(new IOErrorDecoder())
            .options(
                new Request.Options(
                    DEFAULT_CONNECT_TIMEOUT_IN_SECONDS,
                    TimeUnit.SECONDS,
                    DEFAULT_READ_TIMEOUT_IN_SECONDS,
                    TimeUnit.SECONDS,
                    true));
      } catch (NoSuchMethodException
          | InstantiationException
          | IllegalAccessException
          | InvocationTargetException e) {
        throw new IllegalArgumentException("Provided Feign configuration is invalid", e);
      }
    }
  }

}
