package org.gooru.nucleus.bootstrap;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;
import org.gooru.nucleus.global.constants.EndpointsConstants;
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

    LOG.info("Dummy verticle starting...");

    EventBus eb = vertx.eventBus();

    eb.consumer(EndpointsConstants.DUMMY_ENDPOINT, message -> {

      LOG.info("Received message: {}", message.body());

      final JsonObject result = new JsonObject().put("Organisation", "gooru.org").put("Product", "nucleus").put("purpose", "api")
                                                .put("mission", "Honor the human right to education");

      // Now send back reply
      message.reply(result);
    });

    LOG.info("Receiver ready!");
  }

}