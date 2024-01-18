package com.adobe.aio.event.management.feign;

import static com.adobe.aio.event.management.feign.ConflictException.X_CONFLICTING_ID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import feign.FeignException;
import feign.Request;
import feign.Response;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ConflictExceptionTest {

  @Mock
  private Response response ;

  @Mock
  private Request request;

  @Mock
  private FeignException feignException;

  @BeforeEach
  void beforeeach() {
    when(response.request()).thenReturn(request);
  }

  @Test
  void withNoConflictIdHeader() {
    when(response.headers()).thenReturn(Collections.emptyMap());
    assertEquals(null, new ConflictException(response, feignException).getConflictingId());
  }

  @Test
  void withEmptyConflictIdHeader() {
    Map<String, Collection<String>> headers = new HashMap<>();
    headers.put(X_CONFLICTING_ID, Collections.emptyList());
    when(response.headers()).thenReturn(headers);
    assertEquals(null, new ConflictException(response, feignException).getConflictingId());
  }

  @Test
  void withConflictIdHeader() {
    String conflictingId = "someId";
    Map<String, Collection<String>> headers = new HashMap<>();
    headers.put(X_CONFLICTING_ID, Collections.singletonList(conflictingId));
    when(response.headers()).thenReturn(headers);
    assertEquals(conflictingId, new ConflictException(response, feignException).getConflictingId());
  }

}
