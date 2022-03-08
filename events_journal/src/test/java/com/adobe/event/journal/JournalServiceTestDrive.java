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
package com.adobe.event.journal;

import com.adobe.Workspace;
import com.adobe.ims.JWTAuthInterceptor;
import com.adobe.ims.util.PrivateKeyBuilder;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import feign.RequestInterceptor;
import java.security.PrivateKey;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.junit.Rule;
import org.junit.Test;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static junit.framework.TestCase.assertNotNull;

public class JournalServiceTestDrive {

  @Rule
  public WireMockRule wireMockRule = new WireMockRule(9996);

  @Test
  public void journalServiceTest(){

    assertNotNull(getJournalService());

//    /** get */
    stubFor(get(anyUrl())
            .withHeader("x-ims-org-id", equalTo("01AB82@AdobeOrg"))
            .willReturn(aResponse()
                    .withStatus(200)
            ));

    getJournalService().get("http://localhost:9996");
    verify(getRequestedFor(anyUrl()));

    //    /** getLatest */
    stubFor(get(urlEqualTo("/?latest=true"))
            .withHeader("x-ims-org-id", equalTo("01AB82@AdobeOrg"))
            .willReturn(aResponse()
                    .withStatus(200)
            ));

    getJournalService().getLatest();
    verify(getRequestedFor(urlEqualTo("/?latest=true")));

    //    /** getSince */
    stubFor(get(urlEqualTo("/?since=10"))
            .withHeader("x-ims-org-id", equalTo("01AB82@AdobeOrg"))
            .willReturn(aResponse()
                    .withStatus(200)
            ));

    getJournalService().getSince("10");
    verify(getRequestedFor(urlEqualTo("/?since=10")));

    //    /** getSince -> with batchSize */
    stubFor(get(urlEqualTo("/?since=10&limit=30"))
            .withHeader("x-ims-org-id", equalTo("01AB82@AdobeOrg"))
            .willReturn(aResponse()
                    .withStatus(200)
            ));

    getJournalService().getSince("10", 30);
    verify(getRequestedFor(urlEqualTo("/?since=10&limit=30")));

  }

