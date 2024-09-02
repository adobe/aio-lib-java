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

import com.adobe.aio.auth.JwtContext;
import com.adobe.aio.auth.OAuthContext;
import com.adobe.aio.workspace.Workspace;
import org.junit.jupiter.api.Test;

import static com.adobe.aio.auth.Context.CLIENT_SECRET;
import static com.adobe.aio.auth.JwtContext.*;
import static com.adobe.aio.auth.OAuthContext.SCOPES;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class WorkspaceUtilTest {

    private static final String TEST_JWT_WORKSPACE_PROPERTIES = "workspace.jwt.properties";
    private static final String TEST_OAUTH_WORKSPACE_PROPERTIES = "workspace.oauth.properties";
    private static final String TEST_VALUE = "_changeMe";

    @Test
    public void getWorkspaceBuilderFromJwtProperties() {
        Workspace workspaceFromProperties = WorkspaceUtil.getWorkspaceBuilder(FileUtil.getMap(TEST_JWT_WORKSPACE_PROPERTIES)).build();
        JwtContext expectedAuthContext = JwtContext.builder()
                .clientSecret(CLIENT_SECRET + TEST_VALUE)
                .technicalAccountId(TECHNICAL_ACCOUNT_ID + TEST_VALUE)
                .addMetascope(META_SCOPES + TEST_VALUE)
                .build();
        Workspace expected = Workspace.builder()
                .imsUrl(Constants.PROD_IMS_URL)
                .imsOrgId(Workspace.IMS_ORG_ID + TEST_VALUE)
                .apiKey(Workspace.API_KEY + TEST_VALUE)
                .consumerOrgId(Workspace.CONSUMER_ORG_ID + TEST_VALUE)
                .projectId(Workspace.PROJECT_ID + TEST_VALUE)
                .workspaceId(Workspace.WORKSPACE_ID + TEST_VALUE)
                .credentialId(Workspace.CREDENTIAL_ID + TEST_VALUE)
                .authContext(expectedAuthContext)
                .build();

        assertEquals(expected, workspaceFromProperties);
        assertEquals(expected.hashCode(), workspaceFromProperties.hashCode());
        assertEquals(expected.toString(), workspaceFromProperties.toString());
        assertEquals(expectedAuthContext, workspaceFromProperties.getAuthContext());
        assertTrue(workspaceFromProperties.isAuthJWT());
        workspaceFromProperties.validateWorkspaceContext();
    }

    @Test
    public void getWorkspaceBuilderFromOAuthProperties() {
        Workspace workspaceFromProperties = WorkspaceUtil.getWorkspaceBuilder(FileUtil.getMap(TEST_OAUTH_WORKSPACE_PROPERTIES)).build();
        OAuthContext expectedAuthContext = new OAuthContext.Builder().clientSecret(CLIENT_SECRET + TEST_VALUE).addScope(SCOPES + TEST_VALUE).build();
        Workspace expected = Workspace.builder()
                .imsUrl(Constants.PROD_IMS_URL)
                .imsOrgId(Workspace.IMS_ORG_ID + TEST_VALUE)
                .apiKey(Workspace.API_KEY + TEST_VALUE)
                .consumerOrgId(Workspace.CONSUMER_ORG_ID + TEST_VALUE)
                .projectId(Workspace.PROJECT_ID + TEST_VALUE)
                .workspaceId(Workspace.WORKSPACE_ID + TEST_VALUE)
                .authContext(expectedAuthContext)
                .build();
        assertEquals(expected, workspaceFromProperties);
        assertEquals(expected.hashCode(), workspaceFromProperties.hashCode());
        assertEquals(expected.toString(), workspaceFromProperties.toString());
        assertEquals(expectedAuthContext, workspaceFromProperties.getAuthContext());
        assertTrue(workspaceFromProperties.isAuthOAuth());
        workspaceFromProperties.validateAll();
    }

}
