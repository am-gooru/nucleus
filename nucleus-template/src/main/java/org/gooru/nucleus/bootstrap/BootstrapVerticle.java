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


  }
}
