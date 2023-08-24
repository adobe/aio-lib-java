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
package com.adobe.aio.ims;

import com.adobe.aio.auth.Context;
import com.adobe.aio.auth.JwtContext;
import com.adobe.aio.workspace.Workspace;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.security.PrivateKey;
import java.util.HashMap;
import java.util.Map;

/**
 * FYI, the JWT token will generate for you will contain the following claims:
 *
 * `exp` - the expiration time. IMS allows a time skew of 30 seconds between the time specified and the IMS server time.
 * `iss` - the issuer. It must be present, and it must be in the format: `org_ident@AdobeOrg` It represents the identity of the organization which issued the token, and it must be for an organization that has provided IMS with a valid certificate. 
 * `sub` - the subject. It must be present and must be in the format: `user_ident@user_auth_src`. It represents the ident and authsrc of the technical account for which a certificate has been uploaded to IMS
 * `aud` - the audience of the token. Must be only one entry, in the format: `ENDPOINT_URI/c/client_id`, where the client_id is the client id for which the access token will be issued. The `ENDPOINT_URI` must be a valid IMS endpoint (e.g. `https://ims-na1.adobelogin.com` for IMS production)
 * `one or more metascopes claims`, in the format: `ENDPOINT_URI/s/SCOPE_CODE: true`, where the ENDPOINT_URI has the same meaning as for the audience, and the SCOPE_CODE is a valid meta-scope code that was granted to you when the certificate binding was created.
 *
 * Note that Optionally, the JWT can contain the following claims (not implemented here yet)
 * `jti` - a unique identifier for the token. It is dependent on the setting being configured when the certificate binding was created, and if it is set as required it must have not been previously seen by the service, or the request will be reject
 *
 * It will also help you getting this signed with a `RSASSA-PKCS1-V1_5` Digital Signatures with `SHA-2` and a `RS256` The JWT algorithm/`alg` header value.
 * For this, it leverages a third-party open source library : [jjwt](https://github.com/jwtk/jjwt)
 *
 * @deprecated See <a href="https://developer.adobe.com/developer-console/docs/guides/authentication/JWT/">Developer Console documentation</a>
 */
@Deprecated
public class JwtTokenBuilder {

  private final Map<String, Object> claims;
  private final PrivateKey privateKey;

  private static final String ISS = "iss";
  private static final String EXP = "exp";
  private static final String SUB = "sub";
  private static final String AUD = "aud";
  private static final String IAT = "iat";

  private static final String AUD_SUFFIX = "/c/";

  public JwtTokenBuilder(final Workspace workspace) {
    if (!(workspace.getAuthContext() instanceof JwtContext)) {
      throw new IllegalStateException("AuthContext in workspace not of type `JwtContext`.");
    }

    JwtContext context = (JwtContext) workspace.getAuthContext();
    context.validate();
    this.claims = getClaims(workspace, context);
    this.privateKey = context.getPrivateKey();
  }

  private static Map<String, Object> getClaims(final Workspace workspace, JwtContext context) {
    Map<String, Object> claims = new HashMap<String, Object>();
    claims.put(ISS, workspace.getImsOrgId());
    claims.put(SUB, context.getTechnicalAccountId());
    claims.put(AUD, workspace.getImsUrl() + AUD_SUFFIX + workspace.getApiKey());

    for (String metascope : context.getMetascopes()) {
      claims.put(workspace.getImsUrl() + metascope, true);
    }

    long iat = System.currentTimeMillis() / 1000L;
    claims.put(IAT, iat);
    claims.put(EXP, iat + 180L);
    return claims;
  }

  public String build() {
    return Jwts.builder().setClaims(claims).signWith(SignatureAlgorithm.RS256, privateKey).compact();
  }

}
