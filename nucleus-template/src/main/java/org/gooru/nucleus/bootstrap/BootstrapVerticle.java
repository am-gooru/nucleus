package org.gooru.nucleus.bootstrap;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import org.gooru.nucleus.global.constants.ConfigConstants;
import org.gooru.nucleus.global.constants.EndpointsConstants;
import org.gooru.nucleus.global.utils.Runner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

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
    final HttpServer httpServer = vertx.createHttpServer();

    final Router router = Router.router(vertx);
    initializeRoutes(router);
    // If the port is not present in configuration then we end up
    // throwing as we are casting it to int. This is what we want.
    final int port = config().getInteger(ConfigConstants.HTTP_PORT);
    LOG.info("Http server starting on port {}", port);
    httpServer.requestHandler(router::accept).listen(port);
    deployVerticles();

  }

  /*
   * Deploy the verticles.
   */
  private void deployVerticles() {
    LOG.info("Starting to deploy other verticles...");

    final JsonArray verticlesList = config().getJsonArray(ConfigConstants.VERTICLES_DEPLOY_LIST);
    final CompletableFuture<Void>[] resultFutures = new CompletableFuture[verticlesList.size()];

    for (int i = 0; i < verticlesList.size(); i++) {
      final String verticleName = verticlesList.getString(i);
      // Note that verticle name should be starting with "service:" prefix
      if (verticleName != null && !verticleName.isEmpty()) {
        LOG.info("Starting verticle: {}", verticleName);

        final CompletableFuture<Void> deployFuture = new CompletableFuture<>();
        resultFutures[i] = deployFuture;

        vertx.deployVerticle(verticleName, res -> {
          if (res.succeeded()) {
            deployFuture.complete(null);
            LOG.info("Deployment id is: " + res.result() + " for verticle: " + verticleName);
          } else {
            deployFuture.completeExceptionally(res.cause());
            LOG.info("Deployment failed!");
          }
        });
      } else {
        LOG.error("Invalid verticle name specified in configuration. Aborting");
        throw new IllegalArgumentException("Invalid verticle name specified in configuration. Aborting.");
      }
    }
    vertx.executeBlocking(future -> {
      future.complete();
      try {
        CompletableFuture.allOf(resultFutures).join();
      } catch (CompletionException e) {
        e.printStackTrace();
        throw e;
      }

    }, blockingResult -> {
      if (blockingResult.succeeded()) {
        LOG.info("Deployment successful");
      } else {
        LOG.error("Error deploying verticles. Shutting down.");
      }
    });

  }

  private void initializeRoutes(Router router) {

    EventBus eb = vertx.eventBus();

    router.route("/").handler(routingContext -> {

      eb.send(EndpointsConstants.DUMMY_ENDPOINT, "ping!", reply -> {
        if (reply.succeeded()) {
          LOG.info("Received reply " + reply.result().body());
          routingContext.response().end(reply.result().body().toString());
        } else {
          LOG.info("No reply");
        }
      });
    });
  }
}
