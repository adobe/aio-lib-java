package com.adobe.aio.ims.feign;

import com.adobe.aio.auth.Context;
import com.adobe.aio.auth.JwtContext;
import com.adobe.aio.auth.OAuthContext;
import com.adobe.aio.ims.ImsService;
import com.adobe.aio.ims.model.AccessToken;
import com.adobe.aio.workspace.Workspace;
import feign.RequestInterceptor;
import feign.RequestTemplate;

import static com.adobe.aio.util.Constants.*;

public abstract class AuthInterceptor implements RequestInterceptor {

  private volatile Long expirationTimeMillis;
  private volatile AccessToken accessToken;
  private final ImsService imsService;

  protected AuthInterceptor(final ImsService imsService) {
    this.imsService = imsService;
  }

  @Override
  public void apply(RequestTemplate requestTemplate) {
    applyAuthorization(requestTemplate);
  }

  ImsService getImsService() {
    return this.imsService;
  }

  abstract AccessToken fetchAccessToken();

  synchronized String getAccessToken() {
    if (expirationTimeMillis == null || System.currentTimeMillis() >= expirationTimeMillis) {
      updateAccessToken();
    }
    return this.accessToken.getAccessToken();
  }

  private void applyAuthorization(RequestTemplate requestTemplate) {
    // If the request already have an authorization
    if (requestTemplate.headers().containsKey(AUTHORIZATION_HEADER)) {
      return;
    }
    // If first time or of expired, get the token
    if (getAccessToken() != null) {
      requestTemplate.header(AUTHORIZATION_HEADER, BEARER_PREFIX + getAccessToken());
    }
  }

  private synchronized void updateAccessToken() {
    this.accessToken = fetchAccessToken();
    this.expirationTimeMillis = System.currentTimeMillis() + accessToken.getExpiresIn();
    // throw RetryableException and implement Feign Retry ?
  }

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {

    private Context authContext;
    private ImsService imsService;

    private Builder() {
    }

    public Builder workspace(Workspace workspace) {
      this.authContext = workspace.getAuthContext();
      this.imsService = ImsService.builder().workspace(workspace).build();
      return this;
    }

    public AuthInterceptor build() {
      if (authContext instanceof JwtContext) {
        return new JWTAuthInterceptor(imsService);
      } else if (authContext instanceof OAuthContext) {
        return new OAuthInterceptor(imsService);
      }
      throw new IllegalStateException("Unable to find interceptor for AuthContext");
    }
  }


}
