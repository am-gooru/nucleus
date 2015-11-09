package org.gooru.nucleus.bootstrap;

import io.vertx.core.AbstractVerticle;
import org.gooru.nucleus.global.utils.Runner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by ashish on 7/11/15.
 * Dummy Verticle to demonstrate the deployment for the template project
 */
public class DummyVerticle extends AbstractVerticle {

  static final Logger LOG = LoggerFactory.getLogger(DummyVerticle.class);


  @Override
  public void start() throws Exception {

    LOG.trace("Dummy verticle starting...");
    LOG.trace("The main configuration is {}", config().getInteger("http.port"));

  }
}