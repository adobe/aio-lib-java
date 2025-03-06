package com.adobe.aio.cloudevents;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import java.io.IOException;
import java.time.DateTimeException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class ZonedDateTimeDeserializer extends JsonDeserializer<ZonedDateTime> {

  @Override
  public ZonedDateTime deserialize(
      JsonParser jsonParser, DeserializationContext deserializationContext)
      throws IOException, JacksonException {
    try {
      return ZonedDateTime.parse(jsonParser.getText(), DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    } catch (DateTimeException e) {
      throw new IllegalArgumentException("could not parse");
    }
  }
}
