package com.adobe.aio.ims.feign;

import com.adobe.aio.ims.ImsService;
import com.adobe.aio.ims.model.AccessToken;
import com.adobe.aio.workspace.Workspace;
import feign.RequestTemplate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.*;

import static com.adobe.aio.util.Constants.AUTHORIZATION_HEADER;
import static com.adobe.aio.util.Constants.BEARER_PREFIX;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthInterceptorTest {

    private static final String ACCESS_TOKEN = "ACCESS_TOKEN";

    @Mock
    private RequestTemplate template;

    @Mock
    private Workspace workspace;

    @Mock
    private ImsService imsService;


    @Test
    void applyAlreadySet() throws Exception {
        Calendar expires = Calendar.getInstance();
        expires.add(Calendar.HOUR, 1);
        AccessToken token = new AccessToken(ACCESS_TOKEN, 3600000);

        Map<String, Collection<String>> headers = new HashMap<>();
        headers.put(AUTHORIZATION_HEADER, Collections.EMPTY_LIST);

        when(template.headers()).thenReturn(headers);

        try (MockedConstruction<ImsService.Builder> ignored = mockConstruction(ImsService.Builder.class,
                (mock, mockContext) -> {
                    when(mock.workspace(workspace)).thenReturn(mock);
                    when(mock.build()).thenReturn(imsService);
                }
        )) {
            AuthInterceptor interceptor = AuthInterceptor.builder().workspace(workspace).build();
            Field expiresField = AuthInterceptor.class.getDeclaredField("expirationTimeMillis");
            expiresField.setAccessible(true);
            expiresField.set(interceptor, expires.getTimeInMillis());

            Field tokenField = AuthInterceptor.class.getDeclaredField("accessToken");
            tokenField.setAccessible(true);
            tokenField.set(interceptor, token);
            interceptor.apply(template);
        }

    }

    @Test
    void apply() throws Exception {
        Calendar expires = Calendar.getInstance();
        expires.add(Calendar.HOUR, 1);
        AccessToken token = new AccessToken(ACCESS_TOKEN, 3600000);

        when(template.headers()).thenReturn(Collections.emptyMap());

      try (MockedConstruction<ImsService.Builder> ignored = mockConstruction(ImsService.Builder.class,
              (mock, mockContext) -> {
                when(mock.workspace(workspace)).thenReturn(mock);
                when(mock.build()).thenReturn(imsService);
              }
      )) {
        AuthInterceptor interceptor = AuthInterceptor.builder().workspace(workspace).build();
        Field expiresField = AuthInterceptor.class.getDeclaredField("expirationTimeMillis");
        expiresField.setAccessible(true);
        expiresField.set(interceptor, expires.getTimeInMillis());

        Field tokenField = AuthInterceptor.class.getDeclaredField("accessToken");
        tokenField.setAccessible(true);
        tokenField.set(interceptor, token);
        interceptor.apply(template);

        verify(template).header(AUTHORIZATION_HEADER, BEARER_PREFIX + ACCESS_TOKEN);
      }
    }

    @Test
    void getAccessTokenNotSet() {
        Calendar expires = Calendar.getInstance();
        expires.add(Calendar.HOUR, 1);
        AccessToken token = new AccessToken(ACCESS_TOKEN, 3600000);

      try (MockedConstruction<ImsService.Builder> ignored = mockConstruction(ImsService.Builder.class,
              (mock, mockContext) -> {
                when(mock.workspace(workspace)).thenReturn(mock);
                when(mock.build()).thenReturn(imsService);
              }
      )) {
        AuthInterceptor interceptor = AuthInterceptor.builder().workspace(workspace).build();
        doReturn(token).when(imsService).getAccessToken();
        assertEquals(ACCESS_TOKEN, interceptor.getAccessToken());
      }
    }

    @Test
    void getAccessTokenExpired() throws Exception {
      Calendar expires = Calendar.getInstance();
      expires.add(Calendar.HOUR, 1);
      AccessToken token = new AccessToken(ACCESS_TOKEN, 3600000);

      try (MockedConstruction<ImsService.Builder> ignored = mockConstruction(ImsService.Builder.class,
              (mock, mockContext) -> {
                when(mock.workspace(workspace)).thenReturn(mock);
                when(mock.build()).thenReturn(imsService);
              }
      )) {
        AuthInterceptor interceptor = AuthInterceptor.builder().workspace(workspace).build();

        Field expiresField = AuthInterceptor.class.getDeclaredField("expirationTimeMillis");
        expiresField.setAccessible(true);
        expiresField.set(interceptor, 1L);

        doReturn(token).when(imsService).getAccessToken();
        assertEquals(ACCESS_TOKEN, interceptor.getAccessToken());
      }
    }

    @Test
    void fetchAccessToken() {

        when(imsService.getAccessToken()).thenReturn(new AccessToken(ACCESS_TOKEN, 0));

        try (MockedConstruction<ImsService.Builder> ignored = mockConstruction(ImsService.Builder.class,
                (mock, mockContext) -> {
                    when(mock.workspace(workspace)).thenReturn(mock);
                    when(mock.build()).thenReturn(imsService);
                }
        )) {
            AuthInterceptor interceptor = AuthInterceptor.builder().workspace(workspace).build();
            assertNotNull(interceptor.fetchAccessToken());
        }
    }
}
