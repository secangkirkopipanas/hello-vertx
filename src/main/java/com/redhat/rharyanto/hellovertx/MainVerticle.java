package com.redhat.rharyanto.hellovertx;

import com.redhat.rharyanto.hellovertx.rest.PersonHandler;
import com.redhat.rharyanto.hellovertx.util.PropertiesUtil;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.healthchecks.HealthCheckHandler;
import io.vertx.ext.healthchecks.HealthChecks;
import io.vertx.ext.web.Router;
import lombok.NoArgsConstructor;

import java.io.IOException;

/**
 * @author <a href="mailto:rharyant@redhat.com">Robertus Lilik Haryanto</a>
 */
@NoArgsConstructor
public class MainVerticle extends AbstractVerticle {

  private static final Logger logger = LoggerFactory.getLogger(MainVerticle.class);

  private JsonObject jsonConfig = null;

  private PersonHandler personHandler = new PersonHandler();
  private HealthCheckHandler healthCheckHandler;

  public MainVerticle(JsonObject jsonConfig) {
    this.jsonConfig = jsonConfig;
  }

  @Override
  public void start(Promise<Void> startPromise) throws Exception {

    // Start the server
    int serverPort = PropertiesUtil.getConfigAsInteger(jsonConfig, "server.port", null);
    vertx.createHttpServer()
      .requestHandler(route())
      .listen(serverPort, http -> {
          if (http.succeeded()) {
            startPromise.complete();
            logger.info("HTTP server started on port " + serverPort);
          } else {
            startPromise.fail(http.cause());
            logger.error("Failed to start HTTP server on port " + serverPort);
          }
      });

    HealthChecks hc = HealthChecks.create(vertx);
    healthCheckHandler = HealthCheckHandler.createWithHealthChecks(hc);
  }

  private Router route() {
    // Create a Router
    Router router = Router.router(vertx);
    router.route("/").handler(rc -> {
      HttpServerResponse response = rc.response();
      response
              .putHeader("Content-Type", "text/html")
              .end("<h1>Hello from my first Vert.x application</h1>");
    });

    router.get("/person/all")
            .produces("application/json")
            .handler(personHandler::getAll)
            .failureHandler(frc -> frc.response().setStatusCode(404).end());

    router.get("/person/:name")
            .produces("application/json")
            .handler(personHandler::get)
            .failureHandler(frc -> frc.response().setStatusCode(404).end());

    router.get("/person/sex/:sex")
            .produces("application/json")
            .handler(personHandler::getBySex)
            .failureHandler(frc -> frc.response().setStatusCode(404).end());

    router.get("/health*").handler(healthCheckHandler);

    logger.info("Exposing " + router.getRoutes().size() + " routes...");
    return router;
  }
}
