package org.gooru.nucleus.infra;

import io.vertx.core.json.JsonObject;

public interface AuthenticateMediator {

  public JsonObject authenticate(String userName, String password);
}
