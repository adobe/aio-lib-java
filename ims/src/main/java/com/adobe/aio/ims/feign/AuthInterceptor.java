package com.adobe.aio.ims.feign;

import com.adobe.aio.ims.ImsService;
import com.adobe.aio.ims.model.AccessToken;
import com.adobe.aio.workspace.Workspace;
import feign.RequestInterceptor;
import feign.RequestTemplate;

import static com.adobe.aio.util.Constants.*;

public class AuthInterceptor implements RequestInterceptor {

  private volatile Long expirationTimeMillis;
  private volatile AccessToken accessToken;
  private final ImsService imsService;

  protected AuthInterceptor (final Workspace workspace) {
    this.imsService = ImsService.builder().workspace(workspace).build();
  }

  @Override
  public void apply(RequestTemplate requestTemplate) {
    applyAuthorization(requestTemplate);
  }

  ImsService getImsService() {
    return this.imsService;
  }

  AccessToken fetchAccessToken() {
    return getImsService().getAccessToken();
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
