package com.adobe.aio.event.management.model;

import org.junit.Assert;
import org.junit.Test;

public class EventMetadataModelTest {

  public static final String TEST_EVENT_CODE = "com.adobe.aio.event.management.test.event";
  public static final String TEST_EVENT_DESC = TEST_EVENT_CODE + " description";

  public static EventMetadata.Builder getTestEventMetadataBuilder(){
    return EventMetadata.builder()
        .eventCode(TEST_EVENT_CODE)
        .description(TEST_EVENT_DESC);
  }

  @Test(expected = IllegalArgumentException.class)
  public void invalid(){
    EventMetadata.builder().build();
  }

  @Test
  public void valid(){
    EventMetadata eventMetadata = getTestEventMetadataBuilder().build();
    Assert.assertEquals(TEST_EVENT_CODE,eventMetadata.getEventCode());
    Assert.assertEquals(TEST_EVENT_CODE,eventMetadata.getLabel());
    Assert.assertEquals(TEST_EVENT_DESC,eventMetadata.getDescription());
  }
}
