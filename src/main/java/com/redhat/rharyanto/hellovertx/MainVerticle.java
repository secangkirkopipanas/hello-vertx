package com.redhat.rharyanto.hellovertx;

import com.hazelcast.core.HazelcastInstance;
import com.redhat.rharyanto.hellovertx.rest.PersonHandler;
import com.redhat.rharyanto.hellovertx.util.PropertiesUtil;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.healthchecks.HealthCheckHandler;
import io.vertx.ext.healthchecks.Status;
import io.vertx.ext.web.Router;
import lombok.NoArgsConstructor;

/**
 * @author <a href="mailto:rharyant@redhat.com">Robertus Lilik Haryanto</a>
 */
@NoArgsConstructor
public class MainVerticle extends AbstractVerticle {

  private static final Logger logger = LoggerFactory.getLogger(MainVerticle.class);

  private JsonObject jsonConfig = null;
  private HazelcastInstance hzInstance = null;

  private PersonHandler personHandler = null;
  private HealthCheckHandler healthCheckHandler;

  public MainVerticle(JsonObject jsonConfig) {
    this.jsonConfig = jsonConfig;
    this.personHandler = new PersonHandler();
  }

  public MainVerticle(JsonObject jsonConfig, HazelcastInstance hzInstance) {
    this.jsonConfig = jsonConfig;
    this.hzInstance = hzInstance;
    this.personHandler = new PersonHandler(this.hzInstance);
  }

  @Override
  public void start(Promise<Void> startPromise) throws Exception {

    // Health check handler
    healthCheckHandler = HealthCheckHandler.create(vertx);

    // Additional health check handler, depends on your requirements
    healthCheckHandler.register("hazelcast", promise -> {
      String hzMode = (System.getenv("HZ_MODE") != null) ? System.getenv("HZ_MODE") : "dev";
      promise.complete(Status.OK(new JsonObject()
              .put("mode", hzMode)
              .put("file", System.getProperty("vertx.hazelcast.config"))
      ));
    });

    // Start the HTTP server
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
  }

  /**
   * Main router
   * @return Router router
   */
  private Router route() {
    // Create a Router
    Router router = Router.router(vertx);
    router.route("/").handler(rc -> {
      HttpServerResponse response = rc.response();
      response
              .putHeader("Content-Type", "text/html")
              .end("<h1>Hello from my first Vert.x application</h1>");
    });

    router.get("/person")
            .produces("application/json")
            .handler(personHandler::getAll)
            .failureHandler(frc -> frc.response().setStatusCode(404).end());

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

    router.get("/person/country/:country")
            .produces("application/json")
            .handler(personHandler::getByCountry)
            .failureHandler(frc -> frc.response().setStatusCode(404).end());

    router.get("/health").handler(healthCheckHandler);

    logger.info("Exposing " + router.getRoutes().size() + " routes...");
    return router;
  }

  @Override
  public void stop(Promise<Void> startPromise) throws Exception {
    vertx.close();
    startPromise.complete();
  }
}
