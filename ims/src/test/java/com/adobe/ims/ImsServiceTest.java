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
package com.adobe.ims;

import com.adobe.ims.model.AccessToken;
import com.adobe.ims.util.TestUtil;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static junit.framework.TestCase.assertNotNull;

public class ImsServiceTest {

  private static final Logger logger = LoggerFactory.getLogger(ImsServiceTest.class);

  @Rule
  public WireMockRule wireMockRule = new WireMockRule(Integer.parseInt(TestUtil.PORT));

  @Test
  public void imsServiceTest(){

    stubFor(post(urlEqualTo("/ims/exchange/jwt"))
            .willReturn(okJson(TestUtil.AUTH_RESPONSE)
                    .withStatus(200)
            ));
    AccessToken accessToken = ImsService.builder().workspace(TestUtil.getWorkspace()).build().getJwtExchangeAccessToken();
    verify(postRequestedFor(urlEqualTo("/ims/exchange/jwt")));
    assertNotNull(accessToken.getAccessToken());
    logger.info("accessToken: {}", accessToken.getAccessToken());

  }

}
