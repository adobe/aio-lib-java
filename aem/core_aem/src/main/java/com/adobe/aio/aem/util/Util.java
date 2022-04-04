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
package com.adobe.aio.aem.util;

import com.adobe.aio.exception.AIOException;
import java.time.Instant;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Util {

  private Util(){}
  private static final long TIME_OUT_MILLI = 30000L;
  private final static Logger log = LoggerFactory.getLogger(Util.class);

  public static void waitFor(Supplier<Boolean> isReady, String supplierDescription) {
    long start = Instant.now().toEpochMilli();
    while (!isReady.get()){
      try {
        Thread.sleep(ThreadLocalRandom.current().nextInt( 50,500));
      } catch (InterruptedException e) {
        throw new AIOException("InterruptedException, while waiting for "+supplierDescription, e);
      }
      log.debug("waiting for {} to be ready ...", supplierDescription);
      if (Instant.now().toEpochMilli()-start> TIME_OUT_MILLI){
        throw new AIOException("Timeout error, while waiting for "+supplierDescription);
      }
    }
  }

}
