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

  private static Workspace getTestWorkspaceFromProperties() throws IOException {
    return Workspace.builder()
        .propertiesPath(TEST_PROPERTIES)
        .build();
  }

  @Test
  public void testProperties() throws IOException {
    Workspace fromProperties = getTestWorkspaceFromProperties();
    Workspace expected = Workspace.builder()
        .imsUrl(Constants.IMS_URL)
        .apiKey(Workspace.API_KEY + TEST_VALUE)
        .clientSecret(Workspace.CLIENT_SECRET + TEST_VALUE)
        .apiKey(Workspace.API_KEY + TEST_VALUE)
        .credentialId(Workspace.CREDENTIAL_ID + TEST_VALUE)
        .consumerOrgId(Workspace.CONSUMER_ORG_ID + TEST_VALUE)
        .projectId(Workspace.PROJECT_ID + TEST_VALUE)
        .workspaceId(Workspace.WORKSPACE_ID + TEST_VALUE)
        .imsOrgId(Workspace.IMS_ORG_ID + TEST_VALUE)
        .technicalAccountId(Workspace.TECHNICAL_ACCOUNT_ID + TEST_VALUE)
        .addMetascope(Workspace.META_SCOPES + TEST_VALUE)
        .build();
    Assert.assertEquals(expected, fromProperties);
    Assert.assertEquals(expected.hashCode(), fromProperties.hashCode());
    Assert.assertEquals(expected.toString(), fromProperties.toString());
  }

  @Test
  public void testValidateJwtCredentialConfig() throws IOException {
    Workspace workspace = getTestWorkspaceFromProperties();
    expectedEx.expect(IllegalArgumentException.class);
    expectedEx.expectMessage("Your `Worskpace` should contain a privateKey");
    workspace.validateJwtCredentialConfig();
  }

}
