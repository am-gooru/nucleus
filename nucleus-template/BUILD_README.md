Building Nucleus
==============

## Prerequisites

- Gradle 2.7
- Java 8

## Running Build

The default task is *shadowJar* which is provided by plugin. So running *gradle* from command line will run *shadowJar* and it will create a fat jar in build/libs folder. Note that there is artifact name specified in build file and hence it will take the name from directory housing the project.

Once the far Jar is created, it could be run as any other Java application.

## Running the Jar as an application

Following command could be used

> java -jar build/libs/nucleus-template-0.1-snapshot-fat.jar -Dvertx.logger-delegate-factory-class-name=io.vertx.core.logging.SLF4JLogDelegateFactory

The system variable is needed by *Vertx.io* to support *Logback* instead of *JUL*. To modify the configuration, modify *main/resources/logback.xml*

If a config file is needed, which would be in most of the cases, then pass it with above specified command as

> java -jar build/libs/nucleus-template-0.1-snapshot-fat.jar -Dvertx.logger-delegate-factory-class-name=io.vertx.core.logging.SLF4JLogDelegateFactory -conf src/main/resources/nucleus.json

Note that any options that need to be passed onto Vertx instance need to be passed at command line e.g, worker pool size etc

If the *Service* model for deploying *Verticles* are used instead of hard coding names, then there is a need to have the service specific deployment file on class path. Ideally, it should not be bundled inside the *Jar*. However, "*java -jar*" command does not accept the "*-cp*". In this case, the proper way to run the *Jar* file would be to include class path for both the target *Jar*file and the directory containing the *JSON* deployment files; and run the main class as provided by *Vertx.io*. An example, assuming that *JSON* files are residing as sibling of current project would be

> java -classpath ./:nucleus-template/build/libs/nucleus-template-0.1-snapshot-fat.jar: -Dvertx.logger-delegate-factory-class-name=io.vertx.core.logging.SLF4JLogDelegateFactory io.vertx.core.Launcher -conf nucleus-template/src/main/resources/nucleus.json
