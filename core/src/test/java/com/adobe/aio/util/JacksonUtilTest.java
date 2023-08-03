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
package com.adobe.aio.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.node.TextNode;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class JacksonUtilTest {

  private static final String KEY = "key";
  private static final String VALUE = "value";

  @Test
  public void testGetJsonNode() throws Exception {
    assertTrue(JacksonUtil.getJsonNode("  ").isEmpty());
    assertTrue(JacksonUtil.getJsonNode("").isEmpty());
    assertTrue(JacksonUtil.getJsonNode(null).isEmpty());
    String simpleString = "a simple string";
    assertEquals(new TextNode(simpleString), JacksonUtil.getJsonNode(simpleString));
    String aWeirdString = "a { weird string";
    assertEquals(new TextNode(aWeirdString), JacksonUtil.getJsonNode(aWeirdString));
    String aJsonTree = "  { \"" + KEY + "\" : \"" + VALUE + "\" } ";
    assertTrue(JacksonUtil.getJsonNode(aJsonTree).has(KEY));
    assertEquals(1, JacksonUtil.getJsonNode(aJsonTree).size());
    assertEquals(VALUE, JacksonUtil.getJsonNode(aJsonTree).get(KEY).textValue());
  }

  @Test
  public void testGetJsonNode_ThrowsJsonProcessingException() throws Exception {
    assertThrows(JsonProcessingException.class, () -> JacksonUtil.getJsonNode(" { an invalid Json Payload "));
  }
}
