package com.adobe.aio.feign;

import com.adobe.aio.workspace.Workspace;
import feign.RequestTemplate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.adobe.aio.util.Constants.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class AIOHeaderInterceptorTest {

  @Mock
  private Workspace workspace;

  private static final String imsOrgId = "IMS ORG Id";
  private static final String apiKey = "API KEY";

  @Test
  void apply() {
    RequestTemplate requestTemplate = new RequestTemplate();
    when(workspace.getApiKey()).thenReturn(apiKey);
    when(workspace.getImsOrgId()).thenReturn(imsOrgId);

    final AIOHeaderInterceptor interceptor = AIOHeaderInterceptor.builder().workspace(workspace).build();
    interceptor.apply(requestTemplate);

    assertEquals(apiKey, requestTemplate.headers().get(API_KEY_HEADER).stream().findFirst().get());
    assertEquals(imsOrgId, requestTemplate.headers().get(IMS_ORG_HEADER).stream().findFirst().get());
  }
}
