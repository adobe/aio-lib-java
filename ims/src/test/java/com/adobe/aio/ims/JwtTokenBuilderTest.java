package com.adobe.aio.ims;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Map;

import com.adobe.aio.auth.Context;
import com.adobe.aio.auth.JwtContext;
import com.adobe.aio.util.FileUtil;
import com.adobe.aio.util.WorkspaceUtil;
import com.adobe.aio.workspace.Workspace;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class JwtTokenBuilderTest {

  @Test
  void wrongContext() {
    class MockContext implements Context {}
    Workspace workspace = Workspace.builder().authContext(new MockContext()).build();
    Exception ex = assertThrows(IllegalStateException.class, () -> new JwtTokenBuilder(workspace));
    assertEquals("AuthContext in workspace not of type `JwtContext`.", ex.getMessage());
  }

  @Test
  void invalidJwtContext() {
    Workspace workspace = Workspace.builder().authContext(JwtContext.builder().build()).build();
    assertThrows(IllegalStateException.class, () -> new JwtTokenBuilder(workspace));
  }

  @Test
  void build() throws Exception{
    KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
    kpg.initialize(2048);
    KeyPair kp = kpg.generateKeyPair();
    PrivateKey privateKey = kp.getPrivate();
    PublicKey publicKey = kp.getPublic();

    Map<String,String> testConfigs = FileUtil.getMap("workspace.properties");
    JwtContext authContext = WorkspaceUtil.getJwtContextBuilder(testConfigs).privateKey(privateKey).build();
    Workspace.Builder builder = WorkspaceUtil.getWorkspaceBuilder(testConfigs);
    Workspace workspace =  builder.authContext(authContext).build();

    String actual = new JwtTokenBuilder(workspace).build();

    Jwt<?, Claims> jwt = Jwts.parserBuilder().setSigningKey(publicKey).build().parseClaimsJws(actual);

    JwtContext jwtContext = (JwtContext) workspace.getAuthContext();
    Claims claims = jwt.getBody();
    assertEquals(workspace.getImsOrgId(), claims.getIssuer(), "Issuer specified.");
    assertEquals(jwtContext.getTechnicalAccountId(), claims.getSubject(), "Subject specified.");

    String audience = workspace.getImsUrl() + "/c/" + workspace.getApiKey();
    assertEquals(audience, claims.getAudience(), "Subject specified.");

    // Scopes
    for (String scope : jwtContext.getMetascopes()) {
      assertTrue((Boolean) claims.get(workspace.getImsUrl() + scope));
    }

    assertNotNull(claims.getIssuedAt(), "Issued At Time specified.");
    assertNotNull(claims.getExpiration(), "Expires Time specified.");
  }
}
