Nucleus Template
================

This is the template project for Project Nucleus, for the components which will be based on *Vertx.io*. 

Instead of bootstrapping all Nucleus components from scratch, one should start here. Cloning this project will provide a starter template.

This project contains:

 - Initial package structure
 - Initial build dependencies
 - Initial plugin for fat jar generation, and service factory
 - Code Style

To understand build related stuff, take a look at **BUILD_README.md**.

The code styles are contained in *nucleus-settings.jar*  file. This file could be imported as settings in *Intellij Idea*.

This project also contains a *Verticle* named *BootstrapVerticle*. This is an outline of how the main *Verticle* would look like. 

To start another component,  copy this project and rename it. There are few code files like *DummyVerticle* which are present there to show how the deployment works via *BootstrapVerticle*. If this is not needed, it should be deleted or renamed. Once the unwanted Java files are removed, the new component structure is all set. 
