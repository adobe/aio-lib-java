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
package com.adobe.aio.workspace;

import java.io.IOException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;

import com.adobe.aio.auth.Context;
import com.adobe.aio.auth.JwtContext;
import com.adobe.aio.util.Constants;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class WorkspaceTest {

  private static Workspace expected;
  private static final String TEST_PROPERTIES = "workspace.properties";
  private static final String TEST_VALUE = "_changeMe";
  private static PrivateKey privateKey;

  @BeforeAll
  public static void beforeClass() throws Exception {
    KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
    kpg.initialize(2048);
    KeyPair kp = kpg.generateKeyPair();
    privateKey = kp.getPrivate();
    expected = Workspace.builder().propertiesPath(TEST_PROPERTIES).privateKey(privateKey).build();
  }

  @Test
  public void properties() throws IOException {

    class MockContext implements Context {
      @Override
      public void validate() {

      }
    }

    Workspace actual = Workspace.builder()
        .imsUrl(Constants.IMS_URL)
        .imsOrgId(Workspace.IMS_ORG_ID + TEST_VALUE)
        .apiKey(Workspace.API_KEY + TEST_VALUE)
        .consumerOrgId(Workspace.CONSUMER_ORG_ID + TEST_VALUE)
        .projectId(Workspace.PROJECT_ID + TEST_VALUE)
        .workspaceId(Workspace.WORKSPACE_ID + TEST_VALUE)
        .authContext(new MockContext())
        .build();

    assertEquals(Workspace.IMS_ORG_ID + TEST_VALUE, actual.getImsOrgId());
    assertEquals(Workspace.API_KEY + TEST_VALUE, actual.getApiKey());
    assertEquals(Workspace.CONSUMER_ORG_ID + TEST_VALUE, actual.getConsumerOrgId());
    assertEquals(Workspace.PROJECT_ID + TEST_VALUE, actual.getProjectId());
    assertEquals(Workspace.WORKSPACE_ID + TEST_VALUE, actual.getWorkspaceId());
    assertEquals(Constants.IMS_URL, actual.getImsUrl());
    actual.validateAll();
  }

  @Test
  public void missingImsOrg() {
    Workspace actual = Workspace.builder().build();
    Exception ex = assertThrows(IllegalStateException.class, actual::validateWorkspaceContext);
    assertEquals("Your `Workspace` is missing an imsOrgId", ex.getMessage());
  }

  @Test
  public void missingConsumerOrgId() {
    Workspace actual = Workspace.builder()
        .imsOrgId(Workspace.IMS_ORG_ID + TEST_VALUE)
        .build();
    Exception ex = assertThrows(IllegalStateException.class, actual::validateWorkspaceContext);
    assertEquals("Your `Workspace` is missing a consumerOrgId", ex.getMessage());
  }

  @Test
  public void missingApiKey() {
    Workspace actual = Workspace.builder()
        .imsOrgId(Workspace.IMS_ORG_ID + TEST_VALUE)
        .consumerOrgId(Workspace.CONSUMER_ORG_ID + TEST_VALUE)
        .build();
    Exception ex = assertThrows(IllegalStateException.class, actual::validateWorkspaceContext);
    assertEquals("Your `Workspace` is missing an apiKey", ex.getMessage());
  }

  @Test
  public void missingProjectId() {
    Workspace actual = Workspace.builder()
        .imsOrgId(Workspace.IMS_ORG_ID + TEST_VALUE)
        .consumerOrgId(Workspace.CONSUMER_ORG_ID + TEST_VALUE)
        .apiKey(Workspace.API_KEY + TEST_VALUE)
        .build();
    Exception ex = assertThrows(IllegalStateException.class, actual::validateWorkspaceContext);
    assertEquals("Your `Workspace` is missing a projectId", ex.getMessage());
  }

  @Test
  public void missingWorkspaceId() {
    Workspace actual = Workspace.builder()
        .imsOrgId(Workspace.IMS_ORG_ID + TEST_VALUE)
        .consumerOrgId(Workspace.CONSUMER_ORG_ID + TEST_VALUE)
        .apiKey(Workspace.API_KEY + TEST_VALUE)
        .projectId(Workspace.PROJECT_ID + TEST_VALUE)
        .build();
    Exception ex = assertThrows(IllegalStateException.class, actual::validateWorkspaceContext);
    assertEquals("Your `Workspace` is missing a workspaceId", ex.getMessage());
  }
  
  @Test
  public void projectUrl() {
    Workspace actual = Workspace.builder()
        .consumerOrgId(Workspace.CONSUMER_ORG_ID + TEST_VALUE)
        .projectId(Workspace.PROJECT_ID + TEST_VALUE)
        .build();
    
    assertEquals("https://developer.adobe.com/console/projects/aio_consumer_org_id_changeMe/aio_project_id_changeMe/overview", actual.getProjectUrl());
  }

  @Test
  public void jwtBackwardsCompatible() throws Exception {
    Workspace actual = Workspace.builder()
        .imsUrl(Constants.IMS_URL)
        .imsOrgId(Workspace.IMS_ORG_ID + TEST_VALUE)
        .apiKey(Workspace.API_KEY + TEST_VALUE)
        .consumerOrgId(Workspace.CONSUMER_ORG_ID + TEST_VALUE)
        .projectId(Workspace.PROJECT_ID + TEST_VALUE)
        .workspaceId(Workspace.WORKSPACE_ID + TEST_VALUE)
        .clientSecret(JwtContext.CLIENT_SECRET + TEST_VALUE)
        .credentialId(JwtContext.CREDENTIAL_ID + TEST_VALUE)
        .technicalAccountId(JwtContext.TECHNICAL_ACCOUNT_ID + TEST_VALUE)
        .privateKey(privateKey)
        .addMetascope(JwtContext.META_SCOPES + TEST_VALUE)
        .build();
    assertEquals(actual, expected);
    assertEquals(actual.hashCode(), expected.hashCode());
    assertEquals(actual.toString(), expected.toString());
    actual.validateAll();
  }
}
