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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.adobe.aio.util.JacksonUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.node.TextNode;
import org.junit.Test;

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
    assertTrue(JacksonUtil.getJsonNode(aJsonTree).size() == 1);
    assertEquals(VALUE, JacksonUtil.getJsonNode(aJsonTree).get(KEY).textValue());
  }

  @Test(expected = JsonProcessingException.class)
  public void testGetJsonNode_ThrowsJsonProcessingException() throws Exception {
    JacksonUtil.getJsonNode(" { an invalid Json Payload ");
  }
}