  private JournalService getJournalService(){

    PrivateKey privateKey = new PrivateKeyBuilder().encodePkcs8Key("MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCazh+VvC9xqWi1VzZj6FtOZNhTwPo6nckpFyDu6B79eXvm/RA5wVha78mKg0jQFxWs6ZqwyrtCIeRZq62lWrEaiSFEblkWTNsVZ6fhaNtOh7JMPwUheZIa4/d3EAyxEkaymvkpLcjDN3js3Q5hnWaRFZao8lojRjaH+wNWGj1Dw8pzk5scmnltTcUCSnhzB7ZiHLsSIZ+Pv3xek8hSKRQjLenb2cCEjwm1KM/QbUWivRo2upJL6oJzc+31lqAe2aqVMEtLFqnUT4oCa3RMRPgAp1LIpHHhqs9/IJUXX5bJ8Em8EH7epjnEi5GZ9ZbMW7PLRgceUETggpBWp1qciXaVAgMBAAECggEARsJY5ZRzCz0oQ1tt3RTkR10JFJ9swUZGIKYVw54OLEZPQDIELKIXxNk+AjYoHhWvLq5Iqu6/0Wa0fdhfMunVcg+kSSc3SV4v9gS/U+Ud+TNFaFyV98sd4XS6NI39fyKfdhwoL45h6fl9KKeSX0QXEdIQX4EHeoAphNZTnBO6VTJ/YhN8/cxl7brykBIDNubk3eJ8bsQ4o6FCc8Hq1QIb//xAA9uZiAMCuZOdsTTpWkCKFEUQchyxpy+PbAR6qxjEUr5lQjTfLoq7eCTnZu4yKRRYiY/v3YyRJ5Rlgg7FjZCbBTunCKPnNolNY1sBQpcFRY9eUTCzwDuuzuPnEldpMQKBgQDJuOlmpTtqqCSr8Ae/FD5yHUFcMHngd2UrC6S+l4PYuFTCfXHTe6hPsuMBcy6yfqIDvHLgvCzs5ZwctKdnAO0zDFPhOu929mxO7h8P/fw+1lv2QU95FCs2KPnI8RB8uXfMhbBNxDs/cWRoXpVS3P93h/otlXO+z8zDg2bR9XGE1wKBgQDEdXP2uE7baIzPAQ+6fZFGsHjVhYZ089QhOBi4PrAphYJcqO3hCilTqJblQpff0ltiUuhzzIsQIxPjwCHKrHk9Skgx3D6RfdmS5/5SFmFk+j7Eops06zM+goIpG5KvlHgBaxkWgYY5KtikbG0Lc0WbbLifYj4TROnEZOJ6cohGcwKBgQC5dhaw1q1gDCNbKR4WIaigBiG3fqIvK9aJ0vSufmMr952GCwuB4qkGTXPEO3/tf9u5D6OW16t+SkRTaAyY+RMb4fOkmijb+QfvMaLBc0RdCXwXVkiZC2AHNXkhs/Dymxp3oVpMxWOrmrcz9fHX83O1FAGBs2xtPGQIRWFdHAo4lQKBgETGlX020qxu8nR5e6ce1F/54aNmZkbFIWsrt0Ow9nzit1t27CgPJZ6a85B4+rApdUJ7odMANWLF1O2zUmEgdiUlvxZtcx39/9A1FUxpd1khXh36ivlAqaTljWmUtIpxIH3mn1bIq1OSE1ukdZw/k4uwyQVLIE4gnvHZG4wgUmLPAoGBAJZQclY6S26h43H9L4cZXIBZeDwO2MRcGN55XlSXMSBkHMJYxpLaHEqlv5MTPPc+Cog/hG0jRVz7Yk4EaUAAdJ3CRrSV2uaGXZHEj5fGDZhB3gQ3FjQZYndz51fCwl9IhMz3NRqO0VfNqdBNrcSH0eZoHUA15kjy+IMwnfmNrdl4").build();
    Map<String, String> map = new HashMap<>();
    map.put(Workspace.API_KEY, "0914e5540cb34ce28c80e5b27c99a12a");
    map.put(Workspace.CLIENT_SECRET, "P9sdSkY1r5NJ5wPAYMssdeb7");
    map.put(Workspace.CONSUMER_ORG_ID, "2318");
    map.put(Workspace.CREDENTIAL_ID, "60771");
    map.put(Workspace.IMS_ORG_ID, "01AB82@AdobeOrg");
    map.put(Workspace.IMS_URL, "http://localhost:9996");
    map.put(Workspace.PROJECT_ID, "4566206088344551943");
    map.put(Workspace.TECHNICAL_ACCOUNT_ID, "ABC123321CBA@techacct.adobe.com");
    map.put(Workspace.WORKSPACE_ID, "4566206088344552874");
    String metaScopes = StringUtils.join("/s/event_receiver_api, /s/ent_adobeio_sdk",',');
    map.put(Workspace.META_SCOPES, metaScopes);

    Workspace workspace = Workspace.builder()
            .configMap(map)
            .privateKey(privateKey)
            .build();

    stubFor(post(urlEqualTo("/ims/exchange/jwt"))
            .willReturn(okJson("{\n" +
                    "  \"expires_in\": 86399761,\n" +
                    "  \"token_type\": \"bearer\",\n" +
                    "  \"access_token\": \"eyJhbGciOiJSUzI1NiIsIng1dSI6Imltcy1rZXktMS5jZXIifQ.eyJpZCI6IjE0NDIyNzk3NjUwNDEtMzRhOWMwZjktNmU1Yi00MmJmLWI2N2MtZTk5OWE2MzA1NDVlIiwibW9pIjoiZWQ4ZGYyNDUiLCJzY29wZSI6ImNyZWF0ZWl2ZV9jbG91ZCxBZG9iZUlkLHJlYWRfb3JnYW5pemF0aW9ucyxvcGVuaWQiLCJjIjoiRGpXZlB0d0lta0x1eTJPNWJ5OGFGZz09IiwiYXMiOiJpbXMtbmExLXFhMiIsImNyZWF0ZWRfYXQiOiIxNDQyMjc5NzY1MDQxIiwiZXhwaXJlc19pbiI6Ijg2NDAwMDAwIiwidXNlcl9pZCI6IjBEQ0U2MTNBNTVFRkJDRUQ3RjAwMDEwMUB0ZWNoYWNjdC5hZG9iZS5jb20iLCJjbGllbnRfaWQiOiJ0ZXN0LXRlY2giLCJ0eXBlIjoiYWNjZXNzX3Rva2VuIn0.KTPsnDJI4tPJ7zbwYMDBG-FUSqxTb4Jh7qTFZIGEARJlUtR9fv_sLxCCtuu8FzTumvANm7yMD2H3WEqDyU4JPIctPfzQFqpQdcygSL4UrEEAIEwVqZN_7oTTCWb3lBVCemVX3cv27HCrpEOZ_LDT7W4hchpnbRHxj32_rI-RLhacj9Um8qHvv7wyxfyYtsb81Vs9__kDUeDk0YN2irpYj2LNkCf44vW-z4m6F-nBN8ntTG94D530f9EslP1NYqkwebIgKfondz01Lxty2TLFrf0Kn6QDgrM1rHGh61vqeVToeVrZAsQW17fSz1yjCibN9xbGaFwMUwfBj5b1656Nvg\"\n" +
                    "}")
                    .withStatus(200)
            ));
    RequestInterceptor authInterceptor = JWTAuthInterceptor.builder()
            .workspace(workspace)
            .build();

    return JournalService.builder()
            .workspace(workspace)
            .authInterceptor(authInterceptor)
            .url(workspace.getImsUrl())
            .build();
  }


}
