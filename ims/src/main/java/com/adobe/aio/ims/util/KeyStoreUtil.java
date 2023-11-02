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
package com.adobe.aio.ims.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

public class KeyStoreUtil {

  private static final String PKCS_12 = "PKCS12";
  private static final String RSA = "RSA";

  private KeyStoreUtil() {
  }

  public static PrivateKey getPrivateKeyFromPkcs12File(String filePath, String alias,
      String password)
      throws IOException, KeyStoreException, CertificateException, NoSuchAlgorithmException, UnrecoverableKeyException {
    KeyStore keystore = KeyStore.getInstance(PKCS_12);
    keystore.load(new FileInputStream(filePath), password.toCharArray());
    return (PrivateKey) keystore.getKey(alias, password.toCharArray());
  }

  public static PrivateKey getPrivateKeyFromEncodedPkcs8(String base64EncodedPkcs8)
      throws InvalidKeySpecException, NoSuchAlgorithmException {
    return getPrivateKeyFromPkcs8(Base64.getDecoder().decode(base64EncodedPkcs8));
  }

  public static PrivateKey getPrivateKeyFromPkcs8File(String filePath)
      throws IOException, InvalidKeySpecException, NoSuchAlgorithmException {
    return getPrivateKeyFromPkcs8(Files.readAllBytes(Paths.get(filePath)));
  }

  private static PrivateKey getPrivateKeyFromPkcs8(byte[] pkcs8)
      throws NoSuchAlgorithmException, InvalidKeySpecException {
    return KeyFactory.getInstance(RSA).generatePrivate(new PKCS8EncodedKeySpec(pkcs8));
  }

}
