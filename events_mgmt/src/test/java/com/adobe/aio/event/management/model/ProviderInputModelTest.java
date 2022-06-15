package com.adobe.aio.event.management.model;

import static com.adobe.aio.util.Constants.CUSTOM_EVENTS_PROVIDER_METADATA_ID;

import org.junit.Assert;
import org.junit.Test;

public class ProviderInputModelTest {

  public static final String TEST_EVENT_PROVIDER_LABEL = "com.adobe.aio.event.management.test";
  public static final String TEST_EVENT_PROVIDER_DESC = TEST_EVENT_PROVIDER_LABEL + " description";
  
  public static ProviderInputModel.Builder getProviderInputModelBuilder(){
    return ProviderInputModel.builder()
        .label(TEST_EVENT_PROVIDER_LABEL)
        .description(TEST_EVENT_PROVIDER_DESC)
        .docsUrl("https://github.com/adobe/aio-lib-java");
  }

  @Test(expected = IllegalArgumentException.class)
  public void invalid(){
    ProviderInputModel.builder().build();
  }

  @Test
  public void valid(){
    ProviderInputModel providerInputModel = getProviderInputModelBuilder().build();
    Assert.assertEquals(TEST_EVENT_PROVIDER_DESC,providerInputModel.getDescription());
    Assert.assertEquals(TEST_EVENT_PROVIDER_LABEL,providerInputModel.getLabel());
    Assert.assertEquals(CUSTOM_EVENTS_PROVIDER_METADATA_ID,providerInputModel.getProviderMetadataId());
  }
}
