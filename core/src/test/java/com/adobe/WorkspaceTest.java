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
package com.adobe;

import com.adobe.util.Constants;
import com.adobe.util.FileUtil;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class WorkspaceTest {

  @Rule
  public ExpectedException expectedEx = ExpectedException.none();

  private static final String TEST_PROPERTIES = "workspace.properties";
  private static final String TEST_VALUE = "_changeMe";

  private static Workspace properties() throws IOException {
    return Workspace.builder()
        .properties(FileUtil.readPropertiesFromClassPath(TEST_PROPERTIES))
        .build();
  }

  @Test
  public void testProperties() throws IOException {
    Workspace workspace = properties();
    Assert.assertEquals(Constants.IMS_URL, workspace.getImsUrl());
    Assert.assertEquals(Workspace.API_KEY+TEST_VALUE, workspace.getApiKey());
    Assert.assertEquals(Workspace.CLIENT_SECRET+TEST_VALUE, workspace.getClientSecret());
    Assert.assertEquals(Workspace.API_KEY+TEST_VALUE, workspace.getApiKey());
    Assert.assertEquals(Workspace.CREDENTIAL_ID+TEST_VALUE, workspace.getCredentialId());
    Assert.assertEquals(Workspace.CONSUMER_ORG_ID+TEST_VALUE, workspace.getConsumerOrgId());
    Assert.assertEquals(Workspace.IMS_ORG_ID+TEST_VALUE, workspace.getImsOrgId());
    Assert.assertEquals(Workspace.TECHNICAL_ACCOUNT_ID+TEST_VALUE, workspace.getTechnicalAccountId());
    Assert.assertEquals(Workspace.META_SCOPES+TEST_VALUE, workspace.getMetascopes().iterator().next());
    Assert.assertEquals(null, workspace.getPrivateKey());
  }

  @Test
  public void testValidateJwtCredentialConfig() throws IOException {
    Workspace workspace = properties();
    expectedEx.expect(IllegalArgumentException.class);
    expectedEx.expectMessage("Your `Worskpace` should contain a privateKey");
    workspace.validateJwtCredentialConfig();

  }

}
