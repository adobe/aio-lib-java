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
package com.adobe.xdm.event;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

public class JsonUtils {

  private static final ObjectMapper JSON_MAPPER = new ObjectMapper();
  private static final JsonFactory JSON_FACTORY = new JsonFactory(JSON_MAPPER);

  public static String toPrettyString(Object jsonObject) throws IOException {
    final StringWriter sw = new StringWriter();
    writePrettyPrint(sw, jsonObject);
    return sw.toString();
  }

  public static void writePrettyPrint(Writer writer, Object jsonObject) throws IOException {
    final JsonGenerator jw = JSON_FACTORY.createGenerator(writer);
    jw.useDefaultPrettyPrinter();
    jw.writeObject(jsonObject);
  }


}
