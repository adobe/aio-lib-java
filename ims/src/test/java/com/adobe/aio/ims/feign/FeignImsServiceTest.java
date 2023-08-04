package com.adobe.aio.ims.feign;

import com.adobe.aio.auth.Context;
import com.adobe.aio.auth.JwtContext;
import com.adobe.aio.exception.feign.IOUpstreamError;
import com.adobe.aio.ims.ImsService;
import com.adobe.aio.ims.JwtTokenBuilder;
import com.adobe.aio.ims.model.AccessToken;
import com.adobe.aio.util.Constants;
import com.adobe.aio.workspace.Workspace;
import feign.FeignException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockserver.client.MockServerClient;
import org.mockserver.junit.jupiter.MockServerExtension;
import org.mockserver.model.HttpStatusCode;
import org.mockserver.model.MediaType;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockserver.model.HttpRequest.*;
import static org.mockserver.model.HttpResponse.*;

@ExtendWith({ MockitoExtension.class, MockServerExtension.class })
public class FeignImsServiceTest {

  protected MockServerClient client;

  @Mock(strictness = Mock.Strictness.LENIENT)
  private Workspace workspace;

  @BeforeEach
  void before() {
    when(workspace.getImsUrl()).thenReturn(Constants.IMS_URL);
  }

  @Test
  void getOAuthAccessToken() {
    fail("Not implemented.");
  }

  @Test
  void getJwtInvalidAuthContext() {
    when(workspace.getAuthContext()).thenReturn(mock(Context.class));
    ImsService service = new FeignImsService(workspace);
    Exception ex = assertThrows(IllegalStateException.class, service::getJwtExchangeAccessToken);
    assertEquals("AuthContext in workspace not of type `JwtContext`.", ex.getMessage());
  }

  @Test
  void getJwtInvalidJwtAuthContext() {
    Context context = JwtContext.builder().build();
    when(workspace.getAuthContext()).thenReturn(context);
    ImsService service = new FeignImsService(workspace);
    assertThrows(IllegalStateException.class, service::getJwtExchangeAccessToken);
  }

  @Test
  void getJwtExchangeAccessTokenError(MockServerClient client) {
    final String imsUrl = "http://localhost:" + client.getPort();
    final String apiKey = "API_KEY";
    final String clientSecret = "CLIENT_SECRET";
    final String jwtToken = "JWT_TOKEN_400";
    JwtContext context = mock(JwtContext.class);
    when(workspace.getImsUrl()).thenReturn(imsUrl);
    when(workspace.getAuthContext()).thenReturn(context);
    when(workspace.getApiKey()).thenReturn(apiKey);
    when(context.getClientSecret()).thenReturn(clientSecret);

    client.when(
        request()
            .withMethod("POST")
            .withPath("/ims/exchange/jwt")
            .withHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
            .withBody("client_id=API_KEY&client_secret=CLIENT_SECRET&jwt_token=JWT_TOKEN_400")
    ).respond(
        response().withStatusCode(HttpStatusCode.BAD_REQUEST_400.code())
    );

    try (MockedConstruction<JwtTokenBuilder> ignored = mockConstruction(JwtTokenBuilder.class,
        (mock, mockContext) -> {
          // Have to tell the mocks to return the Desired JWT Token value.
          when(mock.build()).thenReturn(jwtToken);
        }
    )) {
      ImsService service = new FeignImsService(workspace);
      assertThrows(FeignException.class, service::getJwtExchangeAccessToken);
    }
    verify(context).validate();
  }

  @Test
  void getJwtExchangeAccessTokenSuccess(MockServerClient client) {
    final String imsUrl = "http://localhost:" + client.getPort();
    final String apiKey = "API_KEY";
    final String clientSecret = "CLIENT_SECRET";
    final String jwtToken = "JWT_TOKEN_200";
    JwtContext context = mock(JwtContext.class);
    when(workspace.getImsUrl()).thenReturn(imsUrl);
    when(workspace.getAuthContext()).thenReturn(context);
    when(workspace.getApiKey()).thenReturn(apiKey);
    when(context.getClientSecret()).thenReturn(clientSecret);

    client.when(
        request()
            .withMethod("POST")
            .withPath("/ims/exchange/jwt")
            .withHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
            .withBody("client_id=API_KEY&client_secret=CLIENT_SECRET&jwt_token=JWT_TOKEN_200")
    ).respond(
        response()
            .withStatusCode(HttpStatusCode.OK_200.code())
            .withHeader("Content-Type", MediaType.APPLICATION_JSON.toString())
            .withBody("{ \"access_token\": \"ACCESS_TOKEN\", \"token_type\": \"bearer\", \"expires_in\": \"1000\" }")
    );

    try (MockedConstruction<JwtTokenBuilder> ignored = mockConstruction(JwtTokenBuilder.class,
        (mock, mockContext) -> {
          // Have to tell the mocks to return the Desired JWT Token value.
          when(mock.build()).thenReturn(jwtToken);
        }
    )) {
      ImsService service = new FeignImsService(workspace);
      AccessToken token = service.getJwtExchangeAccessToken();
      assertNotNull(token);
      assertEquals("ACCESS_TOKEN", token.getAccessToken());
    }
    verify(context).validate();
  }



  @Test
  void validateInvalidJwtAuthContext() {
    when(workspace.getAuthContext()).thenReturn(mock(Context.class));
    ImsService service = new FeignImsService(workspace);
    assertThrows(IllegalStateException.class, service::getJwtExchangeAccessToken);
  }

  @Test
  void validateAccessToken(MockServerClient client) {
    final String imsUrl = "http://localhost:" + client.getPort();
    final String apiKey = "API_KEY";
    final String accessToken = "ACCESS_TOKEN";
    client.when(
        request()
            .withMethod("POST")
            .withPath("/ims/validate_token/v1")
            .withHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
            .withBody("type=access_token&client_id=API_KEY&token=ACCESS_TOKEN")
    ).respond(
        response()
            .withStatusCode(HttpStatusCode.OK_200.code())
            .withHeader("Content-Type", MediaType.APPLICATION_JSON.toString())
            .withBody("{ \"valid\": \"true\" }")
    );
    JwtContext context = mock(JwtContext.class);
    when(workspace.getAuthContext()).thenReturn(context);
    when(workspace.getImsUrl()).thenReturn(imsUrl);
    when(workspace.getApiKey()).thenReturn(apiKey);
    ImsService service = new FeignImsService(workspace);
    assertTrue(service.validateAccessToken(accessToken));
  }
}
