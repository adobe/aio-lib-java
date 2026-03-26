package com.adobe.aio.ims.feign;

import com.adobe.aio.ims.AccessTokenProvider;
import com.adobe.aio.ims.ImsService;
import com.adobe.aio.ims.model.AccessToken;
import com.adobe.aio.workspace.Workspace;
import feign.RequestInterceptor;
import feign.RequestTemplate;

import static com.adobe.aio.util.Constants.*;

public class AuthInterceptor implements RequestInterceptor {

  private volatile Long expirationTimeMillis;
  private volatile AccessToken accessToken;
  private final AccessTokenProvider accessTokenProvider;

  protected AuthInterceptor(final Workspace workspace) {
    this(ImsService.builder().workspace(workspace).build()::getAccessToken);
  }

  public AuthInterceptor(final AccessTokenProvider accessTokenProvider) {
    this.accessTokenProvider = accessTokenProvider;
  }

  @Override
  public void apply(RequestTemplate requestTemplate) {
    applyAuthorization(requestTemplate);
  }

  AccessToken fetchAccessToken() {
    return accessTokenProvider.getAccessToken();
  }

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

    private Workspace workspace;

    private Builder() {
    }

    public Builder workspace(Workspace workspace) {
      this.workspace = workspace;
      return this;
    }

    public AuthInterceptor build() {
        return new AuthInterceptor(workspace);
    }
  }


}
