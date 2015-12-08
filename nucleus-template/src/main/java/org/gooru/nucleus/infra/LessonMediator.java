package org.gooru.nucleus.infra;

import io.vertx.core.json.JsonObject;

public interface LessonMediator {

  public JsonObject createLesson(String courseId, String unitId, JsonObject jsonData);
  
  public JsonObject copyLessonToUnit(String courseId, String unitId, JsonObject jsonData);
  
  public JsonObject updateLesson(String courseId, String unitId, String lessonId, JsonObject jsonData);
  
  public JsonObject getLessonById(String courseId, String unitId, String lessonId);
  
  public JsonObject deleteLesson(String courseId, String unitId, String lessonId);
  
  public JsonObject reorderCollectionsAndAssessmentsInLesson(String courseId, String unitId, String lessonId, JsonObject jsonData);
}
