package com.redhat.rharyanto.hellovertx;

import io.vertx.config.ConfigRetriever;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @author <a href="mailto:rharyant@redhat.com">Robertus Lilik Haryanto</a>
 */
@ExtendWith(VertxExtension.class)
public class TestMainVerticle {

  private Vertx vertx = Vertx.vertx();
  private JsonObject jsonConfig = null;
  private ConfigRetriever configRetriever = ConfigRetriever.create(vertx);

  @BeforeEach
  void deploy_verticle(Vertx vertx, VertxTestContext testContext) {
    configRetriever.getConfig(json -> {
      jsonConfig = json.result();
      vertx.deployVerticle(new MainVerticle(jsonConfig), testContext.succeeding(id -> testContext.completeNow()));
    });
  }

  @Test
  void verticle_deployed(Vertx vertx, VertxTestContext testContext) throws Throwable {
    testContext.completeNow();
  }
}
