package com.adobe.aio.ims.feign;

import com.adobe.aio.auth.OAuthContext;
import com.adobe.aio.ims.ImsService;
import com.adobe.aio.ims.model.AccessToken;
import com.adobe.aio.workspace.Workspace;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OAuthAuthInterceptorTest {
  private static final String ACCESS_TOKEN = "ACCESS_TOKEN";

  @Mock
  private Workspace workspace;

  @Mock
  private OAuthContext authContext;

  @Mock
  private ImsService imsService;

  @Test
  void fetchAccessToken() {
    when(workspace.isAuthOAuth()).thenReturn(true);
    when(imsService.getOAuthAccessToken()).thenReturn(new AccessToken(ACCESS_TOKEN, 0));

    try (MockedConstruction<ImsService.Builder> ignored = mockConstruction(ImsService.Builder.class,
        (mock, mockContext) -> {
          when(mock.workspace(workspace)).thenReturn(mock);
          when(mock.build()).thenReturn(imsService);
        }
    )) {
      OAuthInterceptor interceptor = (OAuthInterceptor) AuthInterceptor.builder().workspace(workspace).build();
      assertNotNull(interceptor.fetchAccessToken());
    }
  }
}
