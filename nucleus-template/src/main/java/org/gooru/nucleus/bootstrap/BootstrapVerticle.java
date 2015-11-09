package org.gooru.nucleus.bootstrap;

import io.vertx.core.AbstractVerticle;
import org.gooru.nucleus.global.utils.Runner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by ashish on 6/11/15.
 * This class is responsible to bootstrap the application.
 * To start the application, it does two things:
 * First it starts the verticles which need to be deployed based on the configuration.
 * Second, once that is done, it spawns a HTTP server and continues with main even loop.
 */
public class BootstrapVerticle extends AbstractVerticle {

  static final Logger LOG = LoggerFactory.getLogger(BootstrapVerticle.class);

  // Convenience method to enable running from IDE
  public static void main(String[] args) {
    Runner.runVerticle(BootstrapVerticle.class);
  }

  @Override
  public void start() throws Exception {

    LOG.trace("Main verticle starting, trying to deploy other verticles...");
    LOG.trace("The main configuration is {}", config().getInteger("http.port"));

    vertx.deployVerticle("service:org.gooru.nucleus.service.Dummy");
/*
    // Different ways of deploying verticles

    // Deploy a verticle and don't wait for it to start
    vertx.deployVerticle("io.vertx.example.core.verticle.deploy.OtherVerticle");

    // Deploy another instance and  want for it to start
    vertx.deployVerticle("io.vertx.example.core.verticle.deploy.OtherVerticle", res -> {
      if (res.succeeded()) {

        String deploymentID = res.result();

        System.out.println("Other verticle deployed ok, deploymentID = " + deploymentID);

        // You can also explicitly undeploy a verticle deployment.
        // Note that this is usually unnecessary as any verticles deployed by a verticle will be automatically
        // undeployed when the parent verticle is undeployed

        vertx.undeploy(deploymentID, res2 -> {
          if (res2.succeeded()) {
            System.out.println("Undeployed ok!");
          } else {
            res2.cause().printStackTrace();
          }
        });

      } else {
        res.cause().printStackTrace();
      }
    });

    // Deploy specifying some config
    JsonObject config = new JsonObject().put("foo", "bar");
    vertx.deployVerticle("io.vertx.example.core.verticle.deploy.OtherVerticle", new DeploymentOptions().setConfig(config));

    // Deploy 10 instances
    vertx.deployVerticle("io.vertx.example.core.verticle.deploy.OtherVerticle", new DeploymentOptions().setInstances(10));

    // Deploy it as a worker verticle
    vertx.deployVerticle("io.vertx.example.core.verticle.deploy.OtherVerticle", new DeploymentOptions().setWorker(true));
*/


  }
}
