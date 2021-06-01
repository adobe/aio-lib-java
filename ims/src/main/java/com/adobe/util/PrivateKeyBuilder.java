package com.adobe.util;

import static com.adobe.util.FileUtil.getMapFromProperties;
import static com.adobe.util.FileUtil.readPropertiesFromClassPath;
import static com.adobe.util.FileUtil.readPropertiesFromFile;

import java.io.IOException;
import java.security.PrivateKey;
import java.util.Map;
import java.util.Properties;
import org.apache.commons.lang3.StringUtils;

public class PrivateKeyBuilder {

  private static final String ENCODE_PKCS8_KEY = "encoded_pkcs8";
  private static final String PKCS8_FILE_PATH = "pkcs8_file_path";

  private static final String PKCS12_FILE_PATH = "pkcs12_file_path";
  private static final String PKCS12_PASSWORD = "pkcs12_password";
  private static final String PKCS12_ALIAS = "pkcs12_alias";

  private Map<String, String> configMap;
  private String encodePkcs8Key;

  public PrivateKeyBuilder(){
  }

  public PrivateKeyBuilder systemEnv(){
    this.configMap = System.getenv();
    return this;
  }

  /**
   * will first look on the file system,
   * if not found, in the classpath
   */
  public PrivateKeyBuilder configPath(String configPath) throws IOException {
    this.configMap = getMapFromProperties(
         readPropertiesFromFile(configPath)
        .orElse(readPropertiesFromClassPath(configPath)));
    return this;
  }

  public PrivateKeyBuilder properties(Properties properties)  {
    this.configMap = getMapFromProperties(properties);
    return this;
  }

  public PrivateKeyBuilder encodePkcs8Key(String encodePkcs8Key)  {
    this.encodePkcs8Key = encodePkcs8Key;
    return this;
  }

  public PrivateKey build(){
    if (!StringUtils.isEmpty(encodePkcs8Key)){
      try {
        return KeyStoreUtil.getPrivateKeyFromEncodedPkcs8(encodePkcs8Key);
      }
      catch (Exception e) {
          throw new RuntimeException(
              "Invalid encoded pkcs8 Private Key configuration. "
                  + "" + e.getMessage(), e);
        }
    } else {
      return getPrivateKey(this.configMap);
    }
  }

  private static PrivateKey getPrivateKey(final Map<String, String> imsConfig) {
    try {
      if (imsConfig.containsKey(ENCODE_PKCS8_KEY)) {
        return KeyStoreUtil.getPrivateKeyFromEncodedPkcs8(imsConfig.get(ENCODE_PKCS8_KEY));
      } else if (imsConfig.containsKey(PKCS8_FILE_PATH)) {
        return KeyStoreUtil.getPrivateKeyFromPkcs8File(imsConfig.get(PKCS8_FILE_PATH));
      } else if (imsConfig.containsKey(PKCS12_FILE_PATH) && imsConfig.containsKey(PKCS12_PASSWORD)
          && imsConfig.containsKey(PKCS12_ALIAS)) {
        return KeyStoreUtil.getPrivateKeyFromPkcs12File(
            imsConfig.get(PKCS12_FILE_PATH), imsConfig.get(PKCS12_ALIAS),
            imsConfig.get(PKCS12_PASSWORD));
      } else {
        throw new IllegalArgumentException(
            "PrivateKeyBuilder is missing a valid (pkcs8 or pkcs12) Private Key configuration");
      }
    } catch (Exception e) {
      throw new RuntimeException(
          "PrivateKeyBuilder holds an invalid (pkcs8 or pkcs12) Private Key configuration. "
              + "" + e.getMessage(), e);
    }
  }



}
