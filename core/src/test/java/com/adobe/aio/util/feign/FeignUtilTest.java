package com.adobe.aio.util.feign;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import feign.Logger.Level;
import feign.slf4j.Slf4jLogger;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class FeignUtilTest {

  @Test
  public void testConfigBuilderConfigMapWithEmptyMap() {
    Map<String, String> emptyConfigMap = new HashMap<>();
    assertDoesNotThrow(() -> new FeignUtil.ConfigBuilder().configMap(emptyConfigMap).build());
  }

  @Test
  public void testConfigBuilderConfigMapWithNull() {
    assertThrows(IllegalArgumentException.class, () -> new FeignUtil.ConfigBuilder().configMap(null).build());
  }

  @Test
  public void testConfigBuilderWithDefaults() {
    FeignUtil.ConfigBuilder configBuilder = new FeignUtil.ConfigBuilder().configMap(new HashMap<>());
    assertEquals(Slf4jLogger.class, configBuilder.getLoggerClass());
    assertEquals(Level.NONE, configBuilder.getLogLevel());
    assertEquals(FeignUtil.DEFAULT_RETRY_PERIOD_IN_SECONDS, configBuilder.getRetryPeriod());
    assertEquals(FeignUtil.DEFAULT_MAX_ATTEMPTS, configBuilder.getMaxAttempts());
    assertEquals(FeignUtil.DEFAULT_MAX_PERIOD_IN_SECONDS, configBuilder.getMaxPeriod());
  }

  @ParameterizedTest
  @ValueSource(strings = {"feign.Logger$JavaLogger", "feign.slf4j.Slf4jLogger", "feign.Logger$NoOpLogger", "feign.Logger$ErrorLogger"})
  public void testConfigBuilderWithLoggerNames(String loggerName) {
    Map<String, String> configMap = new HashMap<>();
    configMap.put(FeignUtil.AIO_FEIGN_LOGGER_CLASS, loggerName);
    FeignUtil.ConfigBuilder configBuilder = new FeignUtil.ConfigBuilder().configMap(configMap);
    assertEquals(loggerName, configBuilder.getLoggerClass().getName());
    assertDoesNotThrow(configBuilder::build);
  }

  @ParameterizedTest
  @ValueSource(strings = {"NONE", "BASIC", "HEADERS", "FULL"})
  public void testConfigBuilderWithLogLevels(String logLevel) {
    Map<String, String> configMap = new HashMap<>();
    configMap.put(FeignUtil.AIO_FEIGN_LOG_LEVEL, logLevel);
    configMap.put(FeignUtil.AIO_FEIGN_RETRY_PERIOD, "1");
    configMap.put(FeignUtil.AIO_FEIGN_RETRY_MAX_ATTEMPTS, "2");
    configMap.put(FeignUtil.AIO_FEIGN_RETRY_MAX_PERIOD, "3");
    FeignUtil.ConfigBuilder configBuilder = new FeignUtil.ConfigBuilder().configMap(configMap);
    assertEquals(Level.valueOf(logLevel), configBuilder.getLogLevel());
    assertEquals(1, configBuilder.getRetryPeriod());
    assertEquals(2, configBuilder.getMaxAttempts());
    assertEquals(3, configBuilder.getMaxPeriod());
    assertDoesNotThrow(configBuilder::build);
  }

}
