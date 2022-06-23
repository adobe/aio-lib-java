/*
 * Copyright 2017 Adobe. All rights reserved.
 * This file is licensed to you under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License. You may obtain a copy
 * of the License at http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under
 * the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR REPRESENTATIONS
 * OF ANY KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package com.adobe.aio.retrofit;

import com.adobe.aio.util.JacksonUtil;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import retrofit2.Converter.Factory;
import retrofit2.Retrofit;
import retrofit2.Retrofit.Builder;
import retrofit2.converter.jackson.JacksonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;


public class RetrofitUtility {

  /**
   * Scalars converter supports converting strings and both primitives and their boxed types to
   * text/plain bodies.
   */
  private static Builder getRetrofitBuilderWithScalarsConverter(String url,
      int readTimeoutInSeconds) {
    Builder builder = new Builder();
    OkHttpClient okHttpClient = new OkHttpClient().newBuilder().
        readTimeout(readTimeoutInSeconds, TimeUnit.SECONDS).build();
    builder.baseUrl(url);
    builder.addConverterFactory(ScalarsConverterFactory.create());
    builder.client(okHttpClient);
    return builder;
  }

  private static Builder getRetrofitBuilder(String url, int readTimeoutInSeconds,
      Factory converterFactory) {
    return getRetrofitBuilderWithScalarsConverter(url, readTimeoutInSeconds)
        .addConverterFactory(converterFactory);
  }

  /**
   * @return Retrofit with a jackson converter
   */
  public static Retrofit getRetrofitWithJacksonConverterFactory(String url,
      int readTimeoutInSeconds) {
    return getRetrofitBuilder(url, readTimeoutInSeconds,
        JacksonConverterFactory.create(JacksonUtil.DEFAULT_OBJECT_MAPPER)).build();
  }
}
