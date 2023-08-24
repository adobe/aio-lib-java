package com.adobe.aio.ims.feign;

import java.lang.reflect.Field;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.adobe.aio.auth.Context;
import com.adobe.aio.ims.ImsService;
import com.adobe.aio.ims.model.AccessToken;
import com.adobe.aio.workspace.Workspace;
import feign.RequestTemplate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.adobe.aio.util.Constants.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthInterceptorTest {

  private static final String ACCESS_TOKEN = "ACCESS_TOKEN";

  @Mock
  private RequestTemplate template;

  @Mock
  private Context authContext;

  @Mock
  private Workspace workspace;

  @Mock
  private ImsService imsService;

  @Test
  void invalidContext() {
    when(workspace.getAuthContext()).thenReturn(authContext);

    try (MockedConstruction<ImsService.Builder> ignored = mockConstruction(ImsService.Builder.class,
        (mock, mockContext) -> {
          when(mock.workspace(workspace)).thenReturn(mock);
          when(mock.build()).thenReturn(imsService);
        }
    )) {
      assertThrows(IllegalStateException.class, () -> AuthInterceptor.builder().workspace(workspace).build());
    }
  }

  @Test
  void applyAlreadySet() throws Exception {
    Calendar expires = Calendar.getInstance();
    expires.add(Calendar.HOUR, 1);
    AccessToken token = new AccessToken(ACCESS_TOKEN, 3600000);

    Map<String, Collection<String>> headers = new HashMap<>();
    headers.put(AUTHORIZATION_HEADER, Collections.EMPTY_LIST);

    when(template.headers()).thenReturn(headers);

    AuthInterceptor interceptor = mock(AuthInterceptor.class,
        withSettings().useConstructor(imsService).defaultAnswer(CALLS_REAL_METHODS));
    Field expiresField = AuthInterceptor.class.getDeclaredField("expirationTimeMillis");
    expiresField.setAccessible(true);
    expiresField.set(interceptor, expires.getTimeInMillis());

    Field tokenField = AuthInterceptor.class.getDeclaredField("accessToken");
    tokenField.setAccessible(true);
    tokenField.set(interceptor, token);
    interceptor.apply(template);

  }

  @Test
  void apply() throws Exception {
    Calendar expires = Calendar.getInstance();
    expires.add(Calendar.HOUR, 1);
    AccessToken token = new AccessToken(ACCESS_TOKEN, 3600000);

    when(template.headers()).thenReturn(Collections.emptyMap());

    AuthInterceptor interceptor = mock(AuthInterceptor.class,
        withSettings().useConstructor(imsService).defaultAnswer(CALLS_REAL_METHODS));
    Field expiresField = AuthInterceptor.class.getDeclaredField("expirationTimeMillis");
    expiresField.setAccessible(true);
    expiresField.set(interceptor, expires.getTimeInMillis());

    Field tokenField = AuthInterceptor.class.getDeclaredField("accessToken");
    tokenField.setAccessible(true);
    tokenField.set(interceptor, token);
    interceptor.apply(template);

    verify(template).header(AUTHORIZATION_HEADER, BEARER_PREFIX + ACCESS_TOKEN);
  }

  @Test
  void getAccessTokenNotSet() {
    Calendar expires = Calendar.getInstance();
    expires.add(Calendar.HOUR, 1);
    AccessToken token = new AccessToken(ACCESS_TOKEN, 3600000);

    AuthInterceptor interceptor = mock(AuthInterceptor.class,
        withSettings().useConstructor(imsService).defaultAnswer(CALLS_REAL_METHODS));
    doReturn(token).when(interceptor).fetchAccessToken();
    assertEquals(ACCESS_TOKEN, interceptor.getAccessToken());
  }

  @Test
  void getAccessTokenExpired() throws Exception {
    Calendar expires = Calendar.getInstance();
    expires.add(Calendar.HOUR, 1);
    AccessToken token = new AccessToken(ACCESS_TOKEN, 3600000);

    AuthInterceptor interceptor = mock(AuthInterceptor.class,
        withSettings().useConstructor(imsService).defaultAnswer(CALLS_REAL_METHODS));
    Field expiresField = AuthInterceptor.class.getDeclaredField("expirationTimeMillis");
    expiresField.setAccessible(true);
    expiresField.set(interceptor, 1L);

    doReturn(token).when(interceptor).fetchAccessToken();
    assertEquals(ACCESS_TOKEN, interceptor.getAccessToken());
  }
}
