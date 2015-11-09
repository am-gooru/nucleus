package org.gooru.nucleus.global.utils;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;

import java.io.File;
import java.io.IOException;
import java.util.function.Consumer;

/**
 * Utility class to help run the verticles from the IDE. Should not be used by the project or production code
 * Added by ashish on 7/11/15.
 */
public class Runner {
  private static final String PROJECT_DIR = "nucleus-template";
  private static final String PROJECT_SRC_JAVA_DIR = PROJECT_DIR + "/src/main/java/";

  public static void runVerticle(Class clazz) {

    runVerticle(PROJECT_SRC_JAVA_DIR, clazz, new VertxOptions().setClustered(false), null);
  }

  private static void runVerticle(String projectSrcJavaDir, Class clazz, VertxOptions vertxOptions, DeploymentOptions deploymentOptions) {
    runVerticle(projectSrcJavaDir + clazz.getPackage().getName().replace(".", "/"), clazz.getName(), vertxOptions, deploymentOptions);
  }

  public static void runVerticle(String projectSrcJavaDir, String verticleID, VertxOptions options, DeploymentOptions deploymentOptions) {
    if (options == null) {
      options = new VertxOptions();
    }

    try {
      File current = new File(".").getCanonicalFile();
      if (projectSrcJavaDir.startsWith(current.getName()) && !projectSrcJavaDir.equals(current.getName())) {
        projectSrcJavaDir = projectSrcJavaDir.substring(current.getName().length() + 1);
      }
    } catch (IOException e) {
      // Ignore it.
    }

    System.setProperty("vertx.cwd", projectSrcJavaDir);
    Consumer<Vertx> runner = vertx -> {
      try {
        if (deploymentOptions != null) {
          vertx.deployVerticle(verticleID, deploymentOptions);
        } else {
          vertx.deployVerticle(verticleID);
        }
      } catch (Throwable t) {
        t.printStackTrace();
      }
    };

    if (options.isClustered()) {
      Vertx.clusteredVertx(options, res -> {
        if (res.succeeded()) {
          Vertx vertx = res.result();
          runner.accept(vertx);
        } else {
          res.cause().printStackTrace();
        }
      });
    } else {
      Vertx vertx = Vertx.vertx(options);
      runner.accept(vertx);
    }
  }
}
