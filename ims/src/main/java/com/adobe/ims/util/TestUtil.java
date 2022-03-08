package com.adobe.ims.util;

import com.adobe.Workspace;
import org.apache.commons.lang3.StringUtils;
import java.security.PrivateKey;
import java.util.HashMap;
import java.util.Map;

public class TestUtil {

    public static final String CONTENT_TYPE_HEADER = "Content-Type";
    public static final String CONTENT_TYPE_HEADER_VALUE = "application/json";
    public static final String IMS_ORG_ID_HEADER = "x-ims-org-id";

    public static final String IMS_ORG_ID = "someImsOrgId";
    private static final String API_KEY = "someApiKey";
    private static final String CLIENT_SECRET = "someClientSecret";
    private static final String CONSUMER_ORG_ID = "someConsumerOrgId";
    private static final String CREDENTIAL_ID = "someCredentialId";
    private static final String IMS_URL = "http://localhost:9999";
    private static final String PROJECT_ID = "someProjectId";
    private static final String TECHNICAL_ACCCOUNT_ID = "someTechnicalAccountId";
    private static final String WORKSPACE_ID = "someWorkspaceId";
    private static final String PRIVATE_KEY = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCazh+VvC9xqWi1VzZj6FtOZNhTwPo6nckpFyDu6B79eXvm/RA5wVha78mKg0jQFxWs6ZqwyrtCIeRZq62lWrEaiSFEblkWTNsVZ6fhaNtOh7JMPwUheZIa4/d3EAyxEkaymvkpLcjDN3js3Q5hnWaRFZao8lojRjaH+wNWGj1Dw8pzk5scmnltTcUCSnhzB7ZiHLsSIZ+Pv3xek8hSKRQjLenb2cCEjwm1KM/QbUWivRo2upJL6oJzc+31lqAe2aqVMEtLFqnUT4oCa3RMRPgAp1LIpHHhqs9/IJUXX5bJ8Em8EH7epjnEi5GZ9ZbMW7PLRgceUETggpBWp1qciXaVAgMBAAECggEARsJY5ZRzCz0oQ1tt3RTkR10JFJ9swUZGIKYVw54OLEZPQDIELKIXxNk+AjYoHhWvLq5Iqu6/0Wa0fdhfMunVcg+kSSc3SV4v9gS/U+Ud+TNFaFyV98sd4XS6NI39fyKfdhwoL45h6fl9KKeSX0QXEdIQX4EHeoAphNZTnBO6VTJ/YhN8/cxl7brykBIDNubk3eJ8bsQ4o6FCc8Hq1QIb//xAA9uZiAMCuZOdsTTpWkCKFEUQchyxpy+PbAR6qxjEUr5lQjTfLoq7eCTnZu4yKRRYiY/v3YyRJ5Rlgg7FjZCbBTunCKPnNolNY1sBQpcFRY9eUTCzwDuuzuPnEldpMQKBgQDJuOlmpTtqqCSr8Ae/FD5yHUFcMHngd2UrC6S+l4PYuFTCfXHTe6hPsuMBcy6yfqIDvHLgvCzs5ZwctKdnAO0zDFPhOu929mxO7h8P/fw+1lv2QU95FCs2KPnI8RB8uXfMhbBNxDs/cWRoXpVS3P93h/otlXO+z8zDg2bR9XGE1wKBgQDEdXP2uE7baIzPAQ+6fZFGsHjVhYZ089QhOBi4PrAphYJcqO3hCilTqJblQpff0ltiUuhzzIsQIxPjwCHKrHk9Skgx3D6RfdmS5/5SFmFk+j7Eops06zM+goIpG5KvlHgBaxkWgYY5KtikbG0Lc0WbbLifYj4TROnEZOJ6cohGcwKBgQC5dhaw1q1gDCNbKR4WIaigBiG3fqIvK9aJ0vSufmMr952GCwuB4qkGTXPEO3/tf9u5D6OW16t+SkRTaAyY+RMb4fOkmijb+QfvMaLBc0RdCXwXVkiZC2AHNXkhs/Dymxp3oVpMxWOrmrcz9fHX83O1FAGBs2xtPGQIRWFdHAo4lQKBgETGlX020qxu8nR5e6ce1F/54aNmZkbFIWsrt0Ow9nzit1t27CgPJZ6a85B4+rApdUJ7odMANWLF1O2zUmEgdiUlvxZtcx39/9A1FUxpd1khXh36ivlAqaTljWmUtIpxIH3mn1bIq1OSE1ukdZw/k4uwyQVLIE4gnvHZG4wgUmLPAoGBAJZQclY6S26h43H9L4cZXIBZeDwO2MRcGN55XlSXMSBkHMJYxpLaHEqlv5MTPPc+Cog/hG0jRVz7Yk4EaUAAdJ3CRrSV2uaGXZHEj5fGDZhB3gQ3FjQZYndz51fCwl9IhMz3NRqO0VfNqdBNrcSH0eZoHUA15kjy+IMwnfmNrdl4";

