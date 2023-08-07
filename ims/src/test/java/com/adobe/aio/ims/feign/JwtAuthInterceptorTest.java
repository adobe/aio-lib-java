package com.adobe.aio.ims.feign;

import java.lang.reflect.Field;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.adobe.aio.ims.ImsService;
import com.adobe.aio.ims.model.AccessToken;
import feign.RequestTemplate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.verification.VerificationMode;

import static com.adobe.aio.util.Constants.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class JwtAuthInterceptorTest {

  private static final String ACCESS_TOKEN = "ACCESS_TOKEN";
  
  @Mock
  private ImsService imsService;

  @Mock
  private RequestTemplate template;

  @Test
  void isUpExpirationNull() throws Exception {
    AccessToken token = new AccessToken(ACCESS_TOKEN, new Date().getTime());

    when(imsService.getJwtExchangeAccessToken()).thenReturn(token);
    when(imsService.validateAccessToken(ACCESS_TOKEN)).thenReturn(true);

    try (MockedConstruction<ImsService.Builder> ignored = mockConstruction(ImsService.Builder.class,
        (mock, mockContext) -> {
          when(mock.workspace(null)).thenReturn(mock);
          when(mock.build()).thenReturn(imsService);
        }
    )) {
      JWTAuthInterceptor interceptor = JWTAuthInterceptor.builder().workspace(null).build();
      assertTrue(interceptor.isUp());
    }
  }

  @Test
  void isUpTokenExpired() throws Exception {
    AccessToken token = new AccessToken(ACCESS_TOKEN, '1');

    when(imsService.getJwtExchangeAccessToken()).thenReturn(token);
    when(imsService.validateAccessToken(ACCESS_TOKEN)).thenReturn(true);

    try (MockedConstruction<ImsService.Builder> ignored = mockConstruction(ImsService.Builder.class,
        (mock, mockContext) -> {
          when(mock.workspace(null)).thenReturn(mock);
          when(mock.build()).thenReturn(imsService);
        }
    )) {
      JWTAuthInterceptor interceptor = JWTAuthInterceptor.builder().workspace(null).build();
      Field expiresField = interceptor.getClass().getDeclaredField("expirationTimeMillis");
      expiresField.setAccessible(true);
      expiresField.set(interceptor, 1L);
      assertTrue(interceptor.isUp());
    }
  }


  @Test
  void isUpTokenValid() throws Exception {
    Calendar expires = Calendar.getInstance();
    expires.add(Calendar.HOUR, 1);
    AccessToken token = new AccessToken(ACCESS_TOKEN, expires.getTimeInMillis());

    when(imsService.validateAccessToken(ACCESS_TOKEN)).thenReturn(true);

    try (MockedConstruction<ImsService.Builder> ignored = mockConstruction(ImsService.Builder.class,
        (mock, mockContext) -> {
          when(mock.workspace(null)).thenReturn(mock);
          when(mock.build()).thenReturn(imsService);
        }
    )) {
      JWTAuthInterceptor interceptor = JWTAuthInterceptor.builder().workspace(null).build();
      Field expiresField = interceptor.getClass().getDeclaredField("expirationTimeMillis");
      expiresField.setAccessible(true);
      expiresField.set(interceptor, expires.getTimeInMillis());

      Field tokenField = interceptor.getClass().getDeclaredField("accessToken");
      tokenField.setAccessible(true);
      tokenField.set(interceptor, token);

      assertTrue(interceptor.isUp());
    }
  }

  @Test
  void applyAlreadySet() throws Exception {
    Calendar expires = Calendar.getInstance();
    expires.add(Calendar.HOUR, 1);
    AccessToken token = new AccessToken(ACCESS_TOKEN, expires.getTimeInMillis());

    Map<String, Collection<String>> headers = new HashMap<>();
    headers.put(AUTHORIZATION_HEADER, Collections.EMPTY_LIST);

    when(template.headers()).thenReturn(headers);

    try (MockedConstruction<ImsService.Builder> ignored = mockConstruction(ImsService.Builder.class,
        (mock, mockContext) -> {
          when(mock.workspace(null)).thenReturn(mock);
          when(mock.build()).thenReturn(imsService);
        }
    )) {
      JWTAuthInterceptor interceptor = JWTAuthInterceptor.builder().workspace(null).build();
      Field expiresField = interceptor.getClass().getDeclaredField("expirationTimeMillis");
      expiresField.setAccessible(true);
      expiresField.set(interceptor, expires.getTimeInMillis());

      Field tokenField = interceptor.getClass().getDeclaredField("accessToken");
      tokenField.setAccessible(true);
      tokenField.set(interceptor, token);
      interceptor.apply(template);
    }
  }

  @Test
  void apply() throws Exception {
    Calendar expires = Calendar.getInstance();
    expires.add(Calendar.HOUR, 1);
    AccessToken token = new AccessToken(ACCESS_TOKEN, expires.getTimeInMillis());

    when(template.headers()).thenReturn(Collections.emptyMap());

    try (MockedConstruction<ImsService.Builder> ignored = mockConstruction(ImsService.Builder.class,
        (mock, mockContext) -> {
          when(mock.workspace(null)).thenReturn(mock);
          when(mock.build()).thenReturn(imsService);
        }
    )) {
      JWTAuthInterceptor interceptor = JWTAuthInterceptor.builder().workspace(null).build();
      Field expiresField = interceptor.getClass().getDeclaredField("expirationTimeMillis");
      expiresField.setAccessible(true);
      expiresField.set(interceptor, expires.getTimeInMillis());

      Field tokenField = interceptor.getClass().getDeclaredField("accessToken");
      tokenField.setAccessible(true);
      tokenField.set(interceptor, token);
      interceptor.apply(template);

      verify(template).header(AUTHORIZATION_HEADER, BEARER_PREFIX + ACCESS_TOKEN);
    }
  }
}
