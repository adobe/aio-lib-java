package com.adobe.aio.event.webhook.feign;

import java.io.File;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PublicKey;

import javax.crypto.KeyGenerator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockserver.client.MockServerClient;
import org.mockserver.junit.jupiter.MockServerExtension;
import shaded_package.org.apache.commons.codec.binary.Base64;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockserver.model.HttpRequest.*;
import static org.mockserver.model.HttpResponse.*;

@ExtendWith(MockServerExtension.class)
public class FeignPubKeyServiceTest {

  @Test
  public void response200(MockServerClient client) throws Exception {
    final KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
    kpg.initialize(2048);
    KeyPair kp = kpg.generateKeyPair();
    final PublicKey publicKey = kp.getPublic();
    final String expected = Base64.encodeBase64String(publicKey.getEncoded());

    final String url = "http://localhost:" + client.getPort();
    final String keyPath = "/junit/pubkey.pem";

    client
        .when(request().withMethod("GET").withPath(keyPath))
        .respond(response().withStatusCode(200).withBody(expected));

    PublicKey actual =  new FeignPubKeyService(url).getAioEventsPublicKey(keyPath);
    assertNotNull(actual);
    assertEquals(expected, Base64.encodeBase64String(actual.getEncoded()));
  }

  @Test
  public void response404(MockServerClient client) {
    final String url = "http://localhost:" + client.getPort();
    final FeignPubKeyService service = new FeignPubKeyService(url);

    final String keyPath = "/junit/pubkey-not-found.pem";

    client
        .when(request().withMethod("GET").withPath(keyPath))
        .respond(response().withStatusCode(404));

    assertThrows(IllegalStateException.class, () -> service.getAioEventsPublicKey(keyPath));
  }
}
