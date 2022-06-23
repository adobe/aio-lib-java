/*************************************************************************
 * ADOBE CONFIDENTIAL ___________________
 * <p/>
 * Copyright 2017 Adobe Systems Incorporated All Rights Reserved.
 * <p/>
 * NOTICE: All information contained herein is, and remains the property of Adobe Systems
 * Incorporated and its suppliers, if any. The intellectual and technical concepts contained herein
 * are proprietary to Adobe Systems Incorporated and its suppliers and are protected by all
 * applicable intellectual property laws, including trade secret and copyright laws. Dissemination
 * of this information or reproduction of this material is strictly forbidden unless prior written
 * permission is obtained from Adobe Systems Incorporated.
 **************************************************************************/

package com.adobe.egqa.service;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface PubKeyService {
  @GET("{pubKeyPath}")
  Call<String> getPubKeyFromCDN(@Path(value = "pubKeyPath", encoded = true) String pubKeyPath);
}
