package com.redhat.rharyanto.hellovertx;

import io.netty.util.internal.logging.InternalLoggerFactory;
import io.netty.util.internal.logging.Log4J2LoggerFactory;
import io.vertx.config.ConfigRetriever;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;

/**
 * @author Robertus Lilik Haryanto <a href="mailto:rharyant@redhat.com" />
 */
public class MainVerticle extends AbstractVerticle {

  private static final Logger logger = LoggerFactory.getLogger(MainVerticle.class);

  @Override
  public void start(Promise<Void> startPromise) throws Exception {

    InternalLoggerFactory.setDefaultFactory(Log4J2LoggerFactory.INSTANCE);

    ConfigRetriever retriever = ConfigRetriever.create(vertx);

    logger.debug("Starting...");

    retriever.getConfig(json -> {
      int serverPort = PropertiesUtil.getConfigAsInteger(json.result(), "server.port", null);

      vertx.createHttpServer().requestHandler(req -> {
        req.response()
          .putHeader("Content-Type", "text/html")
          .end("<h1>Hello from my first Vert.x application</h1>");
      }).listen(serverPort, http -> {
        if (http.succeeded()) {
          startPromise.complete();
          logger.info("HTTP server started on port " + serverPort);
        } else {
          startPromise.fail(http.cause());
        }
      });
    });
  }
}
