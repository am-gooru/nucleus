package org.gooru.nucleus.infra;

import io.vertx.core.json.JsonObject;

public interface UnitMediator {

  public JsonObject createUnit(JsonObject jsonData);

  public JsonObject copyUnitToCourse(String courseId, JsonObject jsonData);

  public JsonObject updateUnit(JsonObject jsonData);

  public JsonObject getUnitById(String courseId, String unitId);

  public JsonObject deleteUnit(String courseId, String unitId);

  public JsonObject reorderLessonsInUnit(String courseId, String unitId, JsonObject jsonData);

}
