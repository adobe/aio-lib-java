package com.adobe.aio.ims.feign;

import java.lang.reflect.Field;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.adobe.aio.auth.JwtContext;
import com.adobe.aio.ims.ImsService;
import com.adobe.aio.ims.model.AccessToken;
import com.adobe.aio.workspace.Workspace;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.junit.jupiter.MockitoExtension;
import shaded_package.org.checkerframework.checker.units.qual.A;

import static com.adobe.aio.util.Constants.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class JwtAuthInterceptorTest {
  private static final String ACCESS_TOKEN = "ACCESS_TOKEN";

  @Mock
  private Workspace workspace;

  @Mock
  private JwtContext authContext;

  @Mock
  private ImsService imsService;

  @Test
  void isUp() {
    when(workspace.isAuthJWT()).thenReturn(true);
    when(imsService.validateAccessToken(ACCESS_TOKEN)).thenReturn(true);

    try (MockedConstruction<ImsService.Builder> ignored = mockConstruction(ImsService.Builder.class,
        (mock, mockContext) -> {
          when(mock.workspace(workspace)).thenReturn(mock);
          when(mock.build()).thenReturn(imsService);
        }
    )) {
      JWTAuthInterceptor spy = spy((JWTAuthInterceptor) AuthInterceptor.builder().workspace(workspace).build());
      doReturn(ACCESS_TOKEN).when(spy).getAccessToken();
      assertTrue(spy.isUp());
    }
  }

  @Test
  void fetchAccessToken() {
    when(workspace.isAuthJWT()).thenReturn(true);
    when(imsService.getJwtExchangeAccessToken()).thenReturn(new AccessToken(ACCESS_TOKEN, 0));

    try (MockedConstruction<ImsService.Builder> ignored = mockConstruction(ImsService.Builder.class,
        (mock, mockContext) -> {
          when(mock.workspace(workspace)).thenReturn(mock);
          when(mock.build()).thenReturn(imsService);
        }
    )) {
      JWTAuthInterceptor interceptor = (JWTAuthInterceptor) AuthInterceptor.builder().workspace(workspace).build();
      assertNotNull(interceptor.fetchAccessToken());
    }
  }
}
