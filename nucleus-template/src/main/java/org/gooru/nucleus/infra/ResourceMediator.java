package org.gooru.nucleus.infra;

import io.vertx.core.json.JsonObject;

public interface ResourceMediator {

  public JsonObject getResourceById(String resourceId);

  public JsonObject createResource(JsonObject jsonData);

  public JsonObject updateResource(JsonObject jsonData);

  public JsonObject updateResourceMetadata(JsonObject jsonData);
}