    public static final String AUTH_RESPONSE = "{\n" +
            "  \"expires_in\": 86399761,\n" +
            "  \"token_type\": \"bearer\",\n" +
            "  \"access_token\": \"eyJhbGciOiJSUzI1NiIsIng1dSI6Imltcy1rZXktMS5jZXIifQ.eyJpZCI6IjE0NDIyNzk3NjUwNDEtMzRhOWMwZjktNmU1Yi00MmJmLWI2N2MtZTk5OWE2MzA1NDVlIiwibW9pIjoiZWQ4ZGYyNDUiLCJzY29wZSI6ImNyZWF0ZWl2ZV9jbG91ZCxBZG9iZUlkLHJlYWRfb3JnYW5pemF0aW9ucyxvcGVuaWQiLCJjIjoiRGpXZlB0d0lta0x1eTJPNWJ5OGFGZz09IiwiYXMiOiJpbXMtbmExLXFhMiIsImNyZWF0ZWRfYXQiOiIxNDQyMjc5NzY1MDQxIiwiZXhwaXJlc19pbiI6Ijg2NDAwMDAwIiwidXNlcl9pZCI6IjBEQ0U2MTNBNTVFRkJDRUQ3RjAwMDEwMUB0ZWNoYWNjdC5hZG9iZS5jb20iLCJjbGllbnRfaWQiOiJ0ZXN0LXRlY2giLCJ0eXBlIjoiYWNjZXNzX3Rva2VuIn0.KTPsnDJI4tPJ7zbwYMDBG-FUSqxTb4Jh7qTFZIGEARJlUtR9fv_sLxCCtuu8FzTumvANm7yMD2H3WEqDyU4JPIctPfzQFqpQdcygSL4UrEEAIEwVqZN_7oTTCWb3lBVCemVX3cv27HCrpEOZ_LDT7W4hchpnbRHxj32_rI-RLhacj9Um8qHvv7wyxfyYtsb81Vs9__kDUeDk0YN2irpYj2LNkCf44vW-z4m6F-nBN8ntTG94D530f9EslP1NYqkwebIgKfondz01Lxty2TLFrf0Kn6QDgrM1rHGh61vqeVToeVrZAsQW17fSz1yjCibN9xbGaFwMUwfBj5b1656Nvg\"\n" +
            "}";

    public static Workspace getWorkspace(){

        PrivateKey privateKey = new PrivateKeyBuilder().encodePkcs8Key(PRIVATE_KEY).build();
        Map<String, String> map = new HashMap<>();
        map.put(Workspace.API_KEY, API_KEY);
        map.put(Workspace.CLIENT_SECRET, CLIENT_SECRET);
        map.put(Workspace.CONSUMER_ORG_ID, CONSUMER_ORG_ID);
        map.put(Workspace.CREDENTIAL_ID, CREDENTIAL_ID);
        map.put(Workspace.IMS_ORG_ID, IMS_ORG_ID);
        map.put(Workspace.IMS_URL, IMS_URL);
        map.put(Workspace.PROJECT_ID, PROJECT_ID);
        map.put(Workspace.TECHNICAL_ACCOUNT_ID, TECHNICAL_ACCCOUNT_ID);
        map.put(Workspace.WORKSPACE_ID, WORKSPACE_ID);
        String metaScopes = StringUtils.join("/s/event_receiver_api, /s/ent_adobeio_sdk",',');
        map.put(Workspace.META_SCOPES, metaScopes);

        return Workspace.builder()
                .configMap(map)
                .privateKey(privateKey)
                .build();
    }

}
