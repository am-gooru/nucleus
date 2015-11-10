package org.gooru.nucleus.bootstrap;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import org.gooru.nucleus.global.constants.ConfigConstants;
import org.gooru.nucleus.global.utils.Runner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by ashish on 6/11/15.
 * This class is responsible to bootstrap the application.
 * To start the application, it does three things:
 * First it starts the verticles which need to be deployed based on the configuration.
 * Second, once that is done, it spawns a HTTP server and continues with main even loop.
 * Third, mount the subrouters and the main router
 */
public class BootstrapVerticle extends AbstractVerticle {

  static final Logger LOG = LoggerFactory.getLogger(BootstrapVerticle.class);

  // Convenience method to enable running from IDE
  public static void main(String[] args) {
    Runner.runVerticle(BootstrapVerticle.class);
  }

  @Override
  public void start() throws Exception {
    HttpServer httpServer = vertx.createHttpServer();

    Router router = Router.router(vertx);
    initializeRoutes(router);
    // If the port is not present in configuration then we end up
    // throwing as we are casting it to int. This is what we want.
    int port = config().getInteger(ConfigConstants.HTTP_PORT);
    LOG.info("Http server starting on port {}", port);
    httpServer.requestHandler(router::accept).listen(port);

  }

  /*
   * Deploy the verticles.
   */
  private void deployVerticles() {
    LOG.info("Starting to deploy other verticles...");

    JsonArray verticlesList = config().getJsonArray(ConfigConstants.VERTICLES_DEPLOY_LIST);

    for (int i = 0; i < verticlesList.size(); i++) {
      String verticleName = verticlesList.getString(i);

      if (verticleName != null) {
        LOG.info("Starting verticle: {}", verticleName);
        vertx.deployVerticle(verticleName);
      } else {
        LOG.error("Invalid verticle name specified in configuration. Aborting");
        throw new IllegalArgumentException("Invalid verticle name specified in configuration. Aborting.");
      }
    }

  }

  private void initializeRoutes(Router router) {
    router.route("/").handler(routingContext -> {
      JsonObject result = new JsonObject().put("Organisation", "gooru.org").put("Product", "nucleus").put("purpose", "api")
                                          .put("mission", "Honor the human right to education");
      routingContext.response().end(result.toString());
    });
  }
}
